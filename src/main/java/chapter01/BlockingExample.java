package chapter01;

import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingExample {

    public static void main(String[] args) throws IOException {
        final int portNumber = 8080;
        // 1. 새로운 Server Socket이 지정된 포트에서 연결 요청을 수신한다.
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // 2. accept호출은 연결될 때까지 진행을 블로킹한다.
        Socket clientSocket = serverSocket.accept();

        // 3. 연결 되어 생성된 소켓을 통해 스트림 객체를 생성한다.
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String request;
        String response;

        while ((request = in.readLine()) != null) {
            if ("Done".equals(request)) {
                break;
            }
            response = processRequest(request);
            System.out.println(response);
        }
    }

    private static String processRequest(String request) {
        return "response";
    }
}
