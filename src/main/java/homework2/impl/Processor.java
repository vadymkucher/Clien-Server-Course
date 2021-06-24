package homework2.impl;

import homework2.api.IProccesor;
import homework2.api.ISender;

public class Processor implements IProccesor {
	private ISender sender;

	public Processor(ISender sender) {
		this.sender = sender;
	}

	@Override
	public void process(Packet request) {
		System.out.println("Processing request: " + request);

		Message.Builder mBuilder= new Message.Builder();
		mBuilder.setCType(request.getbMsq().getcType())
				.setBUserId(request.getbMsq().getbUserId())
				.setMessage("OK");

		Message message = mBuilder.build();
		Packet.Builder pBuilder = new Packet.Builder();
		pBuilder.setBMagic(request.getBMagic())
				.setBSrc(request.getBSrc())
				.setBPktId(request.getBPktId())
				.setBMsq(message);
		Packet response = pBuilder.build();
		response.setClientAddress(request.getClientAddress());
		response.setClientPort(request.getClientPort());
		sender.sendMessage(response, response.getClientAddress());
	}


}
