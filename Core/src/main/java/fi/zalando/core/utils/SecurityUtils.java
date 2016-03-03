package fi.zalando.core.utils;

import android.os.Build;
import android.os.Process;
import android.support.v4.util.Pair;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class used to provide security related functionalities
 *
 * Created by jduran on 09/02/16.
 */
public class SecurityUtils {

    /**
     * Decrypts the given text using the given Key
     *
     * @param textToDecrypt {@link String} to decrypt
     * @param key           {@link String} with the key to use for decryption
     * @return {@link String} decrypted
     * @throws GeneralSecurityException     if AES is not implemented on this system
     * @throws UnsupportedEncodingException if UTF-8 is not supported in this system
     */
    public static String decrypt(String textToDecrypt, String key) throws GeneralSecurityException,
            UnsupportedEncodingException {

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity
                .CipherTextIvMac(textToDecrypt);
        return AesCbcWithIntegrity.decryptString(cipherTextIvMac, AesCbcWithIntegrity.keys(key));
    }

    /**
     * Generates a random {@link String}
     *
     * @return {@link String} randomnly generated
     */
    public static String generateRandomString() {

        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    /**
     * Generates a pure random encryption key
     *
     * @return {@link String} with a random key
     */
    public static String generateRandomKey() throws GeneralSecurityException {

        return AesCbcWithIntegrity.generateKeyFromPassword(generateRandomString()).toString();
    }

    /**
     * Generates an encryption key using as reference the given password
     *
     * @param password {@link String} with the password to use as reference
     * @return {@link String} with the generated key
     * @throws GeneralSecurityException if AES is not implemented on this system
     */
    public static String generateKey(String password) throws GeneralSecurityException {

        return AesCbcWithIntegrity.generateKeyFromPassword(password, Base64.decode(SecurityUtils
                .class.getName(), Base64.DEFAULT)).toString();
    }

    /**
     * Encrypts the given text. It returns a {@link Pair} where the first item is the encrypted text
     * and second is the needed key to decrypt it
     *
     * @param textToEncrypt {@link String} to encrypt
     * @return {@link Pair} where the first item is the encrypted text and second is the needed key
     * to decrypt it
     * @throws GeneralSecurityException     if AES is not implemented on this system
     * @throws UnsupportedEncodingException if UTF-8 is not supported in this system
     */
    public static Pair<String, String> encrypt(String textToEncrypt) throws
            GeneralSecurityException, UnsupportedEncodingException {

        AesCbcWithIntegrity.SecretKeys secretKeys = AesCbcWithIntegrity.generateKey();
        String encryptedValue = AesCbcWithIntegrity.encrypt(textToEncrypt, secretKeys).toString();

        return new Pair<>(encryptedValue, secretKeys.toString());
    }

    /**
     * Creates an MD5 BASE64 digest of the given {@link String} text
     *
     * @param textToEncrypt {@link String} to encrypt
     * @return {@link String} encrypted
     */
    public static String encryptKey(String textToEncrypt) {

        return MD5.createDigest(textToEncrypt);
    }

    /**
     * Transforms the text to Base64 encoded text
     *
     * @param textToTransform {@link String} to transform
     * @param flags           {@link Integer} with the Base64 flags
     * @return {@link String} encoded in Base64
     */
    public static String toBase64(String textToTransform, int flags) {

        return Base64.encodeToString(textToTransform.getBytes(), flags);
    }

    /**
     * Utility class used for encryption and decryption. Source: http://bit.ly/1EJijAI
     */
    @SuppressWarnings("unused")
    private static class AesCbcWithIntegrity {
        // If the PRNG fix would not succeed for some reason, we normally will throw an exception.
        // If ALLOW_BROKEN_PRNG is true, however, we will simply log instead.
        private static final boolean ALLOW_BROKEN_PRNG = false;

        private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
        private static final String CIPHER = "AES";
        private static final String RANDOM_ALGORITHM = "SHA1PRNG";
        private static final int AES_KEY_LENGTH_BITS = 128;
        private static final int IV_LENGTH_BYTES = 16;
        private static final int PBE_ITERATION_COUNT = 10000;
        private static final int PBE_SALT_LENGTH_BITS = AES_KEY_LENGTH_BITS; // same size as key
        // output
        private static final String PBE_ALGORITHM = "PBKDF2WithHmacSHA1";

        //Made BASE_64_FLAGS public as it's useful to know for compatibility.
        public static final int BASE64_FLAGS = Base64.NO_WRAP;
        //default for testing
        static final AtomicBoolean prngFixed = new AtomicBoolean(false);

        private static final String HMAC_ALGORITHM = "HmacSHA256";
        private static final int HMAC_KEY_LENGTH_BITS = 256;

        /**
         * Converts the given AES/HMAC keys into a base64 encoded string suitable for storage.
         * Sister function of keys.
         *
         * @param keys The combined aes and hmac keys
         * @return a base 64 encoded AES string & hmac key as base64(aesKey) : base64(hmacKey)
         */
        public static String keyString(SecretKeys keys) {
            return keys.toString();
        }

        /**
         * An aes key derived from a base64 encoded key. This does not generate the key. It's not
         * random or a PBE key.
         *
         * @param keysStr a base64 encoded AES key / hmac key as base64(aesKey) : base64(hmacKey).
         * @return an AES & HMAC key set suitable for other functions.
         */
        public static SecretKeys keys(String keysStr) throws InvalidKeyException {
            String[] keysArr = keysStr.split(":");

            if (keysArr.length != 2) {
                throw new IllegalArgumentException("Cannot parse aesKey:hmacKey");

            } else {
                byte[] confidentialityKey = Base64.decode(keysArr[0], BASE64_FLAGS);
                if (confidentialityKey.length != AES_KEY_LENGTH_BITS / 8) {
                    throw new InvalidKeyException("Base64 decoded key is not " +
                            AES_KEY_LENGTH_BITS + " bytes");
                }
                byte[] integrityKey = Base64.decode(keysArr[1], BASE64_FLAGS);
                if (integrityKey.length != HMAC_KEY_LENGTH_BITS / 8) {
                    throw new InvalidKeyException("Base64 decoded key is not " +
                            HMAC_KEY_LENGTH_BITS + " bytes");
                }

                return new SecretKeys(
                        new SecretKeySpec(confidentialityKey, 0, confidentialityKey.length, CIPHER),
                        new SecretKeySpec(integrityKey, HMAC_ALGORITHM));
            }
        }

        /**
         * A function that generates random AES & HMAC keys and prints out exceptions but doesn't
         * throw them since none should be encountered. If they are encountered, the return value is
         * null.
         *
         * @return The AES & HMAC keys.
         * @throws GeneralSecurityException if AES is not implemented on this system, or a suitable
         *                                  RNG is not available
         */
        public static SecretKeys generateKey() throws GeneralSecurityException {
            fixPrng();
            KeyGenerator keyGen = KeyGenerator.getInstance(CIPHER);
            // No need to provide a SecureRandom or set a seed since that will
            // happen automatically.
            keyGen.init(AES_KEY_LENGTH_BITS);
            SecretKey confidentialityKey = keyGen.generateKey();

            //Now make the HMAC key
            byte[] integrityKeyBytes = randomBytes(HMAC_KEY_LENGTH_BITS / 8);//to get bytes
            SecretKey integrityKey = new SecretKeySpec(integrityKeyBytes, HMAC_ALGORITHM);

            return new SecretKeys(confidentialityKey, integrityKey);
        }

        /**
         * A function that generates password-based AES & HMAC keys. It prints out exceptions but
         * doesn't throw them since none should be encountered. If they are encountered, the return
         * value is null.
         *
         * @param password The password to derive the keys from.
         * @return The AES & HMAC keys.
         * @throws GeneralSecurityException if AES is not implemented on this system, or a suitable
         *                                  RNG is not available
         */
        public static SecretKeys generateKeyFromPassword(String password, byte[] salt) throws
                GeneralSecurityException {
            fixPrng();
            //Get enough random bytes for both the AES key and the HMAC key:
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                    PBE_ITERATION_COUNT, AES_KEY_LENGTH_BITS + HMAC_KEY_LENGTH_BITS);
            SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance(PBE_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();

            // Split the random bytes into two parts:
            byte[] confidentialityKeyBytes = copyOfRange(keyBytes, 0, AES_KEY_LENGTH_BITS / 8);
            byte[] integrityKeyBytes = copyOfRange(keyBytes, AES_KEY_LENGTH_BITS / 8,
                    AES_KEY_LENGTH_BITS / 8 + HMAC_KEY_LENGTH_BITS / 8);

            //Generate the AES key
            SecretKey confidentialityKey = new SecretKeySpec(confidentialityKeyBytes, CIPHER);

            //Generate the HMAC key
            SecretKey integrityKey = new SecretKeySpec(integrityKeyBytes, HMAC_ALGORITHM);

            return new SecretKeys(confidentialityKey, integrityKey);
        }

        /**
         * A function that generates password-based AES & HMAC keys. See generateKeyFromPassword.
         *
         * @param password The password to derive the AES/HMAC keys from
         * @return The AES & HMAC keys.
         */
        public static SecretKeys generateKeyFromPassword(String password) throws
                GeneralSecurityException {
            return generateKeyFromPassword(password, generateSalt());
        }

        /**
         * Generates a random salt.
         *
         * @return The random salt suitable for generateKeyFromPassword.
         */
        public static byte[] generateSalt() throws GeneralSecurityException {
            return randomBytes(PBE_SALT_LENGTH_BITS);
        }

        /**
         * Converts the given salt into a base64 encoded string suitable for storage.
         *
         * @return a base 64 encoded salt string suitable to pass into generateKeyFromPassword.
         */
        public static String saltString(byte[] salt) {
            return Base64.encodeToString(salt, BASE64_FLAGS);
        }


        /**
         * Creates a random Initialization Vector (IV) of IV_LENGTH_BYTES.
         *
         * @return The byte array of this IV
         * @throws GeneralSecurityException if a suitable RNG is not available
         */
        public static byte[] generateIv() throws GeneralSecurityException {
            return randomBytes(IV_LENGTH_BYTES);
        }

        private static byte[] randomBytes(int length) throws GeneralSecurityException {
            fixPrng();
            SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
            byte[] b = new byte[length];
            random.nextBytes(b);
            return b;
        }

    /*
     * -----------------------------------------------------------------
     * Encryption
     * -----------------------------------------------------------------
     */

        /**
         * Generates a random IV and encrypts this plain text with the given key. Then attaches a
         * hashed MAC, which is contained in the CipherTextIvMac class.
         *
         * @param plaintext  The text that will be encrypted, which will be serialized with UTF-8
         * @param secretKeys The AES & HMAC keys with which to encrypt
         * @return a tuple of the IV, ciphertext, mac
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if UTF-8 is not supported in this system
         */
        public static CipherTextIvMac encrypt(String plaintext, SecretKeys secretKeys)
                throws UnsupportedEncodingException, GeneralSecurityException {
            return encrypt(plaintext, secretKeys, "UTF-8");
        }

        /**
         * Generates a random IV and encrypts this plain text with the given key. Then attaches a
         * hashed MAC, which is contained in the CipherTextIvMac class.
         *
         * @param plaintext  The bytes that will be encrypted
         * @param secretKeys The AES & HMAC keys with which to encrypt
         * @return a tuple of the IV, ciphertext, mac
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if the specified encoding is invalid
         */
        public static CipherTextIvMac encrypt(String plaintext, SecretKeys secretKeys, String
                encoding)
                throws UnsupportedEncodingException, GeneralSecurityException {
            return encrypt(plaintext.getBytes(encoding), secretKeys);
        }

        /**
         * Generates a random IV and encrypts this plain text with the given key. Then attaches a
         * hashed MAC, which is contained in the CipherTextIvMac class.
         *
         * @param plaintext  The text that will be encrypted
         * @param secretKeys The combined AES & HMAC keys with which to encrypt
         * @return a tuple of the IV, ciphertext, mac
         * @throws GeneralSecurityException if AES is not implemented on this system
         */
        public static CipherTextIvMac encrypt(byte[] plaintext, SecretKeys secretKeys)
                throws GeneralSecurityException {
            byte[] iv = generateIv();
            Cipher aesCipherForEncryption = Cipher.getInstance(CIPHER_TRANSFORMATION);
            aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKeys.getConfidentialityKey(),
                    new IvParameterSpec(iv));

        /*
         * Now we get back the IV that will actually be used. Some Android
         * versions do funny stuff w/ the IV, so this is to work around bugs:
         */
            iv = aesCipherForEncryption.getIV();
            byte[] byteCipherText = aesCipherForEncryption.doFinal(plaintext);
            byte[] ivCipherConcat = CipherTextIvMac.ivCipherConcat(iv, byteCipherText);

            byte[] integrityMac = generateMac(ivCipherConcat, secretKeys.getIntegrityKey());
            return new CipherTextIvMac(byteCipherText, iv, integrityMac);
        }

        /**
         * Ensures that the PRNG is fixed. Should be used before generating any keys. Will only run
         * once, and every subsequent call should return immediately.
         */
        private static void fixPrng() {
            if (!prngFixed.get()) {
                synchronized (PrngFixes.class) {
                    if (!prngFixed.get()) {
                        PrngFixes.apply();
                        prngFixed.set(true);
                    }
                }
            }
        }

    /*
     * -----------------------------------------------------------------
     * Decryption
     * -----------------------------------------------------------------
     */

        /**
         * AES CBC decrypt.
         *
         * @param civ        The cipher text, IV, and mac
         * @param secretKeys The AES & HMAC keys
         * @param encoding   The string encoding to use to decode the bytes after decryption
         * @return A string derived from the decrypted bytes (not base64 encoded)
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if the encoding is unsupported
         */
        public static String decryptString(CipherTextIvMac civ, SecretKeys secretKeys, String
                encoding)
                throws UnsupportedEncodingException, GeneralSecurityException {
            return new String(decrypt(civ, secretKeys), encoding);
        }

        /**
         * AES CBC decrypt.
         *
         * @param civ        The cipher text, IV, and mac
         * @param secretKeys The AES & HMAC keys
         * @return A string derived from the decrypted bytes, which are interpreted as a UTF-8
         * String
         * @throws GeneralSecurityException     if AES is not implemented on this system
         * @throws UnsupportedEncodingException if UTF-8 is not supported
         */
        public static String decryptString(CipherTextIvMac civ, SecretKeys secretKeys)
                throws UnsupportedEncodingException, GeneralSecurityException {
            return decryptString(civ, secretKeys, "UTF-8");
        }

        /**
         * AES CBC decrypt.
         *
         * @param civ        the cipher text, iv, and mac
         * @param secretKeys the AES & HMAC keys
         * @return The raw decrypted bytes
         * @throws GeneralSecurityException if MACs don't match or AES is not implemented
         */
        public static byte[] decrypt(CipherTextIvMac civ, SecretKeys secretKeys)
                throws GeneralSecurityException {

            byte[] ivCipherConcat = CipherTextIvMac.ivCipherConcat(civ.getIv(), civ.getCipherText
                    ());
            byte[] computedMac = generateMac(ivCipherConcat, secretKeys.getIntegrityKey());
            if (constantTimeEq(computedMac, civ.getMac())) {
                Cipher aesCipherForDecryption = Cipher.getInstance(CIPHER_TRANSFORMATION);
                aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKeys.getConfidentialityKey(),
                        new IvParameterSpec(civ.getIv()));
                return aesCipherForDecryption.doFinal(civ.getCipherText());
            } else {
                throw new GeneralSecurityException("MAC stored in civ does not match computed MAC" +
                        ".");
            }
        }

