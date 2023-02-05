import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//класс для создания графического интерфейса серверного приложения
public class ServerGui {
    private JButton startServerButton; //кнопка запуска сервера
    private JButton stopServerButton; //кнопка остановки сервера
    private JTextField portNumField; //поле для ввода номера порта серверного сокета
    private JTextField encryptPassField; //поле для ввода пароля шифрования
    private Server server; //ссылка на экземпляр класса сервера
    public void start() {
        JFrame frame = new JFrame("SBS - Server"); //окно приложения
        Font bigFont = new Font("sanserif", Font.BOLD,18); //шрифт
        JPanel mainPanel = new JPanel(); //главная панель в окне приложения
        mainPanel.setLayout(null); //убираем компоновщик в главной панели - будем размещать элементы вручную по координатам
        JLabel portNumLabel = new JLabel("Server socket port number (1025-65536"); //надпись
        portNumLabel.setBounds(70, 50, 160, 30); //положение и размеры
        portNumField = new JTextField(5); //размер поля в условных "колонках"
        portNumField.setBounds(80, 80, 140, 20); //положение и размеры
        portNumField.setText("12021"); //текст в поле
        JLabel encryptPassLabel = new JLabel("Server encrypt pass "); //надпись
        encryptPassLabel.setBounds(90, 120, 160, 30); //положение и размеры
        encryptPassField = new JTextField(10); //размер поля в условных "колонках"
        encryptPassField.setBounds(80, 150, 140, 20); //положение и размеры
        encryptPassField.setText("testPass"); //текст в поле
        startServerButton = new JButton("Start server");
        startServerButton.addActionListener(new startServerListener()); //добавляем кнопку старта сервера в прослушиваемые
        startServerButton.setBounds(80, 200, 140, 50); //положение и размеры
        startServerButton.setFont(bigFont); //устанавливаем шрифт
        stopServerButton = new JButton("Stop server");
        stopServerButton.addActionListener(new stopServerListener()); //добавляем кнопку остановки сервера в прослушиваемые
        stopServerButton.setBounds(80, 250, 140, 50); //положение и размеры
        stopServerButton.setFont(bigFont); //устанавливаем шрифт
        stopServerButton.setEnabled(false); //делаем кнопку остановки сервера неактивной
        //добавляем все элементы на главную панель:
        mainPanel.add(portNumLabel);
        mainPanel.add(portNumField);
        mainPanel.add(encryptPassLabel);
        mainPanel.add(encryptPassField);
        mainPanel.add(startServerButton);
        mainPanel.add(stopServerButton);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //действие по умолчанию при закрытии окна приложения - EXIT
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel); //устанавливаем главную панель в центре компоновщика BorderLayout окна приложения
        frame.setSize(300,400); //размер окна приложения
        frame.setLocationRelativeTo(null); //устанавливаем окно приложения по центру экрана
        frame.setVisible(true); //показываем окно приложения

        server = new Server(); //создаем экземпляр класса сервера
    }
    class startServerListener implements ActionListener { //вложенный класс для метода по нажатию кнопки старта сервера
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки старта сервера
            if (portNumField.getText().matches("[-+]?\\d+")
                    && Integer.parseInt(portNumField.getText()) <= 65536
                    && Integer.parseInt(portNumField.getText()) >=1025
                    && !server.isStarted()) { //если с введенным номером порта все в порядке и сервер еще не запущен
                server.startServer(Integer.parseInt(portNumField.getText()),encryptPassField.getText()); //запускаем метод старта сервера
                startServerButton.setEnabled(false); //делаем кнопку старта сервера неактивной
                stopServerButton.setEnabled(true); //делаем кнопку остановки сервера активной
            }
        }
    }
    class stopServerListener implements ActionListener { //вложенный класс для метода по нажатию кнопки остановки сервера
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки остановки сервера
            if (server.isStarted()) { //если сервер работает
                server.stopServer(); //запускаем метод остановки сервера
                stopServerButton.setEnabled(false); //делаем кнопку остановки сервера неактивной
                startServerButton.setEnabled(true); //делаем кнопку старта сервера активной
            }
        }
    }
}
