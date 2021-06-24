package homework2;

import homework2.impl.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReceiverTest {
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
	private static final String KEY = "Mary has one cat";
	@Test
	public void receiverTest() {
		Decryptor decryptor = new Decryptor(KEY);
		Encryptor encryptor = new Encryptor(KEY);
		SenderMock sender = new SenderMock(encryptor);
		Processor processor = new Processor(sender);
		Receiver receiver = new Receiver(decryptor, processor, sender);


		/*Creating packet */
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

		ArrayList<byte[]> packets = new ArrayList<>();
		packets.add(encryptor.encrypt(packet));
		/*Creating packet */

		receiver.init(packets);

		executor.submit(receiver::start);
		executor.schedule(receiver::stop, 500L, TimeUnit.MILLISECONDS);
		try {
			TimeUnit.MILLISECONDS.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}

}
