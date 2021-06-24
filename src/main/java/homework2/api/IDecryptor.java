package homework2.api;


import homework2.impl.Packet;

public interface IDecryptor {
	Packet decrypt(byte[] message);
}