    /*
     * -----------------------------------------------------------------
     * Helper Code
     * -----------------------------------------------------------------
     */

        /**
         * Generate the mac based on HMAC_ALGORITHM
         *
         * @param integrityKey   The key used for hmac
         * @param byteCipherText the cipher text
         * @return A byte array of the HMAC for the given key & ciphertext
         */
        public static byte[] generateMac(byte[] byteCipherText, SecretKey integrityKey) throws
                NoSuchAlgorithmException, InvalidKeyException {
            //Now compute the mac for later integrity checking
            Mac sha256_HMAC = Mac.getInstance(HMAC_ALGORITHM);
            sha256_HMAC.init(integrityKey);
            return sha256_HMAC.doFinal(byteCipherText);
        }

        /**
         * Holder class that has both the secret AES key for encryption (confidentiality) and the
         * secret HMAC key for integrity.
         */

        public static class SecretKeys {
            private SecretKey confidentialityKey;
            private SecretKey integrityKey;

            /**
             * Construct the secret keys container.
             *
             * @param confidentialityKeyIn The AES key
             * @param integrityKeyIn       the HMAC key
             */
            public SecretKeys(SecretKey confidentialityKeyIn, SecretKey integrityKeyIn) {
                setConfidentialityKey(confidentialityKeyIn);
                setIntegrityKey(integrityKeyIn);
            }

