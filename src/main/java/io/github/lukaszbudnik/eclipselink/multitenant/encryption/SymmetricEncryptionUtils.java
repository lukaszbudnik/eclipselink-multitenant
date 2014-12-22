package io.github.lukaszbudnik.eclipselink.multitenant.encryption;


import lombok.Cleanup;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SymmetricEncryptionUtils {

    public static final int KEY_SIZE = 32;

    private static final SecureRandom random = new SecureRandom();

    private static final String ALGORITHM_FULL = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    public static byte[] generateSecretKey() {
        byte[] secretKey = new byte[KEY_SIZE];
        random.nextBytes(secretKey);
        return secretKey;
    }

    public static byte[] encrypt(byte[] dataToEncrypt, byte[] secretKey, byte[] iVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedData = null;
        Cipher cipher = Cipher.getInstance(ALGORITHM_FULL);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM), new IvParameterSpec(iVector));
        encryptedData = cipher.doFinal(dataToEncrypt);
        return encryptedData;
    }

    public static void encrypt(InputStream streamToEncrypt, OutputStream encryptedDataStream, byte[] secretKey, byte[] iVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] buffer = new byte[2048];
        Cipher cipher = Cipher.getInstance(ALGORITHM_FULL);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM), new IvParameterSpec(iVector));
        CipherOutputStream cus = new CipherOutputStream(encryptedDataStream, cipher);
        int numRead = 0;
        while ((numRead = streamToEncrypt.read(buffer)) >= 0) {
            cus.write(buffer, 0, numRead);
        }
        cus.flush();
        cus.close();
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] secretKey, byte[] iVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] decryptedData = null;
        Cipher cipher = Cipher.getInstance(ALGORITHM_FULL);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM), new IvParameterSpec(iVector));
        decryptedData = cipher.doFinal(encryptedData);
        return decryptedData;
    }

    public static void decrypt(InputStream encryptedDataStream, OutputStream decryptedDataStream, byte[] secretKey, byte[] iVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] buffer = new byte[2048];
        Cipher cipher = Cipher.getInstance(ALGORITHM_FULL);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, ALGORITHM), new IvParameterSpec(iVector));
        @Cleanup CipherInputStream cis = new CipherInputStream(encryptedDataStream, cipher);
        int numRead = 0;
        while ((numRead = cis.read(buffer)) >= 0) {
            decryptedDataStream.write(buffer, 0, numRead);
        }
        decryptedDataStream.flush();
        decryptedDataStream.close();
    }
}