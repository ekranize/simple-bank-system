import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
//класс клиента банковской системы (моб.приложение/личный кабинет на сайте)
public class Client {
    private final JTextField responseField; //поле для отображения полученного ответа от сервера
    public Client (JTextField responseField) { //конструктор класса - на входе экземпляр поля для ответа
        this.responseField = responseField;
    }
    public void sendRequest (int portNum, String userName, String password, String encryptPass, String parameter) { //метод для тестирования соединения
        String response; //ответ
        BufferedReader reader; //ссылка на экземпляр класса для чтения данных из сокета
        Socket sock; //ссылка на экземпляр класса сокета
        PrintWriter writer; //ссылка на экземпляр класса для записи данных в сокет
        InputStreamReader isReader; //ссылка на экземпляр класса для чтения потока данных из сокета
            try {
            sock = new Socket("127.0.0.1", portNum); //открываем сокет для соединения с сервером
            writer = new PrintWriter(sock.getOutputStream()); //создаем экземпляр класса для записи данных в сокет
            isReader = new InputStreamReader(sock.getInputStream()); //создаем экземпляр класса для чтения потока данных из сокета
            reader = new BufferedReader(isReader); //создаем экземпляр класса для чтения данных из сокета
            System.out.println("networking established"); //сообщаем в консоль, что соединение установлено
            String request = "simple-bank-system;" + userName + ";" + Helper.makeMD5(password) + ";" + parameter; //собираем строку запроса на сервер
            System.out.println("request " + request); //выводим строку запроса в консоль
            String encryptedRequest = Helper.encrypt(request, encryptPass); //шифруем строку запроса на сервер
            System.out.println("Sending encrypted request " + encryptedRequest); //выводим шифрованную строку запроса в консоль
            writer.println(encryptedRequest); //пишем в сокет строку запроса
            writer.flush(); //принудительно отправляем в сокет то, что записали выше
                response = reader.readLine(); //ждем ответа от сервера, принимаем его
            System.out.println("Read response " + response); //выводим строку ответа в консоль
            this.responseField.setText(response); //выводим строку ответа в поле JTextField
            reader.close(); //закрываем чтение данных из сокета
            isReader.close(); //закрываем чтение потока данных из сокета
            writer.close(); //закрываем запись данных в сокет
            sock.close(); //закрываем сокет
            System.out.println("Connection closed"); // выводим в консоль сообщение о закрытии соединения
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
