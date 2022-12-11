import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean isStarted = false;
    public void startServer (int portNum) {
        try {
            serverSocket = new ServerSocket(portNum);
            serverThread = new Thread(new ServerHandler(serverSocket));
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
            serverSocket = null;
            System.out.println("Server stopped");
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
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    class ServerHandler implements Runnable {
        private ServerSocket serverSocket;
        public ServerHandler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    //PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                    Thread t = new Thread(new ClientHandler(clientSocket));
                    t.start();
                    System.out.println("Got a connection");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
