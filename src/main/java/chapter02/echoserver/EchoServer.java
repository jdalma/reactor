package chapter02.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() +
                " <port>"
            );
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 1. EventLoopGroup을 생성
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 2. ServerBootStrap을 생성
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                // 3. Nio 전송 채널을 사용하도록 지정
                .channel(NioServerSocketChannel.class)
                // 4. 지정된 포트를 이용해 소켓 주소를 설정
                .localAddress(new InetSocketAddress(port))
                // 5. EchoServerHandler 하나를 채널의 ChannelPipeline으로 추가
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        // serverHandler는 @Shared 이기 때문에 재사용 가능하다.
                        ch.pipeline().addLast(serverHandler);
                    }
                });

            // 6. 서버를 비동기식으로 바인딩, sync()는 바인딩이 완료되기를 대기한다.
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + channelFuture.channel().localAddress());

            // 7. 채널의 CloseFuture를 얻고 완료될 때까지 현재 스레드를 블로킹
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 8. EventLoopGroup을 종료하고 모든 리소스를 해제
            group.shutdownGracefully().sync();
        }
    }
}
