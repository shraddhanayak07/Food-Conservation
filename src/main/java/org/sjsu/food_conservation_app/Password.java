package org.sjsu.food_conservation_app;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Password {

	private static final SecureRandom RAND = new SecureRandom();
	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 512;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
	
	public static Optional<String> generateSalt(int length) {
		byte[] salt = new byte[length];
		RAND.nextBytes(salt);
		
		return Optional.of(Base64.getEncoder().encodeToString(salt));
	}
	
	public static Optional<String> hashPassword(String password, String salt) {
		char[] chars = password.toCharArray();
		byte[] bytes = salt.getBytes();
		
		PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);
		
		Arrays.fill(chars, Character.MIN_VALUE);
		
		
		byte[] securePassword;
		try {
			SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
			securePassword = fac.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return Optional.empty();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return Optional.empty();
		}
		return Optional.of(Base64.getEncoder().encodeToString(securePassword));
	}
	
	public static boolean verifyPassword(String password, String key, String salt) {
		Optional<String> optEncrypted = hashPassword(password, salt);
		if (optEncrypted.isEmpty()) {
			return false;
		}

		return optEncrypted.get().equals(key);
	}
}
