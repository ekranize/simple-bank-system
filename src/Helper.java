import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;

public final class Helper {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String decrypt (String string, String encryptPass) {
        String salt = "12345678";
        SecretKey key = getKeyFromPassword(encryptPass,salt);
        String algorithm = "AES";
        byte[] plainText;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE,key);
            plainText = cipher.doFinal(Base64.getDecoder().decode(string));
            return new String(plainText);
        } catch (BadPaddingException bpEx) {
            return "Decrypting error (BadPaddingException)";
        }
        catch (Exception ex) {
            return "Decrypting error (other Exception)";
        }

    }
    public static SecretKey getKeyFromPassword(String password, String salt) {
        SecretKey secret = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
            secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return secret;

    }
    public static String makeMD5 (String message) {
        byte[] digest = {};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message.getBytes());
            digest = md.digest();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bytesToHex(digest);
    }
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static String encrypt (String string) {
        String password = "testPass";
        String salt = "12345678";
        SecretKey key = getKeyFromPassword(password,salt);
        String algorithm = "AES";
        byte[] cipherText = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            cipherText = cipher.doFinal(string.getBytes());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }
}
