package practice3;

import homework2.impl.Encryptor;
import homework2.impl.Message;
import homework2.impl.Packet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ClientServerTCPTest {
Encryptor encryptor;
StoreServerTCP server;

	@Before
	public void setup() throws IOException {
		server = new StoreServerTCP(TestConst.TCP_SERVER_PORT, TestConst.KEY);
		server.start();
		/* Encryptor Creating */
		 encryptor = new Encryptor(TestConst.KEY);
	}

	@Test
	public void TCPCommunicationTest() throws IOException {
		StoreClientTCP client = new StoreClientTCP();
		client.startConnection(TestConst.LOCALHOST, TestConst.TCP_SERVER_PORT);

		/* PacketTestPR1 Creating */
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
		/* PacketTestPR1 Creating */
		Packet response = client.sendMessage(encryptor.encrypt(packet));
		System.out.println(response.toString());
		client.stopConnection();
	}

	@After
	public void tearDown() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
