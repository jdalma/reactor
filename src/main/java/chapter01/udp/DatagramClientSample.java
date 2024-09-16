package chapter01.udp;

import java.io.IOException;
import java.net.*;
import java.util.Date;

public class DatagramClientSample {

    public static void main(String[] args) {
        for (int i = 0 ; i < 3 ; i++) {
            sendDatagramData("I liked UDP " + new Date());
        }
        sendDatagramData("EXIT");
    }

    /**
     * UDP 통신은 서버가 데이터를 받을 준비가 되어 있지 않더라도 클라이언트에서는 아무런 오류를 내지 않고 데이터를 전송한다.
     * @param data
     */
    private static void sendDatagramData(String data) {
        try (final DatagramSocket client = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName("127.0.0.1");
            final byte[] buffer = data.getBytes();
            final DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, address, 9999);
            client.send(packet);
            System.out.println("Client: Send data [" + data  + "]");
            Thread.sleep(1000);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
