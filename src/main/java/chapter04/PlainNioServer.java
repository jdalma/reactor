package chapter04;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {
    public void serve(int port) throws IOException {
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket serverSocket = serverChannel.socket(); // 현재 채널과 연관된 서버 소켓을 검색한다.
        InetSocketAddress address = new InetSocketAddress(port);

        serverSocket.bind(address); // 서버를 선택된 포트로 바인딩
        Selector selector = Selector.open();    // 채널을 처리할 셀렉터를 연다.

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);   // 연결을 수락할 ServerSocket을 셀렉터에 등록
        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
        while (true) {
            try {
                selector.select();  // 처리할 새로운 이벤트를 기다리고 다음 들어오는 이벤트까지 블로킹
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();  // 이벤트를 수신한 모든 SelectionKey 인스턴스를 얻음
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {   // 이 키의 채널이 새 소켓 연결을 수락할 준비가 되었는지 확인
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept(); // 이 채널의 소켓에 대한 연결을 허용
                        client.configureBlocking(false);

                        // 클라이언트를 수락하고 셀렉터에 등록
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted connection from " + client);
                    }
                    if (key.isWritable()) {     // 이 키의 채널이 쓸 준비가 되었는지 확인
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {    // 연결된 클라이언트로 데이터를 출력
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        throw new RuntimeException(cex);
                    }
                }
            }
        }
    }
}
