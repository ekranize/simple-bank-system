import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;

public class ClientMain {
    public static void main(String[] args) {
        try {
            String message;
            BufferedReader reader;
            Socket sock = new Socket("127.0.0.1", 12021);
            PrintWriter writer = new PrintWriter(sock.getOutputStream());
            InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(isReader);
            System.out.println("networking established");
            String request = "user1;" + makeMD5("password1") + ";password changing";
            System.out.println("Sending request " + request);
            writer.println("user1;" + makeMD5("password1") + ";password changing");
            writer.flush();
            message = reader.readLine();
            System.out.println("Read response " + message);
            sock.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static String makeMD5 (String message) {
        byte[] digest = {};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message.getBytes());
            digest = md.digest();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return digest.toString();
    }
}
