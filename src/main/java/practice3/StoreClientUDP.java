package practice3;

import homework2.impl.Decryptor;
import homework2.impl.Encryptor;
import homework2.impl.Packet;
import homework2.impl.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class StoreClientUDP {

    private DatagramSocket socket;
    private Decryptor decryptor;
    private Encryptor encryptor;

    public StoreClientUDP(int port, String key) {
        decryptor = new Decryptor(key);
        encryptor = new Encryptor(key);
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public Packet sendMessage(byte[] msg, InetAddress address, int port) {
        DatagramPacket datagramPacket
                = new DatagramPacket(msg, msg.length, address, port);
        try {
            socket.send(datagramPacket);
            System.out.println();
            byte[] responseBytes = new byte[Util.BUFFER_SIZE];
            datagramPacket = new DatagramPacket(responseBytes, responseBytes.length);

            socket.receive(datagramPacket);
            Packet response = decryptor.decrypt(datagramPacket.getData());
            System.out.println("UDP client: Received: " + response);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        socket.close();
    }
}
