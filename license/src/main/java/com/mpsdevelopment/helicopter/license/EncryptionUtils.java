package com.mpsdevelopment.helicopter.license;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.IOUtils;

import com.github.fommil.ssh.SshRsaCrypto;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class EncryptionUtils {

	private static Logger LOGGER = LoggerUtil.getLogger(EncryptionUtils.class);
	// private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();

	public static String decrypt(String filename, PrivateKey key, Charset charset) {
		try {

			LOGGER.debug("Decrypting file %s", filename);

			// if (!EncryptionUtils.class.getClassLoader().getResource(filename).) {
			// LOGGER.error("File %s does not exist in resources", filename);
			// }

			Cipher cipher = Cipher.getInstance("RSA");
//			LOGGER.info("Decryption provider is %s", cipher.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(geBytesFromFile(filename)), charset);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
			LOGGER.error("Cannot decrypt from %s, because %s", filename, e.getMessage());
			LOGGER.printStackTrace(e);
		}
		return null;
	}

	/**
	 * Reads the PKCS#8 standard encoded RSA private key in <code>filename</code>.
	 *
	 * @param filename
	 *            The name of the file with the private key.
	 *
	 * @return The private key in <code>filename</code>.
	 */
	public static PrivateKey readPrivateKey(String filename) {
		String key;
		try {

			// if (resourceManager.getFile(filename).exists()) {
			// LOGGER.debug("Private key is ok");
			// }

			key = getTextFromFile(filename);
			SshRsaCrypto rsa = new SshRsaCrypto();
			return rsa.readPrivateKey(rsa.slurpPrivateKey(key));
		} catch (IOException | GeneralSecurityException e) {
			LOGGER.error("Cannot read private key from %s, because %s", filename, e.getMessage());
		}
		return null;
	}

	public static PrivateKey readPrivateKeyFromString(String keyString) {
		try {
			SshRsaCrypto rsa = new SshRsaCrypto();
			return rsa.readPrivateKey(rsa.slurpPrivateKey(keyString));
		} catch (IOException | GeneralSecurityException e) {
			LOGGER.error("Cannot read private key, because %s", e.getMessage());
		}
		return null;
	}

	/**
	 * Reads RSA public key from file
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static PublicKey readPublicKey(String filename) throws Exception {
		String key = getTextFromFile(filename);
		SshRsaCrypto rsa = new SshRsaCrypto();
		return rsa.readPublicKey(rsa.slurpPublicKey(key));
	}

	private static byte[] geBytesFromFile(String filename) throws IOException {
		byte[] bytes;
		try (InputStream file = EncryptionUtils.class.getClassLoader().getResource(filename).openStream()) {
			bytes = IOUtils.toByteArray(file);
		}
		// LOGGER.debug("Public key is %s", bytes);
		return bytes;
	}

	private static String getTextFromFile(String filename) throws IOException {
		byte[] bytes = geBytesFromFile(filename);
		return new String(bytes, "UTF-8");
	}
}