            public SecretKey getConfidentialityKey() {
                return confidentialityKey;
            }

            public void setConfidentialityKey(SecretKey confidentialityKey) {
                this.confidentialityKey = confidentialityKey;
            }

            public SecretKey getIntegrityKey() {
                return integrityKey;
            }

            public void setIntegrityKey(SecretKey integrityKey) {
                this.integrityKey = integrityKey;
            }

            /**
             * Encodes the two keys as a string
             *
             * @return base64(confidentialityKey):base64(integrityKey)
             */
            @Override
            public String toString() {
                return Base64.encodeToString(getConfidentialityKey().getEncoded(), BASE64_FLAGS)
                        + ":" + Base64.encodeToString(getIntegrityKey().getEncoded(), BASE64_FLAGS);
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + confidentialityKey.hashCode();
                result = prime * result + integrityKey.hashCode();
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                SecretKeys other = (SecretKeys) obj;
                return integrityKey.equals(other.integrityKey) && confidentialityKey.equals(other
                        .confidentialityKey);
            }
        }


        /**
         * Simple constant-time equality of two byte arrays. Used for security to avoid timing
         * attacks.
         *
         * @return true iff the arrays are exactly equal.
         */
        public static boolean constantTimeEq(byte[] a, byte[] b) {
            if (a.length != b.length) {
                return false;
            }
            int result = 0;
            for (int i = 0; i < a.length; i++) {
                result |= a[i] ^ b[i];
            }
            return result == 0;
        }

        /**
         * Holder class that allows us to bundle ciphertext and IV together.
         */
        public static class CipherTextIvMac {
            private final byte[] cipherText;
            private final byte[] iv;
            private final byte[] mac;

            public byte[] getCipherText() {
                return cipherText;
            }

            public byte[] getIv() {
                return iv;
            }

            public byte[] getMac() {
                return mac;
            }

            /**
             * Construct a new bundle of ciphertext and IV.
             *
             * @param c The ciphertext
             * @param i The IV
             * @param h The mac
             */
            public CipherTextIvMac(byte[] c, byte[] i, byte[] h) {
                cipherText = new byte[c.length];
                System.arraycopy(c, 0, cipherText, 0, c.length);
                iv = new byte[i.length];
                System.arraycopy(i, 0, iv, 0, i.length);
                mac = new byte[h.length];
                System.arraycopy(h, 0, mac, 0, h.length);
            }

            /**
             * Constructs a new bundle of ciphertext and IV from a string of the format
             * <code>base64(iv):base64(ciphertext)</code>.
             *
             * @param base64IvAndCiphertext A string of the format <code>iv:ciphertext</code> The IV
             *                              and ciphertext must each be base64-encoded.
             */
            public CipherTextIvMac(String base64IvAndCiphertext) {
                String[] civArray = base64IvAndCiphertext.split(":");
                if (civArray.length != 3) {
                    throw new IllegalArgumentException("Cannot parse iv:ciphertext:mac");
                } else {
                    iv = Base64.decode(civArray[0], BASE64_FLAGS);
                    mac = Base64.decode(civArray[1], BASE64_FLAGS);
                    cipherText = Base64.decode(civArray[2], BASE64_FLAGS);
                }
            }

            /**
             * Concatinate the IV to the cipherText using array copy. This is used e.g. before
             * computing mac.
             *
             * @param iv         The IV to prepend
             * @param cipherText the cipherText to append
             * @return iv:cipherText, a new byte array.
             */
            public static byte[] ivCipherConcat(byte[] iv, byte[] cipherText) {
                byte[] combined = new byte[iv.length + cipherText.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
                return combined;
            }

            /**
             * Encodes this ciphertext, IV, mac as a string.
             *
             * @return base64(iv) : base64(mac) : base64(ciphertext). The iv and mac go first
             * because they're fixed length.
             */
            @Override
            public String toString() {
                String ivString = Base64.encodeToString(iv, BASE64_FLAGS);
                String cipherTextString = Base64.encodeToString(cipherText, BASE64_FLAGS);
                String macString = Base64.encodeToString(mac, BASE64_FLAGS);
                return ivString + ":" + macString + ":" + cipherTextString;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + Arrays.hashCode(cipherText);
                result = prime * result + Arrays.hashCode(iv);
                result = prime * result + Arrays.hashCode(mac);
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                CipherTextIvMac other = (CipherTextIvMac) obj;
                return Arrays.equals(cipherText, other.cipherText) && Arrays.equals(iv, other.iv)
                        && Arrays.equals(mac, other.mac);
            }
        }

        /**
         * Copy the elements from the start to the end
         *
         * @param from  the source
         * @param start the start index to copy
         * @param end   the end index to finish
         * @return the new buffer
         */
        private static byte[] copyOfRange(byte[] from, int start, int end) {
            int length = end - start;
            byte[] result = new byte[length];
            System.arraycopy(from, start, result, 0, length);
            return result;
        }

        /**
         * Fixes for the RNG as per http://android-developers.blogspot
         * .com/2013/08/some-securerandom-thoughts.html
         *
         * This software is provided 'as-is', without any express or implied warranty. In no event
         * will Google be held liable for any damages arising from the use of this software.
         *
         * Permission is granted to anyone to use this software for any purpose, including
         * commercial applications, and to alter it and redistribute it freely, as long as the
         * origin is not misrepresented.
         *
         * Fixes for the output of the default PRNG having low entropy.
         *
         * The fixes need to be applied via {@link #apply()} before any use of Java Cryptography
         * Architecture primitives. A good place to invoke them is in the application's {@code
         * onCreate}.
         */
        public static final class PrngFixes {

