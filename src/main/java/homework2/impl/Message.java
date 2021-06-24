package homework2.impl;

import java.util.HashMap;
import java.util.Map;

public class Message {
	private static final Segments[] SEGMENTS = {Segments.cType, Segments.bUserId, Segments.message};
	private Map<Segments, byte[]> data = new HashMap<>();

	private Message() {

	}

	public static Message decode(byte[] data) {
		validateData(data);
		int cType = Util.bytesToInt(data, 0);
		int bUserId = Util.bytesToInt(data, 4);
		byte[] messageBytes = new byte[data.length - 8];
		System.arraycopy(data, 8, messageBytes, 0, messageBytes.length);
		Builder builder = new Builder();
		builder.setSegment(Segments.cType, cType).setSegment(Segments.bUserId, bUserId)
				.setSegment(Segments.message, messageBytes);
		return builder.build();

	}

	private static void validateData(byte[] data) {
		if (data.length < 8) // at least 2 int fields
			throw new IllegalStateException();
	}

	/* Properties section */


	public int getcType() {
		return Util.bytesToInt(data.get(Segments.cType), 0);
	}

	public int getbUserId() {
		return Util.bytesToInt(data.get(Segments.bUserId), 0);
	}

	public String getMessage() {
		return new String(data.get(Segments.message));
	}

	public byte[] getMessageBytes() {
		return data.get(Segments.message);
	}


	/* End of Properties section */

	public byte[] encode() {
		return Util.flatten(data.get(Segments.cType), data.get(Segments.bUserId), data.get(Segments.message));
	}

	public static class Builder {
		private Map<Segments, byte[]> data = new HashMap<>();

		public Builder() {
		}

		public Builder(Message message) {
			setMessage(message.getMessage());
			setBUserId(message.getbUserId());
			setCType(message.getcType());
		}


		private void validateData() {
			for (Segments segment : SEGMENTS) {
				if (!data.containsKey(segment))
					throw new IllegalStateException("Invalid data!");
			}
		}

		public Builder setCType(int value) {
			return setSegment(Segments.cType, value);
		}

		public Builder setBUserId(int value) {
			return setSegment(Segments.bUserId, value);
		}

		public Builder setMessage(String value) {
			return setSegment(Segments.message, value);
		}

		public Builder setMessageBytes(byte[] value) {
			data.put(Segments.message, value);
			return this;
		}

		private Builder setSegment(Segments segment, byte[] value) {
			data.put(segment, value);
			return this;
		}

		private Builder setSegment(Segments segment, int value) {
			data.put(segment, Util.intToBytes(value));
			return this;
		}

		private Builder setSegment(Segments segment, String value) {
			data.put(segment, value.getBytes());
			return this;
		}


		public byte[] getSegmentData(Segments segment) {
			return data.get(segment);
		}

		public Message build() {
			validateData();
			Message message = new Message();
			message.data = this.data;
			return message;
		}
	}

	@Override
	public String toString() {
		return "Message{" + "cType=" + getcType() + ", bUserId=" + getbUserId() + ", message='" + getMessage() + '\'' +
				'}';
	}
}


