import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

//класс для создания графического интерфейса серверного приложения
public class ServerMain {
    private JButton startServerButton; //кнопка запуска сервера
    private JButton stopServerButton; //кнопка остановки сервера
    private final int portNum; //номер порта серверного сокета
    private final int soTimeout;
    private final String workString;
    private final String encryptPass; //пароль шифрования
    private String salt;
    private String algorithm;
    private Server server; //ссылка на экземпляр класса сервера
    private static JTextArea logArea; //поле для логов
    private ServerMain () {
        Properties properties = Helper.getProperties("server");
        this.portNum = Integer.parseInt(properties.getProperty("portNum"));
        this.encryptPass = properties.getProperty("encryptPass");
        this.salt = properties.getProperty("salt");
        this.algorithm = properties.getProperty("algorithm");
        this.soTimeout = Integer.parseInt(properties.getProperty("soTimeout"));
        this.workString = properties.getProperty("workString");
    }
    public static void main(String[] args) {
        new ServerMain().run();
    }
    private void run () {
        JFrame frame = new JFrame("SBS - Server"); //окно приложения
        Font bigFont = new Font("sanserif", Font.BOLD,18); //шрифт
        JPanel mainPanel = new JPanel(); //главная панель в окне приложения
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        startServerButton = new JButton("Start server");
        startServerButton.addActionListener(new startServerListener()); //добавляем кнопку старта сервера в прослушиваемые
        startServerButton.setFont(bigFont); //устанавливаем шрифт
        startServerButton.setAlignmentX(0.5f);
        stopServerButton = new JButton("Stop server");
        stopServerButton.addActionListener(new stopServerListener()); //добавляем кнопку остановки сервера в прослушиваемые
        stopServerButton.setFont(bigFont); //устанавливаем шрифт
        stopServerButton.setEnabled(false); //делаем кнопку остановки сервера неактивной
        stopServerButton.setAlignmentX(0.5f);
        logArea = new JTextArea(10,15);
        logArea.setLineWrap(true);
        logArea.setEnabled(false);
        logArea.setAlignmentX(0.5f);
        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane logPane = new JScrollPane(logArea);
        logPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //добавляем все элементы на главную панель:
        mainPanel.add(startServerButton);
        mainPanel.add(stopServerButton);
        mainPanel.add(logPane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //действие по умолчанию при закрытии окна приложения - EXIT
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel); //устанавливаем главную панель в центре компоновщика BorderLayout окна приложения
        frame.setSize(330,320); //размер окна приложения
        frame.setLocationRelativeTo(null); //устанавливаем окно приложения по центру экрана
        frame.setVisible(true); //показываем окно приложения

        server = Server.getInstance(); //создаем экземпляр класса сервера
    }
    private class startServerListener implements ActionListener { //вложенный класс для метода по нажатию кнопки старта сервера
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки старта сервера
            if (portNum <= 65536 && portNum >=1025 && !server.isStarted()) { //если с введенным номером порта все в порядке и сервер еще не запущен
                server.startServer(portNum,encryptPass,workString,soTimeout, salt, algorithm); //запускаем метод старта сервера
                startServerButton.setEnabled(false); //делаем кнопку старта сервера неактивной
                stopServerButton.setEnabled(true); //делаем кнопку остановки сервера активной
            }
        }
    }
    private class stopServerListener implements ActionListener { //вложенный класс для метода по нажатию кнопки остановки сервера
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки остановки сервера
            if (server.isStarted()) { //если сервер работает
                server.stopServer(); //запускаем метод остановки сервера
                stopServerButton.setEnabled(false); //делаем кнопку остановки сервера неактивной
                startServerButton.setEnabled(true); //делаем кнопку старта сервера активной
            }
        }
    }
    public static void addToLogArea (String text) {
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logArea.append(formatForDateNow.format(dateNow) + " " + text + "\n");
    }
}
