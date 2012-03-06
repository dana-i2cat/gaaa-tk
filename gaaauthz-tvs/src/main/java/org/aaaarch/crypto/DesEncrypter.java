/*
 * Created on 10/11/2004
 *
 * @author andrewt
 * @version 1.0
 * <p>Title:DesEncrypter.java</p>
 * <p>Project: CNL_Full</p>
 * <p>Copyright (c) 2004 </p>
 * <p>Telematica Instituut </p>
*/
package org.aaaarch.crypto;

import java.io.UnsupportedEncodingException;
import java.security.spec.AlgorithmParameterSpec;
//import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.aaaarch.crypto.CryptoData;
import org.aaaarch.tvs.utils.HelpersHexConverter;

/**
 * Taken from: http://javaalmanac.com/egs/javax.crypto/DesString.html
 *         and http://javaalmanac.com/egs/javax.crypto/PassKey.html
 */
public class DesEncrypter {
    Cipher ecipher;
    Cipher dcipher;

    // Constructor when you already have a SecretKey
    public DesEncrypter(SecretKey key) {
        try {
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);

        } catch (javax.crypto.NoSuchPaddingException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch (java.security.InvalidKeyException e) {
        }
    }

    // 8-byte Salt
    byte[] salt = {
        (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };

    // Iteration count
    int iterationCount = 19;

    // encrypter when you only have a shared secret phrase
    public DesEncrypter(String passPhrase) {
        try {
    		/*
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedKeyInfo.getAlgName());
			PBEKeySpec passwordSpec = new PBEKeySpec(password);
			SecretKey key = keyFactory.generateSecret(passwordSpec);

			Cipher cipher = Cipher.getInstance(encryptedKeyInfo.getAlgName());
			cipher.init(Cipher.DECRYPT_MODE, key, params);
			PKCS8EncodedKeySpec decrypted = encryptedKeyInfo.getKeySpec(cipher);

			return getPkcs8Key(decrypted.getEncoded());
		 * */
            // Create the key
            //KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            PBEKeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray());
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (java.security.InvalidAlgorithmParameterException e) {
        } catch (java.security.spec.InvalidKeySpecException e) {
        } catch (javax.crypto.NoSuchPaddingException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch (java.security.InvalidKeyException e) {
        }
    }
    public String encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } 
        return null;
    }

    public String encryptHex(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            //return new sun.misc.BASE64Encoder().encode(enc);
            String hex = HelpersHexConverter.byteArrayToHex(enc);
            return hex;
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } 
        return null;
    }

    public String decrypt(String str) {
        try {
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (java.io.IOException e) {
        }
        return null;
    }

    public String decryptHex(String str) {
        try {
            // Decode base64 to get bytes
            byte[] dec = HelpersHexConverter.hexToByteArray(str, false);
            //byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (java.io.IOException e) {
        }
        return null;
    }
    
}