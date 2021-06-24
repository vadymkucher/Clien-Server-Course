package practice1;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static practice1.CRC16.crc16;
import static practice1.Encryption.*;


public class Main {
    private static final byte MAGIC_BYTE = 0x13;

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String prod1 = objectMapper.writeValueAsString(new ProductTestPR1("prod", 10));
        System.out.println(prod1);


        byte [] messageCiphered = doCrypto(prod1.getBytes(StandardCharsets.UTF_8));

        byte[] packet = encodePackage(new PacketTestPR1((byte) 1, 10L, messageCiphered));
        PacketTestPR1 packet1 = decodePackage(packet);

        byte[] messageUnciphered = doUnCrypto(packet1.getMessage());

        ProductTestPR1 decodedProdct = objectMapper.readValue(messageUnciphered, ProductTestPR1.class);
        System.out.println(decodedProdct.toString());
    }

    public static PacketTestPR1 decodePackage(byte[] bytes){
        ByteBuffer bb = ByteBuffer.wrap(bytes).order((ByteOrder.BIG_ENDIAN));

        if (bb.get()!=MAGIC_BYTE){
            throw new IllegalArgumentException("Magic Byte");
        }

        byte client = bb.get();
        long packet = bb.getLong();
        int messageLenght = bb.getInt();
        short crc16Head = bb.getShort();

        byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN)
                .put(MAGIC_BYTE)
                .put(client)
                .putLong(packet)
                .putInt(messageLenght)
                .array();

        if (crc16(head) != crc16Head){
            throw new IllegalArgumentException("CRC16 head");
        }

        byte[] message = Arrays.copyOfRange(bytes, 16, 16 + messageLenght);
        short crc16Message = bb.getShort(16+messageLenght);

        if (crc16(message) != crc16Message){
            throw new IllegalArgumentException("CRC16 message");
        }

        return new PacketTestPR1(client,packet,message);

    }

    public static byte[] encodePackage(PacketTestPR1 packet){
        byte[] message = packet.getMessage();

        byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN)
                .put(MAGIC_BYTE)
                .put(packet.getClient())
                .putLong(packet.getPacketId())
                .putInt(message.length)
                .array();

        return ByteBuffer.allocate(16+message.length+2).order(ByteOrder.BIG_ENDIAN)
                .put(head)
                .putShort(crc16(head))
                .put(message)
                .putShort(crc16(message))
                .array();

    }
}
