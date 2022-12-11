import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) {
        PrintWriter writer = null;
        try {
            Socket sock = new Socket("127.0.0.1", 12021);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            if (writer != null) {
                writer.println("test");
                writer.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
