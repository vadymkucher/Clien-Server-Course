package homework2.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Packet {
    private static final byte MESSAGE_START = 0x13;
    private static final Segments[] SEGMENTS =
            {Segments.bMagic, Segments.bSrc, Segments.bPktId, Segments.wLen, Segments.wHeaderCrc16, Segments.bMsq,
                    Segments.wBodyCrc16};
    private static final Segments[] HEADER_SEGMENTS =
            {Segments.bMagic, Segments.bSrc, Segments.bPktId, Segments.wLen, Segments.wHeaderCrc16};
    private static final Segments[] BODY_SEGMENTS =
            {Segments.bMsq, Segments.wBodyCrc16};
    private Map<Segments, byte[]> data;

    private InetAddress clientAddress;
    private int clientPort;

    private Packet() {
    }

    public byte[] encode() {
        List<byte[]> segmentsData = new ArrayList<>();
        for (Segments segment : SEGMENTS) {
            segmentsData.add(data.get(segment));
        }
        return Util.flatten(segmentsData.toArray());
    }

    public static Packet decode(byte[] data) {

        int position = 0;
        Builder pBuilder = new Builder();
        Integer wLen = null;
        for (Segments segment : SEGMENTS) {
            Integer length = segment.getLength();
            if (length == null && segment == Segments.bMsq)
                length = wLen;

            byte[] segmentData = new byte[length];
            System.arraycopy(data, position, segmentData, 0, length);
            if (segment == Segments.wLen)
                wLen = Util.bytesToInt(segmentData, 0);
            pBuilder.setSegment(segment, segmentData);
            position += length;
        }

        return pBuilder.build();
    }

    public static Packet decodeHeader(byte[] data) {
        int position = 0;
        Builder pBuilder = new Builder();
        for (Segments segment : HEADER_SEGMENTS) {
            Integer length = segment.getLength();

            byte[] segmentData = new byte[length];
            System.arraycopy(data, position, segmentData, 0, length);
            pBuilder.setSegment(segment, segmentData);
            position += length;
        }

        return pBuilder.buildHeader();
    }

    public static Packet decode(Packet header, byte[] data) {
        Builder pBuilder = new Builder(header);
        byte[] temp = new byte[header.getWLen()];
        System.arraycopy(data, 0, temp, 0, header.getWLen());
        pBuilder.setSegment(Segments.bMsq, temp);
        temp = new byte[Segments.wBodyCrc16.getLength()];
        System.arraycopy(data, header.getWLen(), temp, 0, Segments.wBodyCrc16.getLength());
        pBuilder.setSegment(Segments.wBodyCrc16, temp);
        return pBuilder.build();
    }



    /* Properties section */

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public Byte getBMagic() {
        return data.containsKey(Segments.bMagic) ? data.get(Segments.bMagic)[0] : null;
    }

    public Byte getBSrc() {
        return data.containsKey(Segments.bSrc) ? data.get(Segments.bSrc)[0] : null;
    }

    public Long getBPktId() {
        return data.containsKey(Segments.bPktId) ? Util.bytesToLong(data.get(Segments.bPktId), 0) : null;
    }

    public Integer getWLen() {
        return data.containsKey(Segments.wLen) ? Util.bytesToInt(data.get(Segments.wLen), 0) : null;
    }

    public Short getWHeaderCrc16() {
        return data.containsKey(Segments.wHeaderCrc16) ? Util.bytesToShort(data.get(Segments.wHeaderCrc16), 0) : null;
    }

    public Message getbMsq() {
        if (!data.containsKey(Segments.bMsq))
            return null;
        return Message.decode(data.get(Segments.bMsq));
    }

    public Short getWBodyCrc16() {

        return data.containsKey(Segments.wBodyCrc16) ? Util.bytesToShort(data.get(Segments.wBodyCrc16), 0) : null;
    }

    public static int getHeaderLength() {
        int size = 0;
        for (Segments segment : HEADER_SEGMENTS) {
            size += segment.getLength();
        }
        return size;

    }

    public static int getBodyLength(Packet header) {
        return header.getWLen() + Segments.wBodyCrc16.getLength();
    }

    /* End of Properties section */

    public static class Builder {
        private volatile Map<Segments, byte[]> data;

        public Builder() {
            data = new ConcurrentHashMap<>();
        }

        public Builder(Packet packet) {
            this();
            setBSrc(packet.getBSrc());
            setBMagic(packet.getBMagic());
            setBPktId(packet.getBPktId());
            if (packet.getbMsq() != null)
                setBMsq(packet.getbMsq());

        }

        private void validateData() {
            //checking first segment value (must be 0x13)
            if (MESSAGE_START != data.get(Segments.bMagic)[0])

                throw new IllegalStateException("Invalid data! bMagic = " + data.get(Segments.bMagic)[0]);

            //checking sizes of SEGMENTS
            for (Segments segment : SEGMENTS) {

                if (segment != Segments.bMsq && data.get(segment).length != segment.getLength())
                    throw new IllegalStateException("Invalid data! " + segment + " size = " + data.get(segment).length
                            + ", but should be " + segment.getLength());
            }

            //checking Header Crc16
            short crc16 = Util.getCrc16(
                    Util.flatten(data.get(Segments.bMagic), data.get(Segments.bSrc),
                            data.get(Segments.bPktId), data.get(Segments.wLen)));
            if (crc16 != Util.bytesToShort(data.get(Segments.wHeaderCrc16), 0))
                throw new IllegalStateException("Invalid data!");

            //checking Message Crc16
            if (Util.bytesToShort(data.get(Segments.wBodyCrc16), 0) != Util.getCrc16(data.get(Segments.bMsq))) {
                System.out.println(Util.bytesToShort(data.get(Segments.wBodyCrc16), 0) + " " + Util.getCrc16(data.get(Segments.bMsq)));
                throw new IllegalStateException("Invalid data!");
            }


        }

        private void validateHeaderData() {
            //checking first segment value (must be 0x13)
            if (MESSAGE_START != data.get(Segments.bMagic)[0])

                throw new IllegalStateException("Invalid data! bMagic = " + data.get(Segments.bMagic)[0]);

            //checking sizes of HEADER_SEGMENTS
            for (Segments segment : HEADER_SEGMENTS) {

                if (segment != Segments.bMsq && data.get(segment).length != segment.getLength())
                    throw new IllegalStateException("Invalid data! " + segment + " size = " + data.get(segment).length
                            + ", but should be " + segment.getLength());
            }

            //checking Header Crc16
            short crc16 = Util.getCrc16(
                    Util.flatten(data.get(Segments.bMagic), data.get(Segments.bSrc),
                            data.get(Segments.bPktId), data.get(Segments.wLen)));
            if (crc16 != Util.bytesToShort(data.get(Segments.wHeaderCrc16), 0))
                throw new IllegalStateException("Invalid data!");

        }

        public Builder setBMagic(byte value) {
            return setSegment(Segments.bMagic, value);
        }

        public Builder setBSrc(byte value) {
            return setSegment(Segments.bSrc, value);
        }

        public Builder setBPktId(long value) {
            return setSegment(Segments.bPktId, value);
        }

        public Builder setWLen(int value) {
            return setSegment(Segments.wLen, value);
        }

        public Builder setWHeaderCrc16(short value) {
            return setSegment(Segments.wHeaderCrc16, value);
        }

        public Builder setBMsq(Message value) {
            return setSegment(Segments.bMsq, value);
        }

        public Builder setWBodyCrc16(short value) {
            return setSegment(Segments.wBodyCrc16, value);
        }

        private Builder setSegment(Segments segment, byte[] value) {
//			System.out.println(segment + "=" + value);
//			new Exception().printStackTrace(System.out);
            data.put(segment, value);
            return this;
        }

        private Builder setSegment(Segments segment, byte value) {
            setSegment(segment, new byte[]{value});
            return this;
        }

        private Builder setSegment(Segments segment, short value) {
            setSegment(segment, Util.shortToBytes(value));
            return this;
        }

        private Builder setSegment(Segments segment, int value) {
            setSegment(segment, Util.intToBytes(value));
            return this;
        }

        private Builder setSegment(Segments segment, long value) {
            data.put(segment, Util.longToBytes(value));
            return this;
        }

        private Builder setSegment(Segments segment, Message message) {
            setSegment(segment, message.encode());
            return this;
        }

        public byte[] getSegmentData(Segments segment) {
            return data.get(segment);
        }

        public Packet build() {
//			System.out.println("build");
            if (!data.containsKey(Segments.wLen))
                this.setWLen(data.get(Segments.bMsq).length);

            if (!data.containsKey(Segments.wHeaderCrc16))
                this.setSegment(Segments.wHeaderCrc16, Util.getCrc16(
                        Util.flatten(data.get(Segments.bMagic),
                                data.get(Segments.bSrc),
                                data.get(Segments.bPktId),
                                data.get(Segments.wLen))));

            if (!data.containsKey(Segments.wBodyCrc16))
                this.setSegment(Segments.wBodyCrc16, Util.getCrc16(data.get(Segments.bMsq)));

            validateData();
            Packet packet = new Packet();
            packet.data = new HashMap<>(this.data);
//			System.out.println(packet);
            return packet;
        }

        public Packet buildHeader() {
//			System.out.println("build");
            validateHeaderData();
            Packet packet = new Packet();
            packet.data = new HashMap<>(this.data);
//			System.out.println(packet);
            return packet;
        }
    }

    @Override
    public String toString() {
        return "PacketTestPR1{" +
                "clientAddress=" + clientAddress +
                ", clientPort=" + clientPort +
                ", BMagic=" + getBMagic() +
                ", BSrc=" + getBSrc() +
                ", BPktId=" + getBPktId() +
                ", WLen=" + getWLen() +
                ", WHeaderCrc16=" + getWHeaderCrc16() +
                ", bMsq=" + getbMsq() +
                ", WBodyCrc16=" + getWBodyCrc16() +
                '}';
    }
}
