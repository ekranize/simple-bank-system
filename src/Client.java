import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

//класс клиента банковской системы (моб.приложение/личный кабинет на сайте)
public class Client {
    protected final JTextField responseField; //поле для отображения полученного ответа от сервера
    public Client (JTextField responseField) { //конструктор класса - на входе экземпляр поля для ответа
        this.responseField = responseField;
    }
    private BufferedReader reader; //ссылка на экземпляр класса для чтения данных из сокета
    private Socket sock; //ссылка на экземпляр класса сокета
    private PrintWriter writer; //ссылка на экземпляр класса для записи данных в сокет
    private InputStreamReader isReader; //ссылка на экземпляр класса для чтения потока данных из сокета
    public void connect (int portNum, String userName, String passwordHash, String encryptPass) {
        try {
            if (sock == null || sock.isClosed()) {
                Thread readThread;
                sock = new Socket("127.0.0.1", portNum); //открываем сокет для соединения с сервером
                sock.setSoTimeout(20000);
                writer = new PrintWriter(sock.getOutputStream()); //создаем экземпляр класса для записи данных в сокет
                isReader = new InputStreamReader(sock.getInputStream()); //создаем экземпляр класса для чтения потока данных из сокета
                reader = new BufferedReader(isReader); //создаем экземпляр класса для чтения данных из сокета
                System.out.println("networking established"); //сообщаем в консоль, что соединение установлено
                StringBuilder request = new StringBuilder("simple-bank-system;" + userName + ";" + passwordHash + ";connect"); //собираем строку запроса на сервер
                System.out.println("request " + request); //выводим строку запроса в консоль
                String encryptedRequest = Helper.encrypt(request.toString(), encryptPass); //шифруем строку запроса на сервер
                System.out.println("Sending encrypted request " + encryptedRequest); //выводим шифрованную строку запроса в консоль
                writer.println(encryptedRequest); //пишем в сокет строку запроса
                writer.flush(); //принудительно отправляем в сокет то, что записали выше
                readThread = new Thread(new Client.ReadHandler()); //создаем отдельный поток для работы с чтением ответов от сервера
                readThread.start(); //стартуем поток чтения ответов
            } else responseField.setText("Connection is already established");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void sendRequest (String userName, String passwordHash, String encryptPass, String[] parameters) { //метод для отправки запроса на сервер
            try {
                if (sock == null || sock.isClosed()) {
                    responseField.setText("Connection is NOT established");
                } else {
                    StringBuilder request = new StringBuilder("simple-bank-system;" + userName + ";" + passwordHash); //собираем строку запроса на сервер
                    for (String parameter : parameters) {
                        request.append(";");
                        request.append(parameter);
                    }
                    System.out.println("request " + request); //выводим строку запроса в консоль
                    String encryptedRequest = Helper.encrypt(request.toString(), encryptPass); //шифруем строку запроса на сервер
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
                        Client.this.responseField.setText(response); //выводим строку ответа в поле JTextField
                    }
                }
            } catch (SocketTimeoutException ste) {
                System.out.println("The timeout has expired. Connection closed");
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
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
    }
}
