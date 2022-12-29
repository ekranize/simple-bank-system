import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean isStarted = false;
    private String encryptPass;
    public void startServer (int portNum, String encryptPass) {
        this.encryptPass = encryptPass;
        try {
            serverSocket = new ServerSocket(portNum);
            serverSocket.setSoTimeout(2000);
            serverThread = new Thread(new ServerHandler());
            serverThread.start();
            System.out.println("Server started on port " + portNum);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        isStarted = true;
    }
    public void stopServer () {
        try {
            serverThread.interrupt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        isStarted = false;
    }

    public boolean isStarted() {
        return isStarted;
    }
    class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        public ClientHandler(Socket clientSocket) {
            sock = clientSocket;
        }
        public void run() {
            try {
                String message;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                PrintWriter writer = new PrintWriter(sock.getOutputStream());
                reader = new BufferedReader(isReader);
                message = reader.readLine();
                writer.println(requestProcessing(message));
                writer.flush();
                reader.close();
                writer.close();
                sock.close();
                System.out.println("Connection closed");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        private String requestProcessing(String request) {
            System.out.println("Read request " + request);
            String decryptedRequest = Helper.decrypt(request,encryptPass);
            System.out.println("Decrypted request " + decryptedRequest);
            String[] requestArray = decryptedRequest.split(";");
            StringBuilder response = new StringBuilder();
            for (String s : requestArray) {
                response.append(s).append(";");
            }
            if (!response.toString().equals("") && requestArray[0].equals("simple-bank-system")) {
                return response.toString();
            } else return "Empty or bad request";


        }
    }
    class ServerHandler implements Runnable {
        @Override
        public void run() {
            try {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    Thread t = new Thread(new ClientHandler(clientSocket));
                    t.start();
                    System.out.println("Got a connection");
                }
            } catch (SocketTimeoutException stoEx) {
                if (!serverThread.isInterrupted()) run();
                else {
                    try {
                        serverSocket.close();
                        System.out.println("Server stopped (interrupted)");
                    } catch (IOException ioEx) {
                        ioEx.printStackTrace();
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
