package homework2.impl;


import homework2.api.IEncryptor;

public class Encryptor implements IEncryptor {
	private final String KEY;

	public Encryptor(String key) {
		this.KEY = key;
	}

	@Override
	public byte[] encrypt(Packet packet) {
		Message.Builder mBuilder = new Message.Builder(packet.getbMsq());
		mBuilder.setMessageBytes(CryptoUtil.encrypt(KEY, packet.getbMsq().getMessageBytes()));
		Message encryptedMessage = mBuilder.build();
		Packet.Builder pBuilder = new Packet.Builder(packet);
		pBuilder.setBMsq(encryptedMessage);
		return pBuilder.build().encode();
	}
}
