import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

//класс банковского сервера
public class Server {
    private ServerSocket serverSocket; //ссылка на экземпляр серверного сокета
    private Thread serverThread; //ссылка на экземпляр серверного потока
    private boolean isStarted = false; //признак работы сервера (запущен/остановлен)
    private String encryptPass; //строка с паролем для шифрования трафика
    private String salt;
    private String algorithm;
    private int soTimeout;
    private String workString; //рабочая строка - начало любого сообщения
    private DBHandler dbHandler = null;
    private static Server instance = null;
    public static synchronized Server getInstance(){  // Используем шаблон одиночка
        if (instance == null)
            instance = new Server();
        return instance;
    }
    private Server() {}
    public void startServer (int portNum, String encryptPass, String workString, int soTimeout, String salt, String algorithm) { //метод для запуска сервера, на входе - пароль для шифрования трафика и номер серверного порта
        this.encryptPass = encryptPass;
        this.salt = salt;
        this.algorithm = algorithm;
        this.workString = workString;
        this.soTimeout = soTimeout;
        try {
            serverSocket = new ServerSocket(portNum); //открываем серверный сокет на нужном порту
            dbHandler = new DBHandler();
        } catch (SQLException ex) {
            System.out.println("Connection to DB failed");
            ex.printStackTrace();
        } catch (IOException ioex) {
            System.out.println("Opening server socket failed");
            ioex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ServerMain.addToLogArea("Server started on port " + portNum); //сообщаем о запуске сервера
        ServerMain.addToLogArea("Connection to DB established");

        isStarted = true; //выставляем признак запуска сервера - запущен
        new ServerHandler().start();
    }
    public void stopServer() { //метод для останова сервера
        if (isStarted) isStarted = false;
        try {
            serverSocket.close();
        } catch (IOException ioex) {
            ServerMain.addToLogArea("Closing server socket failed");
            ioex.printStackTrace();
        }
    }

    public boolean isStarted() { //метод для проверки признака запуска сервера
        return isStarted;
    }
    class ClientHandler extends Thread { //вложенный класс, содержащий задачу для клиентского потока
        private final Socket sock; //ссылка на экземпляр класса клиентского сокета
        private ClientHandler(Socket clientSocket) { //конструктор вложенного класса, на входе - сокет клиента
            this.sock = clientSocket;
        }
        public void run() { //метод - задача для клиентского потока
            try {
                String message; //строка - сообщение от клиента
                PrintWriter writer = new PrintWriter(sock.getOutputStream()); //создаем экземпляр класса для записи данных в сокет
                BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream())); //создаем экземпляр класса для чтения данных из сокета
                while ((message = reader.readLine()) != null) { //в цикле ждем ответа от сервера, принимаем его
                    String processedMessage = requestProcessing(message);
                    writer.println(processedMessage); //пишем в сокет ответ
                    writer.flush(); //принудительно отправляем в сокет то, что записали выше
                }
                writer.close();
                reader.close();
                sock.close();
            } catch (IOException ioex) {
                ServerMain.addToLogArea("Reading requests/writing responses failed");
                ioex.printStackTrace();
            }
        }
        private synchronized String requestProcessing(String request) { //метод для обработки сообщения/запроса клиента, на входе - сообщение/запрос
            System.out.println("Read request " + request);  // выводим в консоль сообщение/запрос
            String decryptedRequest = Helper.decrypt(request,encryptPass, salt, algorithm); //расшифровываем запрос
            System.out.println("Decrypted request " + decryptedRequest);  // выводим в консоль расшифрованный запрос
            String[] requestArray = decryptedRequest.split(";"); //разбиваем запрос на лексемы - части запроса
            if (requestArray.length != 0) {
                if (requestArray[0].equals(workString)) {
                    try {
                        switch (requestArray[1]) {
                            case "connect": {
                                if (dbHandler.checkAuth(requestArray[2], requestArray[3])) {
                                    Thread.currentThread().setName(requestArray[1]);
                                    return "Authorization - OK";
                                }
                            }
                            case "passwordChange": {
                                if (dbHandler.passwordChange(Thread.currentThread().getName(), requestArray[2]))
                                    return "Password change - OK";
                                else return "Password change - FAILED";
                            }
                            case "moneyTransfer": {
                                if (dbHandler.moneyTransfer(Thread.currentThread().getName(), requestArray[2], Integer.parseInt(requestArray[3])))
                                    return "Transferring " + requestArray[3] + " RUR to " + requestArray[2] + " - OK";
                                else
                                    return "Transferring " + requestArray[3] + " RUR to " + requestArray[2] + " - FAILED";
                            }
                            case "balanceCheck": {
                                int balance = dbHandler.balanceCheck(Thread.currentThread().getName());
                                if (balance > 0)
                                    return "Your balance - " + balance;
                                else return "Balance checking - FAILED";
                            }
                            case "registration": {
                                if (dbHandler.registerClient(requestArray[1], requestArray[2]))
                                    return "Registration - OK";
                                else return "Registration - FAILED";
                            }
                            default:
                                return "Bad request";
                        }
                    } catch (SQLException sqlex) {
                        sqlex.printStackTrace();
                        return "SQL Exception";
                    }
                } else return "Bad request";
            }
            return "Empty request";
        }
    }
    class ServerHandler extends Thread { //вложенный класс, содержащий задачу для серверного потока
        public void run() { //метод - задача для серверного потока
            while (isStarted) {
                try {
                    new ClientHandler(serverSocket.accept()).start();
                }  catch (IOException e) {
                    if (isStarted) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                serverSocket.close();
                dbHandler.connectionClose();
            } catch (IOException ioex) {
                ServerMain.addToLogArea("Closing serverSocket failed");
                ioex.printStackTrace();
            } catch (SQLException ex) {
                ServerMain.addToLogArea("SQL Exception");
                ex.printStackTrace();
            }
            ServerMain.addToLogArea("Server stopped");
            ServerMain.addToLogArea("Connection to DB terminated");

        }
    }
}
