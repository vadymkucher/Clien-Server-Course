package homework2.api;


import homework2.impl.Packet;

public interface IEncryptor {
	byte[] encrypt(Packet packet);
}
