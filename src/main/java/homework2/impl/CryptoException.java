package homework2.impl;

public class CryptoException extends IllegalStateException {

	public CryptoException() {
	}

	public CryptoException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CryptoException(String message) {
		super(message, null);
	}
}