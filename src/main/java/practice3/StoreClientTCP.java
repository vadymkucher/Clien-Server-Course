package practice3;

import homework2.impl.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class StoreClientTCP {
	private Socket clientSocket;
	private OutputStream out;
	private InputStream in;

	public void startConnection(String ip, int port) throws IOException {
		clientSocket = new Socket(ip, port);
		out = clientSocket.getOutputStream();
		in = clientSocket.getInputStream();
	}

	public Packet sendMessage(byte[] msg) throws IOException {
		out.write(msg);
		byte[] headerBytes = new byte[Packet.getHeaderLength()];
		clientSocket.getInputStream().read(headerBytes);
		Packet header = Packet.decodeHeader(headerBytes);
		byte[] bodyBytes = new byte[Packet.getBodyLength(header)];
		clientSocket.getInputStream().read(bodyBytes);
		Packet packet = Packet.decode(header, bodyBytes);
		return packet;
	}

	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
}
