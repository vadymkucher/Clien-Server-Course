package practice3;

import homework2.impl.Packet;
import homework2.impl.Processor;

import java.io.IOException;
import java.net.Socket;

public class TCPSocketThread extends Thread {
    private Socket clientSocket;
    private volatile boolean running;
    private Processor processor;

    public TCPSocketThread(Socket clientSocket, Processor processor) {
        this.clientSocket = clientSocket;
        this.processor = processor;

    }

    @Override
    public void run() {
        super.run();
        running = true;
        while (running) {
            byte[] headerBytes = new byte[Packet.getHeaderLength()];
            try {
                clientSocket.getInputStream().read(headerBytes);
                Packet header = Packet.decodeHeader(headerBytes);
                byte[] bodyBytes = new byte[Packet.getBodyLength(header)];
                clientSocket.getInputStream().read(bodyBytes);
                Packet packet = Packet.decode(header, bodyBytes);
                packet.setClientAddress(clientSocket.getInetAddress());
                processor.process(packet);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
    }

    public void send(byte[] packet) {
        try {
            clientSocket.getOutputStream().write(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            running = false;
        }

    }
}
