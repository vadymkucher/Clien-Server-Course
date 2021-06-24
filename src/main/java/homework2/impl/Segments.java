package homework2.impl;

import java.util.HashMap;
import java.util.Map;

public enum Segments {

	// Wrapped message segments
	bMagic(byte.class), // Байт, що вказує на початок пакету - значення 13h (h - значить hex)
	bSrc(byte.class), // Унікальний номер клієнтського застосування
	bPktId(long.class), // Номер повідомлення. Номер постійно збільшується. В форматі big-endian
	wLen(int.class), // Довжина пакету даних big-endian
	wHeaderCrc16(short.class), // CRC16 байтів (00-13) big-endian
	bMsq(Message.class), // Message - корисне повідомлення
	wBodyCrc16(short.class), // 	CRC16 байтів (16 до 16+wLen-1) big-endian
	// Message segments
	cType(int.class),
	bUserId(int.class),
	message (String.class);

	private final static Map<Class<?>, Integer> LENGTHS = new HashMap<Class<?>, Integer>(){{
        put(byte.class, 1);
        put(long.class, 8);
        put(short.class, 2);
        put(int.class, 4);
    }};
	private Class<?> klass;

	Segments(Class<?> klass) {
		this.klass = klass;
	}

	public Integer getLength() {
		return LENGTHS.get(this.klass);
	}

}