            private static final int VERSION_CODE_JELLY_BEAN = 16;
            private static final int VERSION_CODE_JELLY_BEAN_MR2 = 18;
            private static final byte[] BUILD_FINGERPRINT_AND_DEVICE_SERIAL =
                    getBuildFingerprintAndDeviceSerial();

            /**
             * Hidden constructor to prevent instantiation.
             */
            private PrngFixes() {
            }

            /**
             * Applies all fixes.
             *
             * @throws SecurityException if a fix is needed but could not be applied.
             */
            public static void apply() {
                applyOpenSSLFix();
                installLinuxPRNGSecureRandom();
            }

            /**
             * Applies the fix for OpenSSL PRNG having low entropy. Does nothing if the fix is not
             * needed.
             *
             * @throws SecurityException if the fix is needed but could not be applied.
             */
            private static void applyOpenSSLFix() throws SecurityException {
                if ((Build.VERSION.SDK_INT < VERSION_CODE_JELLY_BEAN)
                        || (Build.VERSION.SDK_INT > VERSION_CODE_JELLY_BEAN_MR2)) {
                    // No need to apply the fix
                    return;
                }

                try {
                    // Mix in the device- and invocation-specific seed.
                    Class.forName("org.apache.harmony.xnet.provider.jsse.NativeCrypto")
                            .getMethod("RAND_seed", byte[].class).invoke(null, new
                            Object[]{generateSeed()});

                    // Mix output of Linux PRNG into OpenSSL's PRNG
                    int bytesRead = (Integer) Class
                            .forName("org.apache.harmony.xnet.provider.jsse.NativeCrypto")
                            .getMethod("RAND_load_file", String.class, long.class)
                            .invoke(null, "/dev/urandom", 1024);
                    if (bytesRead != 1024) {
                        throw new IOException("Unexpected number of bytes read from Linux PRNG: "
                                + bytesRead);
                    }
                } catch (Exception e) {
                    if (ALLOW_BROKEN_PRNG) {
                        Log.w(PrngFixes.class.getSimpleName(), "Failed to seed OpenSSL PRNG", e);
                    } else {
                        throw new SecurityException("Failed to seed OpenSSL PRNG", e);
                    }
                }
            }

            /**
             * Installs a Linux PRNG-backed {@code SecureRandom} implementation as the default. Does
             * nothing if the implementation is already the default or if there is not need to
             * install the implementation.
             *
             * @throws SecurityException if the fix is needed but could not be applied.
             */
            private static void installLinuxPRNGSecureRandom() throws SecurityException {
                if (Build.VERSION.SDK_INT > VERSION_CODE_JELLY_BEAN_MR2) {
                    // No need to apply the fix
                    return;
                }

                // Install a Linux PRNG-based SecureRandom implementation as the
                // default, if not yet installed.
                Provider[] secureRandomProviders = Security.getProviders("SecureRandom.SHA1PRNG");

                // Insert and check the provider atomically.
                // The official Android Java libraries use synchronized methods for
                // insertProviderAt, etc., so synchronizing on the class should
                // make things more stable, and prevent race conditions with other
                // versions of this code.
                synchronized (java.security.Security.class) {
                    if ((secureRandomProviders == null)
                            || (secureRandomProviders.length < 1)
                            || (!secureRandomProviders[0].getClass().getSimpleName().equals
                            ("LinuxPRNGSecureRandomProvider"))) {
                        Security.insertProviderAt(new LinuxPRNGSecureRandomProvider(), 1);
                    }

                    // Assert that new SecureRandom() and
                    // SecureRandom.getInstance("SHA1PRNG") return a SecureRandom backed
                    // by the Linux PRNG-based SecureRandom implementation.
                    SecureRandom rng1 = new SecureRandom();
                    if (!rng1.getProvider().getClass().getSimpleName().equals
                            ("LinuxPRNGSecureRandomProvider")) {
                        if (ALLOW_BROKEN_PRNG) {
                            Log.w(PrngFixes.class.getSimpleName(),
                                    "new SecureRandom() backed by wrong Provider: " + rng1
                                            .getProvider().getClass());
                            return;
                        } else {
                            throw new SecurityException("new SecureRandom() backed by wrong " +
                                    "Provider: "
                                    + rng1.getProvider().getClass());
                        }
                    }

                    SecureRandom rng2;
                    try {
                        rng2 = SecureRandom.getInstance("SHA1PRNG");
                    } catch (NoSuchAlgorithmException e) {
                        if (ALLOW_BROKEN_PRNG) {
                            Log.w(PrngFixes.class.getSimpleName(), "SHA1PRNG not available", e);
                            return;
                        } else {
                            throw new SecurityException("SHA1PRNG not available", e);
                        }
                    }
                    if (!rng2.getProvider().getClass().getSimpleName().equals
                            ("LinuxPRNGSecureRandomProvider")) {
                        if (ALLOW_BROKEN_PRNG) {
                            Log.w(PrngFixes.class.getSimpleName(),
                                    "SecureRandom.getInstance(\"SHA1PRNG\") backed by wrong" + " " +
                                            "Provider: "
                                            + rng2.getProvider().getClass());
                        } else {
                            throw new SecurityException(
                                    "SecureRandom.getInstance(\"SHA1PRNG\") backed by wrong" + " " +
                                            "Provider: "
                                            + rng2.getProvider().getClass());
                        }
                    }
                }
            }

            /**
             * {@code Provider} of {@code SecureRandom} engines which pass through all requests to
             * the Linux PRNG.
             */
            private static class LinuxPRNGSecureRandomProvider extends Provider {

                public LinuxPRNGSecureRandomProvider() {
                    super("LinuxPRNG", 1.0, "A Linux-specific random number provider that uses"
                            + " /dev/urandom");
                    // Although /dev/urandom is not a SHA-1 PRNG, some apps
                    // explicitly request a SHA1PRNG SecureRandom and we thus need
                    // to prevent them from getting the default implementation whose
                    // output may have low entropy.
                    put("SecureRandom.SHA1PRNG", LinuxPRNGSecureRandom.class.getName());
                    put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
                }
            }

            /**
             * {@link SecureRandomSpi} which passes all requests to the Linux PRNG ( {@code
             * /dev/urandom}).
             */
            public static class LinuxPRNGSecureRandom extends SecureRandomSpi {

            /*
             * IMPLEMENTATION NOTE: Requests to generate bytes and to mix in a
             * seed are passed through to the Linux PRNG (/dev/urandom).
             * Instances of this class seed themselves by mixing in the current
             * time, PID, UID, build fingerprint, and hardware serial number
             * (where available) into Linux PRNG.
             *
             * Concurrency: Read requests to the underlying Linux PRNG are
             * serialized (on sLock) to ensure that multiple threads do not get
             * duplicated PRNG output.
             */

                private static final File URANDOM_FILE = new File("/dev/urandom");

                private static final Object sLock = new Object();

                /**
                 * Input stream for reading from Linux PRNG or {@code null} if not yet opened.
                 */
                private static DataInputStream sUrandomIn;

                /**
                 * Output stream for writing to Linux PRNG or {@code null} if not yet opened.
                 */
                private static OutputStream sUrandomOut;

                /**
                 * Whether this engine instance has been seeded. This is needed because each
                 * instance needs to seed itself if the client does not explicitly seed it.
                 */
                private boolean mSeeded;

