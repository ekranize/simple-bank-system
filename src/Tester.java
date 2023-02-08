import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Tester {

    public static void main(String[] args) {
        String response; //ответ
        BufferedReader reader; //ссылка на экземпляр класса для чтения данных из сокета
        Socket sock; //ссылка на экземпляр класса сокета
        PrintWriter writer; //ссылка на экземпляр класса для записи данных в сокет
        InputStreamReader isReader; //ссылка на экземпляр класса для чтения потока данных из сокета
        try {
            sock = new Socket("127.0.0.1", 12021); //открываем сокет для соединения с сервером
            writer = new PrintWriter(sock.getOutputStream()); //создаем экземпляр класса для записи данных в сокет
            isReader = new InputStreamReader(sock.getInputStream()); //создаем экземпляр класса для чтения потока данных из сокета
            reader = new BufferedReader(isReader); //создаем экземпляр класса для чтения данных из сокета
            System.out.println("networking established"); //сообщаем в консоль, что соединение установлено
            StringBuilder request = new StringBuilder("simple-bank-system;user1;6CB75F652A9B52798EB6CF2201057C73;connect"); //собираем строку запроса на сервер
            System.out.println("request " + request); //выводим строку запроса в консоль
            String encryptedRequest = Helper.encrypt(request.toString(), "testPass"); //шифруем строку запроса на сервер
            System.out.println("Sending encrypted request " + encryptedRequest); //выводим шифрованную строку запроса в консоль
            writer.println(encryptedRequest); //пишем в сокет строку запроса
            writer.flush(); //принудительно отправляем в сокет то, что записали выше
            while (!sock.isClosed()) { //пока сокет не закрыт
                while ((response = reader.readLine()) != null) { //в цикле ждем ответа от сервера, принимаем его
                    System.out.println("Read response " + response); //выводим строку ответа в консоль
                }
            }
            reader.close();
            isReader.close();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
