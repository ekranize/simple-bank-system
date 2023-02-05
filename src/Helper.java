import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;
//класс со вспомогательными для всего проекта методами
public final class Helper {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray(); //"константа"-массив для перевода байт в 16-чные символы
    public static String decrypt (String string, String encryptPass) { //метод для расшифрования сообщений, на входе - сообщение и пароль
        String salt = "12345678"; //"соль" для алгоритма
        SecretKey key = getKeyFromPassword(encryptPass,salt); //создаем итоговый ключ для шифрования из пароля и "соли"
        String algorithm = "AES"; //алгоритм шифрования
        byte[] plainText; //массив с результатом расшифрования
        try {
            Cipher cipher = Cipher.getInstance(algorithm); //создаем экземпляр класса для расшифрования
            cipher.init(Cipher.DECRYPT_MODE,key); //инициализируем класс для расшифрования
            plainText = cipher.doFinal(Base64.getDecoder().decode(string)); //расшифровываем сообщение
            return new String(plainText); //возвращаем результат, преобразованный в строку
        } catch (BadPaddingException bpEx) {
            return "Decrypting error (BadPaddingException)";
        }
        catch (Exception ex) {
            return "Decrypting error (other Exception)";
        }

    }
    public static SecretKey getKeyFromPassword(String password, String salt) { //метод для создания итогового ключа для шифрования из пароля и "соли"
        SecretKey secret = null; //ссылка на экземпляр класса SecretKey - в него запишем результат итогового ключа
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //???
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256); //???
            secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //???
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return secret; //возвращаем итоговый ключ

    }
    public static String makeMD5 (String message) { //метод для создания хэш MD5 из строки, на входе - строка
        byte[] digest = {}; //массив для результата
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  //создаем экземпляр класса MessageDigest для создания хэш MD5
            md.update(message.getBytes()); //???
            digest = md.digest(); //???
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bytesToHex(digest); //переводим байты в 16-чные символы и возвращаем результат
    }
    public static String bytesToHex(byte[] bytes) { //метод для перевода байтов в 16-чные символы, на входе байты
        char[] hexChars = new char[bytes.length * 2]; // массив char для результата - количество элементов в 2 раза больше, чем байтов
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF; //???
            hexChars[j * 2] = HEX_ARRAY[v >>> 4]; //???
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F]; //???
        }
        return new String(hexChars); //возвращаем массив, переведенный в строку
    }
    public static String encrypt (String string) { //метод для зашифрования сообщений, на входе - сообщение и пароль
        String password = "testPass"; //пароль для зашифрования
        String salt = "12345678"; //"соль" для алгоритма
        SecretKey key = getKeyFromPassword(password,salt); //создаем итоговый ключ для зашифрования из пароля и "соли"
        String algorithm = "AES"; //алгоритм шифрования
        byte[] cipherText = null; //массив с результатом зашифрования
        try {
            Cipher cipher = Cipher.getInstance(algorithm); //создаем экземпляр класса для зашифрования
            cipher.init(Cipher.ENCRYPT_MODE,key); //инициализируем класс для зашифрования
            cipherText = cipher.doFinal(string.getBytes()); //зашифровываем сообщение

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText); //возвращаем результат, преобразованный в строку
    }
}