                @Override
                protected void engineSetSeed(byte[] bytes) {
                    try {
                        OutputStream out;
                        synchronized (sLock) {
                            out = getUrandomOutputStream();
                        }
                        out.write(bytes);
                        out.flush();
                    } catch (IOException e) {
                        // On a small fraction of devices /dev/urandom is not
                        // writable Log and ignore.
                        Log.w(PrngFixes.class.getSimpleName(), "Failed to mix seed into "
                                + URANDOM_FILE);
                    } finally {
                        mSeeded = true;
                    }
                }

                @Override
                protected void engineNextBytes(byte[] bytes) {
                    if (!mSeeded) {
                        // Mix in the device- and invocation-specific seed.
                        engineSetSeed(generateSeed());
                    }

                    try {
                        DataInputStream in;
                        synchronized (sLock) {
                            in = getUrandomInputStream();
                        }
                        in.readFully(bytes);
                    } catch (IOException e) {
                        throw new SecurityException("Failed to read from " + URANDOM_FILE, e);
                    }
                }

                @Override
                protected byte[] engineGenerateSeed(int size) {
                    byte[] seed = new byte[size];
                    engineNextBytes(seed);
                    return seed;
                }

                private DataInputStream getUrandomInputStream() {
                    synchronized (sLock) {
                        if (sUrandomIn == null) {
                            // NOTE: Consider inserting a BufferedInputStream
                            // between DataInputStream and FileInputStream if you need
                            // higher PRNG output performance and can live with future PRNG
                            // output being pulled into this process prematurely.
                            try {
                                sUrandomIn = new DataInputStream(new FileInputStream(URANDOM_FILE));
                            } catch (IOException e) {
                                throw new SecurityException("Failed to open " + URANDOM_FILE
                                        + " for reading", e);
                            }
                        }
                        return sUrandomIn;
                    }
                }

                private OutputStream getUrandomOutputStream() throws IOException {
                    synchronized (sLock) {
                        if (sUrandomOut == null) {
                            sUrandomOut = new FileOutputStream(URANDOM_FILE);
                        }
                        return sUrandomOut;
                    }
                }
            }

            /**
             * Generates a device- and invocation-specific seed to be mixed into the Linux PRNG.
             */
            private static byte[] generateSeed() {
                try {
                    ByteArrayOutputStream seedBuffer = new ByteArrayOutputStream();
                    DataOutputStream seedBufferOut = new DataOutputStream(seedBuffer);
                    seedBufferOut.writeLong(System.currentTimeMillis());
                    seedBufferOut.writeLong(System.nanoTime());
                    seedBufferOut.writeInt(Process.myPid());
                    seedBufferOut.writeInt(Process.myUid());
                    seedBufferOut.write(BUILD_FINGERPRINT_AND_DEVICE_SERIAL);
                    seedBufferOut.close();
                    return seedBuffer.toByteArray();
                } catch (IOException e) {
                    throw new SecurityException("Failed to generate seed", e);
                }
            }

            /**
             * Gets the hardware serial number of this device.
             *
             * @return serial number or {@code null} if not available.
             */
            private static String getDeviceSerialNumber() {
                // We're using the Reflection API because Build.SERIAL is only
                // available since API Level 9 (Gingerbread, Android 2.3).
                try {
                    return (String) Build.class.getField("SERIAL").get(null);
                } catch (Exception ignored) {
                    return null;
                }
            }

            private static byte[] getBuildFingerprintAndDeviceSerial() {
                StringBuilder result = new StringBuilder();
                String fingerprint = Build.FINGERPRINT;
                if (fingerprint != null) {
                    result.append(fingerprint);
                }
                String serial = getDeviceSerialNumber();
                if (serial != null) {
                    result.append(serial);
                }
                try {
                    return result.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("UTF-8 encoding not supported");
                }
            }
        }
    }

    /**
     * Fast implementation of RSA's MD5 hash generator in Java JDK Beta-2 or higher. <p/> Originally
     * written by Santeri Paavolainen, Helsinki Finland 1996.<br> (c) Santeri Paavolainen, Helsinki
     * Finland 1996<br> Many changes Copyright (c) 2002 - 2005 Timothy W Macinta<br> <p/> This
     * library is free software; you can redistribute it and/or modify it under the terms of the GNU
     * Library General private License as published by the Free Software Foundation; either version
     * 2.1 of the License, or (at your option) any later version. <p/> This library is distributed
     * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
     * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General
     * private License for more details. <p/> You should have received a copy of the GNU Library
     * General private License along with this library; if not, write to the Free Software
     * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. <p/> See
     * http://www.twmacinta.com/myjava/fast_md5.php for more information on this file and the
     * related files. <p/> This was originally a rather straight re-implementation of the reference
     * implementation given in RFC1321 by RSA. It passes the MD5 test suite as defined in RFC1321.
     * <p/> Many optimizations made by Timothy W Macinta. Reduced time to checksum a test file in
     * Java alone to roughly half the time taken compared with java.security.MessageDigest (within
     * an intepretter). Also added an optional native method to reduce the time even further. See
     * http://www.twmacinta.com/myjava/fast_md5.php for further information on the time improvements
     * achieved. <p/> Some bug fixes also made by Timothy W Macinta. <p/> Please note: I (Timothy
     * Macinta) have put this code in the com.twmacinta.util package only because it came without a
     * package. I was not the the original author of the code, although I did optimize it
     * (substantially) and fix some bugs. <p/> This Java class has been derived from the RSA Data
     * Security, Inc. MD5 Message-Digest Algorithm and its reference implementation.
     *
     * @author Santeri Paavolainen <sjpaavol@cc.helsinki.fi>
     * @author Timothy W Macinta (twm@alum.mit.edu) (optimizations and bug fixes)
     * @author Bem Jones-Bey (bem@jones-bey.org) (delete code so it works in MIDP)
     */

    public static class MD5 {

        private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f',};

