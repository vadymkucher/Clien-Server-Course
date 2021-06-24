package practice1;

import java.util.Arrays;

public class PacketTestPR1 {
    private final byte client;
    private final long packet;
    private final byte[] message;

    public PacketTestPR1(final byte client, final long packet, final byte[] message){
        this.client = client;
        this.packet = packet;
        this.message = message;
    }

    public byte getClient() {
        return client;
    }

    public long getPacketId() {
        return packet;
    }

    public byte[] getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PacketTestPR1{" +
                "client=" + client +
                ", packet=" + packet +
                ", message=" + Arrays.toString(message) +
                '}';
    }
}
