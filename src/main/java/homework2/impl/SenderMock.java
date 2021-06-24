package homework2.impl;


import homework2.api.ISender;

import java.net.InetAddress;

public class SenderMock implements ISender {
	Encryptor encryptor;

	public SenderMock(Encryptor encryptor) {
		this.encryptor = encryptor;
	}

	@Override
	public void sendMessage(Packet packet, InetAddress target) {
		System.out.println("Send message: " + packet );
	}
}