        /**
         * Padding for Final()
         */
        static byte padding[] = {(byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        /**
         * If Final() has been called, finals is set to the current finals state. Any Update()
         * causes this to be set to null.
         */
        MD5State finals;

        /**
         * MD5 state
         */
        MD5State state;

        /**
         * Class constructor
         */
        private MD5() {

            Init();
        }

        /**
         * Initialize class, and update hash with ob.toString()
         *
         * @param ob Object, ob.toString() is used to update hash after initialization
         */
        @SuppressWarnings("unused")
        private MD5(final Object ob) {

            this();
            Update(ob.toString());
        }

        /**
         * Turns array of bytes into string representing each byte as unsigned hex number.
         *
         * @param hash Array of bytes to convert to hex-string
         * @return Generated hex string
         */
        private static String asHex(final byte hash[]) {

            final char buf[] = new char[hash.length * 2];
            for (int i = 0, x = 0; i < hash.length; i++) {
                buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
                buf[x++] = HEX_CHARS[hash[i] & 0xf];
            }
            return new String(buf);
        }

        /**
         * Creates a MD5 encoded value of the provided string
         *
         * @return String
         */
        public static String createDigest(final String value) {

            if (value == null) {
                return null;
            }
            final MD5 md5 = new MD5();
            try {
                md5.Update(value, null);
            } catch (final UnsupportedEncodingException e) {
                throw new IllegalStateException(
                        "Could not create MD5 digest value, becuase of encoding problem: " + e);
            }
            return md5.asHex();
        }

        /**
         * @return true iff the first 16 bytes of both hash1 and hash2 are equal; both hash1 and
         * hash2 are null; or either hash array is less than 16 bytes in length and their lengths
         * and all of their bytes are equal.
         */
        @SuppressWarnings("unused")
        private static boolean hashesEqual(final byte[] hash1, final byte[] hash2) {

            if (hash1 == null) {
                return hash2 == null;
            }
            if (hash2 == null) {
                return false;
            }
            int targ = 16;
            if (hash1.length < 16) {
                if (hash2.length != hash1.length) {
                    return false;
                }
                targ = hash1.length;
            } else if (hash2.length < 16) {
                return false;
            }
            for (int i = 0; i < targ; i++) {
                if (hash1[i] != hash2[i]) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Checks that the provided MD5 encoded digested string is the same as the not encoded
         * provided value string
         *
         * @return boolean
         */
        @SuppressWarnings("unused")
        public static boolean verifyDigest(final String value, final String digest) {

            try {
                return digest.equals(MD5.createDigest(value));
            } catch (final Exception e) {
                return false;
            }
        }

        /**
         * Returns 32-character hex representation of this objects hash
         *
         * @return String of this object's hash
         */
        private String asHex() {

            return asHex(Final());
        }

	/*
     * Update()s for other datatypes than byte[] also. Update(byte[], int) is only the main driver.
	 */

        private void Decode(final byte buffer[], final int shift, final int[] out) {

		/*
                 * len += shift; for (int i = 0; shift < len; i++, shift += 4) { out[i] = ((int)
		 * (buffer[shift] & 0xff)) | (((int) (buffer[shift + 1] & 0xff)) << 8) | (((int)
		 * (buffer[shift + 2] & 0xff)) << 16) | (((int) buffer[shift + 3]) << 24); }
		 */

            // unrolled loop (original loop shown above)

            out[0] = (buffer[shift] & 0xff) | ((buffer[shift + 1] & 0xff) << 8)
                    | ((buffer[shift + 2] & 0xff) << 16) | (buffer[shift + 3] << 24);
            out[1] = (buffer[shift + 4] & 0xff) | ((buffer[shift + 5] & 0xff) << 8)
                    | ((buffer[shift + 6] & 0xff) << 16) | (buffer[shift + 7] << 24);
            out[2] = (buffer[shift + 8] & 0xff) | ((buffer[shift + 9] & 0xff) << 8)
                    | ((buffer[shift + 10] & 0xff) << 16) | (buffer[shift + 11] << 24);
            out[3] = (buffer[shift + 12] & 0xff) | ((buffer[shift + 13] & 0xff) << 8)
                    | ((buffer[shift + 14] & 0xff) << 16) | (buffer[shift + 15] << 24);
            out[4] = (buffer[shift + 16] & 0xff) | ((buffer[shift + 17] & 0xff) << 8)
                    | ((buffer[shift + 18] & 0xff) << 16) | (buffer[shift + 19] << 24);
            out[5] = (buffer[shift + 20] & 0xff) | ((buffer[shift + 21] & 0xff) << 8)
                    | ((buffer[shift + 22] & 0xff) << 16) | (buffer[shift + 23] << 24);
            out[6] = (buffer[shift + 24] & 0xff) | ((buffer[shift + 25] & 0xff) << 8)
                    | ((buffer[shift + 26] & 0xff) << 16) | (buffer[shift + 27] << 24);
            out[7] = (buffer[shift + 28] & 0xff) | ((buffer[shift + 29] & 0xff) << 8)
                    | ((buffer[shift + 30] & 0xff) << 16) | (buffer[shift + 31] << 24);
            out[8] = (buffer[shift + 32] & 0xff) | ((buffer[shift + 33] & 0xff) << 8)
                    | ((buffer[shift + 34] & 0xff) << 16) | (buffer[shift + 35] << 24);
            out[9] = (buffer[shift + 36] & 0xff) | ((buffer[shift + 37] & 0xff) << 8)
                    | ((buffer[shift + 38] & 0xff) << 16) | (buffer[shift + 39] << 24);
            out[10] = (buffer[shift + 40] & 0xff) | ((buffer[shift + 41] & 0xff) << 8)
                    | ((buffer[shift + 42] & 0xff) << 16) | (buffer[shift + 43] << 24);
            out[11] = (buffer[shift + 44] & 0xff) | ((buffer[shift + 45] & 0xff) << 8)
                    | ((buffer[shift + 46] & 0xff) << 16) | (buffer[shift + 47] << 24);
            out[12] = (buffer[shift + 48] & 0xff) | ((buffer[shift + 49] & 0xff) << 8)
                    | ((buffer[shift + 50] & 0xff) << 16) | (buffer[shift + 51] << 24);
            out[13] = (buffer[shift + 52] & 0xff) | ((buffer[shift + 53] & 0xff) << 8)
                    | ((buffer[shift + 54] & 0xff) << 16) | (buffer[shift + 55] << 24);
            out[14] = (buffer[shift + 56] & 0xff) | ((buffer[shift + 57] & 0xff) << 8)
                    | ((buffer[shift + 58] & 0xff) << 16) | (buffer[shift + 59] << 24);
            out[15] = (buffer[shift + 60] & 0xff) | ((buffer[shift + 61] & 0xff) << 8)
                    | ((buffer[shift + 62] & 0xff) << 16) | (buffer[shift + 63] << 24);
        }

        private byte[] Encode(final int input[], final int len) {

            int i, j;
            byte out[];

            out = new byte[len];

            for (i = j = 0; j < len; i++, j += 4) {
                out[j] = (byte) (input[i] & 0xff);
                out[j + 1] = (byte) ((input[i] >>> 8) & 0xff);
                out[j + 2] = (byte) ((input[i] >>> 16) & 0xff);
                out[j + 3] = (byte) ((input[i] >>> 24) & 0xff);
            }

            return out;
        }

        /**
         * Returns array of bytes (16 bytes) representing hash as of the current state of this
         * object. Note: getting a hash does not invalidate the hash object, it only creates a copy
         * of the real state which is finalized.
         *
         * @return Array of 16 bytes, the hash of all updated bytes
         */
        private synchronized byte[] Final() {

            byte bits[];
            int index, padlen;
            MD5State fin;

            if (finals == null) {
                fin = new MD5State(state);

                final int[] count_ints = {(int) (fin.count << 3), (int) (fin.count >> 29)};
                bits = Encode(count_ints, 8);

                index = (int) (fin.count & 0x3f);
                padlen = (index < 56) ? (56 - index) : (120 - index);

                Update(fin, padding, 0, padlen);
                Update(fin, bits, 0, 8);

			/* Update() sets finals to null */
                finals = fin;
            }

            return Encode(finals.state, 16);
        }

        /**
         * Initialize MD5 internal state (object can be reused just by calling Init() after every
         * Final()
         */
        private synchronized void Init() {

            state = new MD5State();
            finals = null;
        }

        private void Transform(final MD5State state, final byte buffer[], final int shift,
                               final int[] decode_buf) {

            int a = state.state[0], b = state.state[1], c = state.state[2], d = state.state[3];

            Decode(buffer, shift, decode_buf);

		/* Round 1 */
            a += ((b & c) | (~b & d)) + decode_buf[0] + 0xd76aa478; /* 1 */
            a = ((a << 7) | (a >>> 25)) + b;
            d += ((a & b) | (~a & c)) + decode_buf[1] + 0xe8c7b756; /* 2 */
            d = ((d << 12) | (d >>> 20)) + a;
            c += ((d & a) | (~d & b)) + decode_buf[2] + 0x242070db; /* 3 */
            c = ((c << 17) | (c >>> 15)) + d;
            b += ((c & d) | (~c & a)) + decode_buf[3] + 0xc1bdceee; /* 4 */
            b = ((b << 22) | (b >>> 10)) + c;

            a += ((b & c) | (~b & d)) + decode_buf[4] + 0xf57c0faf; /* 5 */
            a = ((a << 7) | (a >>> 25)) + b;
            d += ((a & b) | (~a & c)) + decode_buf[5] + 0x4787c62a; /* 6 */
            d = ((d << 12) | (d >>> 20)) + a;
            c += ((d & a) | (~d & b)) + decode_buf[6] + 0xa8304613; /* 7 */
            c = ((c << 17) | (c >>> 15)) + d;
            b += ((c & d) | (~c & a)) + decode_buf[7] + 0xfd469501; /* 8 */
            b = ((b << 22) | (b >>> 10)) + c;

            a += ((b & c) | (~b & d)) + decode_buf[8] + 0x698098d8; /* 9 */
            a = ((a << 7) | (a >>> 25)) + b;
            d += ((a & b) | (~a & c)) + decode_buf[9] + 0x8b44f7af; /* 10 */
            d = ((d << 12) | (d >>> 20)) + a;
            c += ((d & a) | (~d & b)) + decode_buf[10] + 0xffff5bb1; /* 11 */
            c = ((c << 17) | (c >>> 15)) + d;
            b += ((c & d) | (~c & a)) + decode_buf[11] + 0x895cd7be; /* 12 */
            b = ((b << 22) | (b >>> 10)) + c;

            a += ((b & c) | (~b & d)) + decode_buf[12] + 0x6b901122; /* 13 */
            a = ((a << 7) | (a >>> 25)) + b;
            d += ((a & b) | (~a & c)) + decode_buf[13] + 0xfd987193; /* 14 */
            d = ((d << 12) | (d >>> 20)) + a;
            c += ((d & a) | (~d & b)) + decode_buf[14] + 0xa679438e; /* 15 */
            c = ((c << 17) | (c >>> 15)) + d;
            b += ((c & d) | (~c & a)) + decode_buf[15] + 0x49b40821; /* 16 */
            b = ((b << 22) | (b >>> 10)) + c;

		/* Round 2 */
            a += ((b & d) | (c & ~d)) + decode_buf[1] + 0xf61e2562; /* 17 */
            a = ((a << 5) | (a >>> 27)) + b;
            d += ((a & c) | (b & ~c)) + decode_buf[6] + 0xc040b340; /* 18 */
            d = ((d << 9) | (d >>> 23)) + a;
            c += ((d & b) | (a & ~b)) + decode_buf[11] + 0x265e5a51; /* 19 */
            c = ((c << 14) | (c >>> 18)) + d;
            b += ((c & a) | (d & ~a)) + decode_buf[0] + 0xe9b6c7aa; /* 20 */
            b = ((b << 20) | (b >>> 12)) + c;

            a += ((b & d) | (c & ~d)) + decode_buf[5] + 0xd62f105d; /* 21 */
            a = ((a << 5) | (a >>> 27)) + b;
            d += ((a & c) | (b & ~c)) + decode_buf[10] + 0x02441453; /* 22 */
            d = ((d << 9) | (d >>> 23)) + a;
            c += ((d & b) | (a & ~b)) + decode_buf[15] + 0xd8a1e681; /* 23 */
            c = ((c << 14) | (c >>> 18)) + d;
            b += ((c & a) | (d & ~a)) + decode_buf[4] + 0xe7d3fbc8; /* 24 */
            b = ((b << 20) | (b >>> 12)) + c;

            a += ((b & d) | (c & ~d)) + decode_buf[9] + 0x21e1cde6; /* 25 */
            a = ((a << 5) | (a >>> 27)) + b;
            d += ((a & c) | (b & ~c)) + decode_buf[14] + 0xc33707d6; /* 26 */
            d = ((d << 9) | (d >>> 23)) + a;
            c += ((d & b) | (a & ~b)) + decode_buf[3] + 0xf4d50d87; /* 27 */
            c = ((c << 14) | (c >>> 18)) + d;
            b += ((c & a) | (d & ~a)) + decode_buf[8] + 0x455a14ed; /* 28 */
            b = ((b << 20) | (b >>> 12)) + c;

            a += ((b & d) | (c & ~d)) + decode_buf[13] + 0xa9e3e905; /* 29 */
            a = ((a << 5) | (a >>> 27)) + b;
            d += ((a & c) | (b & ~c)) + decode_buf[2] + 0xfcefa3f8; /* 30 */
            d = ((d << 9) | (d >>> 23)) + a;
            c += ((d & b) | (a & ~b)) + decode_buf[7] + 0x676f02d9; /* 31 */
            c = ((c << 14) | (c >>> 18)) + d;
            b += ((c & a) | (d & ~a)) + decode_buf[12] + 0x8d2a4c8a; /* 32 */
            b = ((b << 20) | (b >>> 12)) + c;

		/* Round 3 */
            a += (b ^ c ^ d) + decode_buf[5] + 0xfffa3942; /* 33 */
            a = ((a << 4) | (a >>> 28)) + b;
            d += (a ^ b ^ c) + decode_buf[8] + 0x8771f681; /* 34 */
            d = ((d << 11) | (d >>> 21)) + a;
            c += (d ^ a ^ b) + decode_buf[11] + 0x6d9d6122; /* 35 */
            c = ((c << 16) | (c >>> 16)) + d;
            b += (c ^ d ^ a) + decode_buf[14] + 0xfde5380c; /* 36 */
            b = ((b << 23) | (b >>> 9)) + c;

            a += (b ^ c ^ d) + decode_buf[1] + 0xa4beea44; /* 37 */
            a = ((a << 4) | (a >>> 28)) + b;
            d += (a ^ b ^ c) + decode_buf[4] + 0x4bdecfa9; /* 38 */
            d = ((d << 11) | (d >>> 21)) + a;
            c += (d ^ a ^ b) + decode_buf[7] + 0xf6bb4b60; /* 39 */
            c = ((c << 16) | (c >>> 16)) + d;
            b += (c ^ d ^ a) + decode_buf[10] + 0xbebfbc70; /* 40 */
            b = ((b << 23) | (b >>> 9)) + c;

            a += (b ^ c ^ d) + decode_buf[13] + 0x289b7ec6; /* 41 */
            a = ((a << 4) | (a >>> 28)) + b;
            d += (a ^ b ^ c) + decode_buf[0] + 0xeaa127fa; /* 42 */
            d = ((d << 11) | (d >>> 21)) + a;
            c += (d ^ a ^ b) + decode_buf[3] + 0xd4ef3085; /* 43 */
            c = ((c << 16) | (c >>> 16)) + d;
            b += (c ^ d ^ a) + decode_buf[6] + 0x04881d05; /* 44 */
            b = ((b << 23) | (b >>> 9)) + c;

            a += (b ^ c ^ d) + decode_buf[9] + 0xd9d4d039; /* 33 */
            a = ((a << 4) | (a >>> 28)) + b;
            d += (a ^ b ^ c) + decode_buf[12] + 0xe6db99e5; /* 34 */
            d = ((d << 11) | (d >>> 21)) + a;
            c += (d ^ a ^ b) + decode_buf[15] + 0x1fa27cf8; /* 35 */
            c = ((c << 16) | (c >>> 16)) + d;
            b += (c ^ d ^ a) + decode_buf[2] + 0xc4ac5665; /* 36 */
            b = ((b << 23) | (b >>> 9)) + c;

		/* Round 4 */
            a += (c ^ (b | ~d)) + decode_buf[0] + 0xf4292244; /* 49 */
            a = ((a << 6) | (a >>> 26)) + b;
            d += (b ^ (a | ~c)) + decode_buf[7] + 0x432aff97; /* 50 */
            d = ((d << 10) | (d >>> 22)) + a;
            c += (a ^ (d | ~b)) + decode_buf[14] + 0xab9423a7; /* 51 */
            c = ((c << 15) | (c >>> 17)) + d;
            b += (d ^ (c | ~a)) + decode_buf[5] + 0xfc93a039; /* 52 */
            b = ((b << 21) | (b >>> 11)) + c;

            a += (c ^ (b | ~d)) + decode_buf[12] + 0x655b59c3; /* 53 */
            a = ((a << 6) | (a >>> 26)) + b;
            d += (b ^ (a | ~c)) + decode_buf[3] + 0x8f0ccc92; /* 54 */
            d = ((d << 10) | (d >>> 22)) + a;
            c += (a ^ (d | ~b)) + decode_buf[10] + 0xffeff47d; /* 55 */
            c = ((c << 15) | (c >>> 17)) + d;
            b += (d ^ (c | ~a)) + decode_buf[1] + 0x85845dd1; /* 56 */
            b = ((b << 21) | (b >>> 11)) + c;

            a += (c ^ (b | ~d)) + decode_buf[8] + 0x6fa87e4f; /* 57 */
            a = ((a << 6) | (a >>> 26)) + b;
            d += (b ^ (a | ~c)) + decode_buf[15] + 0xfe2ce6e0; /* 58 */
            d = ((d << 10) | (d >>> 22)) + a;
            c += (a ^ (d | ~b)) + decode_buf[6] + 0xa3014314; /* 59 */
            c = ((c << 15) | (c >>> 17)) + d;
            b += (d ^ (c | ~a)) + decode_buf[13] + 0x4e0811a1; /* 60 */
            b = ((b << 21) | (b >>> 11)) + c;

            a += (c ^ (b | ~d)) + decode_buf[4] + 0xf7537e82; /* 61 */
            a = ((a << 6) | (a >>> 26)) + b;
            d += (b ^ (a | ~c)) + decode_buf[11] + 0xbd3af235; /* 62 */
            d = ((d << 10) | (d >>> 22)) + a;
            c += (a ^ (d | ~b)) + decode_buf[2] + 0x2ad7d2bb; /* 63 */
            c = ((c << 15) | (c >>> 17)) + d;
            b += (d ^ (c | ~a)) + decode_buf[9] + 0xeb86d391; /* 64 */
            b = ((b << 21) | (b >>> 11)) + c;

            state.state[0] += a;
            state.state[1] += b;
            state.state[2] += c;
            state.state[3] += d;
        }

        /**
         * Updates hash with given array of bytes
         *
         * @param buffer Array of bytes to use for updating the hash
         */
        @SuppressWarnings("unused")
        private void Update(final byte buffer[]) {

            Update(buffer, 0, buffer.length);
        }

        /**
         * Updates hash with a single byte
         *
         * @param b Single byte to update the hash
         */
        private void Update(final byte b) {

            final byte buffer[] = new byte[1];
            buffer[0] = b;

            Update(buffer, 1);
        }

        private void Update(final byte buffer[], final int length) {

            Update(state, buffer, 0, length);
        }

        /**
         * Plain update, updates this object
         */

        private void Update(final byte buffer[], final int offset, final int length) {

            Update(state, buffer, offset, length);
        }

        /**
         * Update buffer with a single integer (only & 0xff part is used, as a byte)
         *
         * @param i Integer value, which is then converted to byte as i & 0xff
         */

        @SuppressWarnings("unused")
        private void Update(final int i) {

            Update((byte) (i & 0xff));
        }

        /**
         * Updates hash with the bytebuffer given (using at maximum length bytes from that buffer)
         *
         * @param stat   Which state is updated
         * @param buffer Array of bytes to be hashed
         * @param offset Offset to buffer array
         * @param length Use at maximum `length' bytes (absolute maximum is buffer.length)
         */
        private void Update(final MD5State stat, final byte buffer[], final int offset, int
                length) {

            int index, partlen, i, start;
            finals = null;

		/* Length can be told to be shorter, but not inter */
            if ((length - offset) > buffer.length) {
                length = buffer.length - offset;
            }

		/* compute number of bytes mod 64 */

            index = (int) (stat.count & 0x3f);
            stat.count += length;

            partlen = 64 - index;

            if (length >= partlen) {
                // update state (using only Java) to reflect input

                final int[] decode_buf = new int[16];
                if (partlen == 64) {
                    partlen = 0;
                } else {
                    for (i = 0; i < partlen; i++) {
                        stat.buffer[i + index] = buffer[i + offset];
                    }
                    Transform(stat, stat.buffer, 0, decode_buf);
                }
                for (i = partlen; (i + 63) < length; i += 64) {
                    Transform(stat, buffer, i + offset, decode_buf);
                }
                index = 0;
            } else {
                i = 0;
            }

		/* buffer remaining input */
            if (i < length) {
                start = i;
                for (; i < length; i++) {
                    stat.buffer[index + i - start] = buffer[i + offset];
                }
            }
        }

        /**
         * Update buffer with given string. Note that because the version of the s.getBytes() method
         * without parameters is used to convert the string to a byte array, the results of this
         * method may be different on different platforms. The s.getBytes() method converts the
         * string into a byte array using the current platform's default character set and may
         * therefore have different results on platforms with different default character sets. If a
         * version that works consistently across platforms with different default character sets is
         * desired, use the overloaded version of the Update() method which takes a string and a
         * character encoding.
         *
         * @param s String to be update to hash (is used as s.getBytes())
         */
        private void Update(final String s) {

            final byte chars[] = s.getBytes();
            Update(chars, chars.length);
        }

        /**
         * Update buffer with given string using the given encoding. If the given encoding is null,
         * the encoding "ISO8859_1" is used.
         *
         * @param s            String to be update to hash (is used as s.getBytes(charset_name))
         * @param charset_name The character set to use to convert s to a byte array, or null if the
         *                     "ISO8859_1" character set is desired.
         * @throws java.io.UnsupportedEncodingException If the named charset is not supported.
         */
        private void Update(final String s, String charset_name)
                throws UnsupportedEncodingException {

            if (charset_name == null) {
                charset_name = "ISO8859_1";
            }
            final byte chars[] = s.getBytes(charset_name);
            Update(chars, chars.length);
        }

        class MD5State {

            /**
             * 64-byte buffer (512 bits) for storing to-be-hashed characters
             */
            byte buffer[];

            /**
             * 64-bit character count
             */
            long count;

            /**
             * 128-bit state
             */
            int state[];

            private MD5State() {

                buffer = new byte[64];
                count = 0;
                state = new int[4];

                state[0] = 0x67452301;
                state[1] = 0xefcdab89;
                state[2] = 0x98badcfe;
                state[3] = 0x10325476;

            }

            /**
             * Create this State as a copy of another state
             */
            private MD5State(final MD5State from) {

                this();

                int i;

                for (i = 0; i < buffer.length; i++) {
                    buffer[i] = from.buffer[i];
                }

                for (i = 0; i < state.length; i++) {
                    state[i] = from.state[i];
                }

                count = from.count;
            }
        }
    }
}
