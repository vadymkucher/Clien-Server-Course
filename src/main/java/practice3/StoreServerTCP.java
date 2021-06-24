package practice3;

import homework2.api.ISender;
import homework2.impl.Encryptor;
import homework2.impl.Packet;
import homework2.impl.Processor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StoreServerTCP extends Thread {
	private ServerSocket serverSocket;
	private Map<InetAddress, TCPSocketThread> socketThreads = new ConcurrentHashMap<>();
	private ISender sender;
	private volatile boolean running;
	private Processor processor;

	public StoreServerTCP(int port, String key) throws IOException {
		serverSocket = new ServerSocket(port);
		sender = new Sender(new Encryptor(key));
		processor = new Processor(sender);
	}

	public void run()  {
		running = true;
		while(running) {
			try {
				Socket clientSocket = serverSocket.accept();
				TCPSocketThread socketThread = new TCPSocketThread(clientSocket, processor);
				InetAddress inetAddress = clientSocket.getInetAddress();
				socketThreads.put(inetAddress, socketThread);
				socketThread.start();
			} catch (IOException ignore) {
			}
		}
	}

	public void close() throws IOException {
		running = false;
		for (TCPSocketThread tcpSocketThread : socketThreads.values())
			tcpSocketThread.close();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public class Sender implements ISender {
		Encryptor encryptor;

		public Sender(Encryptor encryptor) {
			this.encryptor = encryptor;
		}

		@Override
		public void sendMessage(Packet packet, InetAddress target) {
			TCPSocketThread tcpSocketThread = socketThreads.get(target);
			if (tcpSocketThread != null) {
				System.out.println("Send message: " + packet);
				tcpSocketThread.send(packet.encode());
			} else {
				System.out.println("Incoming connection is not found!");
			}
		}
	}

}
