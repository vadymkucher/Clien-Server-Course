package homework2.impl;


import homework2.api.IReceiver;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Receiver implements IReceiver, Runnable {
	private Queue<byte[]> packetQueue = new ArrayDeque<>(1024);
	private Decryptor decryptor;
	private Processor processor;
	private SenderMock sender;
	private AtomicBoolean running = new AtomicBoolean(false);

	public Receiver(Decryptor decryptor, Processor processor, SenderMock sender) {
		this.decryptor = decryptor;
		this.sender = sender;
		this.processor = processor;
	}

	@Override
	public void receiveMessage() {
		byte[] p = packetQueue.poll();
		if (p == null) return;
		Packet packet = decryptor.decrypt(p);
		processor.process(packet);
	}

	public void init(List<byte[]> packets) {
		packetQueue.addAll(packets);
	}

	public void start() {
		try {
			System.out.println("START");
			running.set(true);
			run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		System.out.println("STOP");
		running.set(false);
	}

	@Override
	public void run() {
		while (running.get()) {
			receiveMessage();
		}
	}

	public boolean isRunning() {
		System.out.println("running=" + running.get());
		return running.get();
	}
}
