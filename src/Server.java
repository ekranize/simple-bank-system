import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;

public class Server {
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean isStarted = false;
    public void startServer (int portNum) {
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
    static class ClientHandler implements Runnable {
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
                System.out.println("Read request \"" + message + "\"");
                writer.println(requestProcessing(message));
                writer.flush();
                sock.close();
                System.out.println("Connection closed");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        private String requestProcessing(String request) {
            String[] requestArray = request.split(";");
            return "Your request is: " + requestArray[0] + ";" + requestArray[1] + ";" + requestArray[2];
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
