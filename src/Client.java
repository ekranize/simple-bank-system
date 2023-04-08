import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

//класс клиента банковской системы (моб.приложение/личный кабинет на сайте)
public class Client {
    private BufferedReader reader; //ссылка на экземпляр класса для чтения данных из сокета
    private Socket sock; //ссылка на экземпляр класса сокета
    private PrintWriter writer; //ссылка на экземпляр класса для записи данных в сокет
    private InputStreamReader isReader; //ссылка на экземпляр класса для чтения потока данных из сокета
    private static Client instance = null;
    private String salt;
    private String algorithm;
    public static synchronized Client getInstance(){  // Используем шаблон одиночка
        if (instance == null)
            instance = new Client();
        return instance;
    }
    private Client() {}
    public void connect (String serverIP, int portNum, String encryptPass, String workString, int soTimeout, String userName, String passwordHash, String salt, String algorithm) {
        this.salt = salt;
        this.algorithm = algorithm;
        try {
            if (sock == null || sock.isClosed()) {
                Thread readThread;
                sock = new Socket(serverIP, portNum); //открываем сокет для соединения с сервером
                System.out.println(sock.getLocalPort());
                //sock.setSoTimeout(soTimeout);
                writer = new PrintWriter(sock.getOutputStream()); //создаем экземпляр класса для записи данных в сокет
                isReader = new InputStreamReader(sock.getInputStream()); //создаем экземпляр класса для чтения потока данных из сокета
                reader = new BufferedReader(isReader); //создаем экземпляр класса для чтения данных из сокета
                System.out.println("networking established"); //сообщаем в консоль, что соединение установлено
                StringBuilder request = new StringBuilder(workString + ";connect;" + userName + ";" + passwordHash); //собираем строку запроса на сервер
                System.out.println("request " + request); //выводим строку запроса в консоль
                String encryptedRequest = Helper.encrypt(request.toString(), encryptPass, salt, algorithm); //шифруем строку запроса на сервер
                System.out.println("Sending encrypted request " + encryptedRequest); //выводим шифрованную строку запроса в консоль
                writer.println(encryptedRequest); //пишем в сокет строку запроса
                writer.flush(); //принудительно отправляем в сокет то, что записали выше
                readThread = new Thread(new Client.ReadHandler()); //создаем отдельный поток для работы с чтением ответов от сервера
                readThread.start(); //стартуем поток чтения ответов
            } else ClientMain.addToLogArea("Connection is already established");
        } catch (ConnectException cex) {
            ClientMain.addToLogArea("Connection refused");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void disconnect () {
        writer.close();
       try {
            reader.close();
            System.out.println("test");
            isReader.close();
            sock.close();
        } catch (IOException ioex) {
            ClientMain.addToLogArea("IO Exception");
        }
    }
    public void sendRequest (String workString, String encryptPass, String[] parameters) { //метод для отправки запроса на сервер
            try {
                if (sock == null || sock.isClosed()) {
                    ClientMain.addToLogArea("Connection is NOT established");
                } else {
                    StringBuilder request = new StringBuilder(); //собираем строку запроса на сервер
                    request.append(workString);
                    for (String parameter : parameters) {
                        request.append(";");
                        request.append(parameter);
                    }
                    System.out.println("request " + request); //выводим строку запроса в консоль
                    String encryptedRequest = Helper.encrypt(request.toString(), encryptPass, salt, algorithm); //шифруем строку запроса на сервер
                    System.out.println("Sending encrypted request " + encryptedRequest); //выводим шифрованную строку запроса в консоль
                    writer.println(encryptedRequest); //пишем в сокет строку запроса
                    writer.flush(); //принудительно отправляем в сокет то, что записали выше
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    class ReadHandler implements Runnable { //вложенный класс, содержащий задачу для потока чтения ответов
        @Override
        public void run() { //метод - задача для потока
            try {
                while (!sock.isClosed()) { //пока сокет не закрыт
                    //ответ
                    String response;
                    while ((response = reader.readLine()) != null) { //в цикле ждем ответа от сервера, принимаем его
                        System.out.println("Read response " + response); //выводим строку ответа в консоль
                        ClientMain.addToLogArea(response); //выводим строку ответа в поле JTextArea
                    }
                }
            } catch (SocketTimeoutException ste) {
                System.out.println("The timeout has expired. Connection closed");
            } catch (SocketException sex) {
                ClientMain.addToLogArea("Connection closed");
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    sock.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
}
