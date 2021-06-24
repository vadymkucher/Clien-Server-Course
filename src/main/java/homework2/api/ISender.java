package homework2.api;

import homework2.impl.Packet;

import java.net.InetAddress;

public interface ISender {
	void sendMessage(Packet packet, InetAddress target);
}