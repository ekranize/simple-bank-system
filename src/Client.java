import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private JTextField responseField;
    public Client (JTextField responseField) {
        this.responseField = responseField;
    }
    public void testConnection () {
        String response;
        BufferedReader reader;
        Socket sock;
        PrintWriter writer;
        InputStreamReader isReader;
            try {
            sock = new Socket("127.0.0.1", 12021);
            writer = new PrintWriter(sock.getOutputStream());
            isReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(isReader);
            System.out.println("networking established");
            String request = "simple-bank-system;" + "user1" + ";" + Helper.makeMD5("password1") + ";password changing";
            System.out.println("request " + request);
            String encryptedRequest = Helper.encrypt(request);
            System.out.println("Sending encrypted request " + encryptedRequest);
            writer.println(encryptedRequest);
            writer.flush();
                response = reader.readLine();
            System.out.println("Read response " + response);
            responseField.setText(response);
            reader.close();
            isReader.close();
            writer.close();
            sock.close();
            System.out.println("Connection closed");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
