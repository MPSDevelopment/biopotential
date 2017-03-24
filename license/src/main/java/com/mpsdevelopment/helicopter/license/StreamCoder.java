package com.mpsdevelopment.helicopter.license;

import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class StreamCoder {

	private static Logger LOGGER = LoggerUtil.getLogger(StreamCoder.class);

	private static final Charset CHARSET = Charset.forName("ISO-8859-1");
	public static final String JSON_ENCODING = "UTF-8";

	private static final String ALGORITHM_WITH_PADDING = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES/CBC/NoPadding";
	private static final String SECRET_KEY_ALGORITHM = "AES";

	private static final String PATH_TO_KEYS = "keys/";
	private static final String PRIVATE_KEY_FILE = "secret_production_rsa";
	private static final String INPUT_VECTOR_FILE = "production.iv";
	private static final String SECRET_KEY_FILE = "production.key";

	private Cipher ecipher;
	private Cipher dcipher;

	private StreamCoder instanceWithPadding = null;
	private StreamCoder instanceWithoutPadding = null;

	private void createCiphers(boolean usePadding) {
		try {
			PrivateKey privateKey = EncryptionUtils.readPrivateKey(PATH_TO_KEYS + PRIVATE_KEY_FILE);
			String iv = EncryptionUtils.decrypt(PATH_TO_KEYS + INPUT_VECTOR_FILE, privateKey, CHARSET);
			if (iv != null) {
				LOGGER.debug("IV is %s. Length is %s. Base64 %s", iv, iv.length(), new String(Base64.encode(iv.getBytes(CHARSET)), CHARSET));
				String key = EncryptionUtils.decrypt(PATH_TO_KEYS + SECRET_KEY_FILE, privateKey, CHARSET);
				if (key != null) {
					LOGGER.debug("Key is %s.Length is %s. Base64 %s", key, key.length(), new String(Base64.encode(key.getBytes(CHARSET)), CHARSET));

					SecretKey key64 = new SecretKeySpec(key.getBytes(CHARSET), SECRET_KEY_ALGORITHM);
					AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv.getBytes(CHARSET));
					if (usePadding) {
						ecipher = Cipher.getInstance(ALGORITHM_WITH_PADDING);
						dcipher = Cipher.getInstance(ALGORITHM_WITH_PADDING);
					} else {
						ecipher = Cipher.getInstance(ALGORITHM);
						dcipher = Cipher.getInstance(ALGORITHM);
					}
					ecipher.init(Cipher.ENCRYPT_MODE, key64, paramSpec);
					dcipher.init(Cipher.DECRYPT_MODE, key64, paramSpec);
				} else {
					LOGGER.error("Cannot get key '%s' for decrytion", PATH_TO_KEYS + SECRET_KEY_FILE);
				}
			} else {
				LOGGER.error("Cannot get iv %s%s", PATH_TO_KEYS, INPUT_VECTOR_FILE);
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			LOGGER.printStackTrace(e);
		}
	}

	public StreamCoder get() {
		if (instanceWithoutPadding == null) {
			instanceWithoutPadding = new StreamCoder();
			instanceWithoutPadding.createCiphers(false);
		}
		return instanceWithoutPadding;
	}

	public StreamCoder getWithPadding() {
		if (instanceWithPadding == null) {
			instanceWithPadding = new StreamCoder();
			instanceWithPadding.createCiphers(true);
		}
		return instanceWithPadding;
	}

	public void encryptFile(File inputFile, File outputFile, boolean useOutputStream, boolean useBase64) {
		cryptFile(inputFile, outputFile, ecipher, useOutputStream, useBase64);
	}

	public void decryptFile(File inputFile, File outputFile, boolean useOutputStream, boolean useBase64) {
		cryptFile(inputFile, outputFile, dcipher, useOutputStream, useBase64);
	}

	private void cryptFile(File inputFile, File outputFile, Cipher cipher, boolean useOutputStream, boolean useBase64) {
		InputStream input = null;
		OutputStream output = null;
		Base64InputStream base64InputStream = null;

		try {
			if (useOutputStream) {
				input = new FileInputStream(inputFile);
				output = new CipherOutputStream(new FileOutputStream(outputFile), cipher);
			} else {
				// if (useBase64) {
				// base64InputStream = new Base64InputStream(new FileInputStream(inputFile), false, -1, null);
				// input = new CipherInputStream(base64InputStream, cipher);
				// } else {
				input = new CipherInputStream(new FileInputStream(inputFile), cipher);
				// }
				output = new FileOutputStream(outputFile);
			}
			IOUtils.copyLarge(input, output);
		} catch (IOException e) {
			LOGGER.error("Cannot encrypt file %s to %s, because %s", inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), e.getMessage());
			LOGGER.printStackTrace(e);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
			if (base64InputStream != null) {
				IOUtils.closeQuietly(base64InputStream);
			}
		}
	}

	public void symmetricEncrypt(String text, File outputFile) {
		try {
			String encryptedString = org.apache.commons.codec.binary.Base64.encodeBase64String(ecipher.doFinal(text.getBytes()));
			FileUtils.writeStringToFile(outputFile, encryptedString, JSON_ENCODING);
		} catch (Exception e) {
			LOGGER.printStackTrace(e);
		}
	}

	public String symmetricDecrypt(File fileName) {
		try {
			String data = FileUtils.readFileToString(fileName, JSON_ENCODING);
			byte[] encryptText = org.apache.commons.codec.binary.Base64.decodeBase64(data);
			return new String(dcipher.doFinal(encryptText));
		} catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
			LOGGER.error("Cannot decrypt json from file %s, because %s", fileName, e.getMessage());
			LOGGER.printStackTrace(e);
		}
		return null;
	}
}
