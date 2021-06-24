package practice3;


import homework2.api.ISender;
import homework2.impl.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class StoreServerUDP extends Thread {
    private DatagramSocket socket;
    private volatile boolean running;
    private Processor processor;
    private Decryptor decryptor;

    public StoreServerUDP(int port, String key) throws SocketException {
        socket = new DatagramSocket(port);
        processor = new Processor(new Sender(new Encryptor(key)));
        decryptor = new Decryptor(key);
    }

    public void run() {
        super.run();
        running = true;

        while (running) {
            try {
                byte[] requestBytes = new byte[Util.BUFFER_SIZE];
                DatagramPacket datagramPacket = new DatagramPacket(requestBytes, requestBytes.length);
                socket.receive(datagramPacket);
                Packet request = decryptor.decrypt(requestBytes);
                request.setClientAddress(datagramPacket.getAddress());
                request.setClientPort(datagramPacket.getPort());
                processor.process(request);
            } catch (IOException e) {
                System.out.println("PacketTestPR1 wasn't send!");
            }
        }
        socket.close();
    }


    public class Sender implements ISender {
        Encryptor encryptor;

        public Sender(Encryptor encryptor) {
            this.encryptor = encryptor;
        }

        @Override
        public void sendMessage(Packet packet, InetAddress target) {
            try {
                byte[] buf = encryptor.encrypt(packet);
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, target, packet.getClientPort());
                socket.send(datagramPacket);
                System.out.println("UDP Server send message: " + packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        running = false;
    }
}
