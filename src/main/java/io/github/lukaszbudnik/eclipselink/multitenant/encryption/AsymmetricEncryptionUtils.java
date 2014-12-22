package io.github.lukaszbudnik.eclipselink.multitenant.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class AsymmetricEncryptionUtils {

    private static final String algorithm = "RSA/ECB/PKCS1Padding";
    private static final String provider = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Decrypts encrypted bytes array using a private key.
     *
     * @param encryptedSource byte array with encrypted data
     * @param privateKey      private key
     * @return array of bytes with decrypted data
     * @throws java.security.NoSuchAlgorithmException
     * @throws javax.crypto.NoSuchPaddingException
     * @throws java.security.InvalidKeyException
     * @throws javax.crypto.IllegalBlockSizeException
     * @throws javax.crypto.BadPaddingException
     * @throws java.security.NoSuchProviderException
     */
    public static byte[] decrypt(byte[] encryptedSource, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        byte[] decryptedData = null;
        Cipher cipher = Cipher.getInstance(algorithm, provider);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedData = cipher.doFinal(encryptedSource);
        return decryptedData;
    }

    /**
     * Encrypts bytes array using a public key
     *
     * @param dataToEncrypt data to encrypt (array of bytes)
     * @param publicKey     public key
     * @return an array of bytes with encrypted data
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchProviderException
     */
    public static byte[] encrypt(byte[] dataToEncrypt, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        byte[] encryptedData = null;
        Cipher cipher = Cipher.getInstance(algorithm, provider);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        encryptedData = cipher.doFinal(dataToEncrypt);
        return encryptedData;
    }
}
