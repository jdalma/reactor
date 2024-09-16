package chapter01.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramServerSample {

    private static final int BUFFER_LENGTH = 256;

    public static void main(String[] args) {
        try (final DatagramSocket server = new DatagramSocket(9999)) {
            final byte[] buffer = new byte[BUFFER_LENGTH];
            final DatagramPacket packet = new DatagramPacket(buffer, BUFFER_LENGTH);
            while (true) {
                System.out.println("Server: Waiting for request");
                server.receive(packet);
                System.out.println("Server: Received, Data length = " + packet.getLength());
                final String data = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Server: Received, Data = " + data);
                if (data.equals("EXIT")) {
                    System.out.println("Stop DatagramServer");
                    break;
                }
                System.out.println("-----------------");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
