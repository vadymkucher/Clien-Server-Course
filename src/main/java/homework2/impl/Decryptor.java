package homework2.impl;


import homework2.api.IDecryptor;

public class Decryptor implements IDecryptor {
	private final String KEY;

	public Decryptor(String key) {
		this.KEY = key;
	}

	@Override
	public Packet decrypt(byte[] message) {
//		System.out.println("decrypt");
		Packet encryptedPacket = Packet.decode(message);
		Packet.Builder pBuilder = new Packet.Builder(encryptedPacket);
		Message.Builder m = new Message.Builder(encryptedPacket.getbMsq());
		m.setMessage(new String(CryptoUtil.decrypt(KEY, encryptedPacket.getbMsq().getMessageBytes())));
		pBuilder.setBMsq(m.build());
		return pBuilder.build();
	}
}
