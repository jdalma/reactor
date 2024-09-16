package chapter01.tcp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class SocketClientSample {

    public static void main(String[] args) {
        for (int i = 0; i < 10 ; i++) {
            sendData(i + "번째 " + new Date());
        }
        sendData("EXIT");
    }

    private static void sendData(String data) {
        try {
            final Socket socket = new Socket("127.0.0.1", 9999);
            System.out.println("Client connect Status = " + socket.isConnected());
            Thread.sleep(1000);
            OutputStream stream = socket.getOutputStream();
            BufferedOutputStream out = new BufferedOutputStream(stream);
            byte[] bytes = data.getBytes();
            out.write(bytes);
            System.out.println("Client : send data");
            out.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
