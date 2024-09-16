package chapter01.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerSample {

    public static void main(String[] args) {
        try (
            final ServerSocket server = new ServerSocket(9999)
        ) {
            while (true) {
                final Socket client = server.accept();
                System.out.println("Server: Accepted, Waiting for data ," + client.isConnected());
                final InputStream inputStream = client.getInputStream();
                final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String data;

                final StringBuilder receivedData = new StringBuilder();
                while ((data = in.readLine()) != null) {
                    receivedData.append(data);
                }
                System.out.println("Received data : " + receivedData);
                in.close();
                inputStream.close();
                client.close();
                if ("EXIT".contentEquals(receivedData)) {
                    System.out.println("Stop SocketServer");
                    break;
                }
                System.out.println("----------------");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
