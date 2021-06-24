package homework2;

import homework2.impl.Decryptor;
import homework2.impl.Encryptor;
import homework2.impl.Message;
import homework2.impl.Packet;
import org.junit.Assert;
import org.junit.Test;

public class PacketTest {

	private static final String KEY = "Mary has one cat";

	@Test
	public void testBuilder() {
		Decryptor decryptor = new Decryptor(KEY);
		Encryptor encryptor = new Encryptor(KEY);

		Message.Builder mBuilder = new Message.Builder();
		mBuilder.setCType(1)
				.setBUserId(1)
				.setMessage("{\"test\": true}");
		Message message = mBuilder.build();

		Packet.Builder pBuilder = new Packet.Builder();
		pBuilder.setBMagic((byte)0x13)
				.setBSrc((byte)0x01)
				.setBPktId(1L)
				.setBMsq(message);

		Packet packet = pBuilder.build();

		byte[] encrypted = encryptor.encrypt(packet);
		Packet decrypted = decryptor.decrypt(encrypted);
		byte[] pEncoded = packet.encode();
		Packet pDecoded = Packet.decode(pEncoded);
		Assert.assertEquals(packet.getBMagic(), pDecoded.getBMagic());
		Assert.assertEquals(packet.getBSrc(), pDecoded.getBSrc());
		Assert.assertEquals(packet.getBPktId(), pDecoded.getBPktId());
		Assert.assertEquals(packet.getbMsq().getMessage(), pDecoded.getbMsq().getMessage());
		Assert.assertEquals(packet.getWLen(), pDecoded.getWLen());
		Assert.assertEquals(packet.getWBodyCrc16(), pDecoded.getWBodyCrc16());
		Assert.assertEquals(packet.getWHeaderCrc16(), pDecoded.getWHeaderCrc16());


	}

}
