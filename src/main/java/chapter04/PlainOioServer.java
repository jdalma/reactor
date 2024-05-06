package chapter04;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlainOioServer {
    public void serve(int port) throws IOException {
        try (ServerSocket socket = new ServerSocket(port)) {    // 서버를 지정된 포트로 바인딩
            while (true) {
                final Socket clientSocket = socket.accept();    // 연결이 이루어질 때까지 차단됩
                System.out.println("Accepted connection from " + clientSocket);

                // 해당 연결을 처리할 스레드 생성
                new Thread(() -> {
                    try (clientSocket) {
                        OutputStream out = clientSocket.getOutputStream();
                        out.write("Hi!\r\n".getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // ignore on close
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
