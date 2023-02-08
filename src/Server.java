import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

//класс банковского сервера
public class Server {
    private ServerSocket serverSocket; //ссылка на экземпляр серверного сокета
    private Thread serverThread; //ссылка на экземпляр серверного потока
    private boolean isStarted = false; //признак работы сервера (запущен/остановлен)
    private String encryptPass; //строка с паролем для шифрования трафика
    DBHandler dbHandler = null;
    public void startServer (int portNum, String encryptPass) { //метод для запуска сервера, на входе - пароль для шифрования трафика и номер серверного порта
        this.encryptPass = encryptPass;
        try {
            serverSocket = new ServerSocket(portNum); //открываем серверный сокет на нужном порту
            serverSocket.setSoTimeout(2000); //таймаут ожидания подключения ???
            serverThread = new Thread(new ServerHandler()); //открываем серверный поток
            serverThread.start(); //запускаем серверный поток
            System.out.println("Server started on port " + portNum); //сообщаем в консоли о запуске сервера
            dbHandler = DBHandler.getInstance();
            System.out.println("Connection to DB established");
        } catch (SQLException ex) {
            System.out.println("SQL Exception");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        isStarted = true; //выставляем признак запуска сервера - запущен
    }
    public void stopServer () { //метод для останова сервера
        try {
            serverThread.interrupt(); //прерываем серверныый поток
            dbHandler.connectionClose();
            System.out.println("Connection to DB closed");
        } catch (SQLException ex) {
            System.out.println("SQL Exception");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        isStarted = false; //выставляем признак запуска сервера - остановлен
    }

    public boolean isStarted() { //метод для проверки признака запуска сервера
        return isStarted;
    }
    class ClientHandler implements Runnable { //вложенный класс, содержащий задачу для клиентского потока
        BufferedReader reader; //ссылка на экземпляр класса для чтения данных из сокета
        InputStreamReader isReader;
        PrintWriter writer;
        Socket sock; //ссылка на экземпляр класса клиентского сокета
        public ClientHandler(Socket clientSocket) { //конструктор вложенного класса, на входе - сокет клиента
            sock = clientSocket;
        }
        public void run() { //метод - задача для клиентского потока
            try {
                String message; //строка - сообщение от клиента
                isReader = new InputStreamReader(sock.getInputStream()); //создаем экземпляр класса для чтения потока данных из сокета
                writer = new PrintWriter(sock.getOutputStream()); //создаем экземпляр класса для записи данных в сокет
                reader = new BufferedReader(isReader); //создаем экземпляр класса для чтения данных из сокета
                while (!sock.isClosed()) { //пока сокет не закрыт
                    while ((message = reader.readLine()) != null) { //в цикле ждем ответа от сервера, принимаем его
                        String processedMessage = requestProcessing(message);
                        if (processedMessage.equals("Connection closed")) { //если ответом является успешное закрытие соединения
                            reader.close(); //закрываем чтение данных из сокета
                            isReader.close(); //закрываем чтение потока данных из сокета
                            writer.close(); //закрываем запись данных из сокета
                            sock.close(); //закрываем сокет
                            System.out.println("Connection closed");  // выводим в консоль сообщение о закрытии соединения с данным клиентом
                        }
                        writer.println(processedMessage); //пишем в сокет ответ
                        writer.flush(); //принудительно отправляем в сокет то, что записали выше
                    }
                }
            } catch (SocketTimeoutException ste) {
                System.out.println("The timeout has expired. Connection closed");
            } catch (Exception ex) {
                ex.printStackTrace();
            }finally {
                try {
                    reader.close();
                    isReader.close();
                    writer.close();
                    sock.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        private String requestProcessing(String request) { //метод для обработки сообщения/запроса клиента, на входе - сообщение/запрос
            System.out.println("Read request " + request);  // выводим в консоль сообщение/запрос
            String decryptedRequest = Helper.decrypt(request,encryptPass); //расшифровываем запрос
            System.out.println("Decrypted request " + decryptedRequest);  // выводим в консоль расшифрованный запрос
            String[] requestArray = decryptedRequest.split(";"); //разбиваем запрос на лексемы - части запроса
            if (requestArray.length != 0) {
                if (requestArray[0].equals("simple-bank-system")) {
                    if (requestArray[3].equals("registration")) {
                        if (registerClient(requestArray[1], requestArray[2])) return "Registration - OK";
                        else return "Registration - FAILED";
                    } else if (checkAuth(requestArray[1], requestArray[2])) {
                        System.out.println("Authorization success");
                        switch (requestArray[3]) {
                            case "connect" -> {
                                return "Connection established - OK";}
                            case "password changing" -> {
                                if (passwordChange(requestArray[1], requestArray[4])) return "Password change - OK";
                                else return "Password change - FAILED";}
                            case "money transaction" -> {
                                System.out.println("money transaction case");
                                return "money transaction case";}
                            default -> {
                                System.out.println("Wrong parameter");
                                return "Wrong parameter";}
                        }
                    } else return "Authorization - FAILED";
                } else return "Bad request";
            }
            return "Empty request";
        }
        private boolean registerClient (String userName, String passwordHash) {
            try {
                assert dbHandler != null;
                if (dbHandler.addUser(userName, passwordHash)) {
                    System.out.println(userName + " added to DB");
                    return true;
                }
            } catch (SQLException ex) {
                System.out.println("SQL Exception");
                ex.printStackTrace();
            }
            return false;
        }
        private boolean checkAuth (String userName, String passwordHash) {
            try {
                assert dbHandler != null;
                if (dbHandler.checkUser(userName, passwordHash)) {
                    return true;
                }
            } catch (SQLException ex) {
                System.out.println("SQL Exception");
                ex.printStackTrace();
            }
            return false;
        }
        private boolean passwordChange (String userName, String passwordHash) {
            try {
                assert dbHandler != null;
                if (dbHandler.passChange(userName, passwordHash)) {
                    return true;
                }
            } catch (SQLException ex) {
                System.out.println("SQL Exception");
                ex.printStackTrace();
            }
            return false;
        }
    }
    class ServerHandler implements Runnable { //вложенный класс, содержащий задачу для серверного потока
        @Override
        public void run() { //метод - задача для серверного потока
            try {
                while (!serverSocket.isClosed()) { //пока серверный сокет не закрыт
                    Socket clientSocket = serverSocket.accept(); //принимаем соединение от очередного клиента
                    clientSocket.setSoTimeout(20000);
                    Thread t = new Thread(new ClientHandler(clientSocket)); //создаем отдельный клиентский поток для работы с каждым клиентом отдельно
                    t.start(); //стартуем клиентский поток
                    System.out.println("Got a connection"); //выводим в консоль сообщение об установлении соединения с клиентом
                }
            } catch (SocketTimeoutException stoEx) { //таймаут серверного сокета сработал?
                if (!serverThread.isInterrupted()) run(); //если серверный поток не прерван - стартуем еще раз задачу для серверного потока
                else { //иначе
                    try {
                        serverSocket.close(); //закрываем к чертям серверный сокет
                        System.out.println("Server stopped (interrupted)"); //выводим в консоль сообщение о том, что сервер преван (поток прерван)
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
