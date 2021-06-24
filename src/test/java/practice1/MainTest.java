package practice1;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main("practice1.MainTest");
    }


    @Test
    public void shouldEncodePackage(){
       PacketTestPR1 input = new PacketTestPR1((byte) 1 , 111, "Hi Owrld".getBytes());
       byte[] encodedInput = Main.encodePackage(input);

       PacketTestPR1 decoded = Main.decodePackage(encodedInput);
       assertEquals(1, decoded.getClient());
       assertEquals(111, decoded.getPacketId());
       assertEquals("Hi Owrld", new String(decoded.getMessage(), StandardCharsets.UTF_8));
    }
}
