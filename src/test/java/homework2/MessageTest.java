package homework2;

import homework2.impl.Message;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {
	private static final String KEY = "Mary has one cat";

	@Test
	public void encryptDecryptTest() {
		Message.Builder builder = new Message.Builder();
		builder.setCType(1)
				.setBUserId(1)
				.setMessage("{\"test\": true}");
		Message message = builder.build();
		byte[] data = message.encode();
		Message decodedMessage = Message.decode(data);
		Assert.assertEquals(message.getMessage(), decodedMessage.getMessage());

	}
}
