import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//класс для создания графического интерфейса клиентского приложения
public class ClientGui {
    private Client client; //ссылка на экземпляр класса клиента
    private JTextField encryptPassField; //поле с паролем шифрования
    private JTextField portNumField; //поле с номером порта для соединения к серверу
    private JTextField userNameField; //поле с именем пользователя
    private JTextField passwordField; //поле с паролем пользователя
    private JTextField newPasswordField; //поле с новым паролем пользователя
    public void start() {
        JTextField responseField; //поле для отображения ответа от сервера
        JButton registrationButton; //кнопка регистрации в системе
        JButton changePassButton; //кнопка смены пароля
        JButton testConnectionButton; //кнопка теста соединения
        JFrame frame = new JFrame("SBS - Client"); //окно приложения
        Font bigFont = new Font("sanserif", Font.BOLD,14); //шрифт
        JPanel mainPanel = new JPanel(); //главная панель в окне приложения
        mainPanel.setLayout(null); //убираем компоновщик в главной панели - будем размещать элементы вручную по координатам
        JLabel portNumLabel = new JLabel("Server socket port number "); //надпись
        portNumLabel.setBounds(70, 10, 160, 30); //положение и размеры
        portNumField = new JTextField(5); //размер поля в условных "колонках"
        portNumField.setBounds(80, 40, 140, 20); //положение и размеры
        portNumField.setText("12021"); //текст в поле
        JLabel encryptPassLabel = new JLabel("Server encrypt pass "); //надпись
        encryptPassLabel.setBounds(90, 60, 160, 30); //положение и размеры
        encryptPassField = new JTextField(10); //размер поля в условных "колонках"
        encryptPassField.setBounds(80, 90, 140, 20); //положение и размеры
        encryptPassField.setText("testPass"); //текст в поле
        JLabel userNameLabel = new JLabel("User name"); //надпись
        userNameLabel.setBounds(100, 110, 160, 30); //положение и размеры
        userNameField = new JTextField(5); //размер поля в условных "колонках"
        userNameField.setBounds(80, 140, 140, 20); //положение и размеры
        userNameField.setText("user1"); //текст в поле
        JLabel passwordLabel = new JLabel("User password"); //надпись
        passwordLabel.setBounds(90, 160, 160, 30); //положение и размеры
        passwordField = new JTextField(5); //размер поля в условных "колонках"
        passwordField.setBounds(80, 190, 140, 20); //положение и размеры
        passwordField.setText("password1"); //текст в поле
        responseField = new JTextField(50); //размер поля в условных "колонках"
        responseField.setBounds(10, 230, 260, 20); //положение и размеры
        responseField.setText(""); //текст в поле
        responseField.setEnabled(false); //устанавливаем полю режим readonly
        testConnectionButton = new JButton("Test connection");
        testConnectionButton.addActionListener(new testConnectionListener()); //добавляем кнопку теста соединения в прослушиваемые
        testConnectionButton.setBounds(80, 270, 140, 50); //положение и размеры
        testConnectionButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        registrationButton = new JButton("Register");
        registrationButton.addActionListener(new registrationListener()); //добавляем кнопку регистрации в прослушиваемые
        registrationButton.setBounds(80, 320, 140, 50); //положение и размеры
        registrationButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        changePassButton = new JButton("Change password");
        changePassButton.addActionListener(new changePassListener()); //добавляем кнопку смены пароля в прослушиваемые
        changePassButton.setBounds(80, 370, 140, 50); //положение и размеры
        changePassButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        JLabel newPasswordLabel = new JLabel("New password"); //надпись
        newPasswordLabel.setBounds(100, 420, 160, 30); //положение и размеры
        newPasswordField = new JTextField(5); //размер поля в условных "колонках"
        newPasswordField.setBounds(80, 450, 140, 20); //положение и размеры
        newPasswordField.setText("password2"); //текст в поле
        //добавляем все элементы на главную панель:
        mainPanel.add(portNumLabel);
        mainPanel.add(portNumField);
        mainPanel.add(encryptPassLabel);
        mainPanel.add(encryptPassField);
        mainPanel.add(userNameLabel);
        mainPanel.add(userNameField);
        mainPanel.add(passwordLabel);
        mainPanel.add(newPasswordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(newPasswordField);
        mainPanel.add(responseField);
        mainPanel.add(registrationButton);
        mainPanel.add(changePassButton);
        mainPanel.add(testConnectionButton);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //действие по умолчанию при закрытии окна приложения - EXIT
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel); //устанавливаем главную панель в центре компоновщика BorderLayout окна приложения
        frame.setSize(300,600); //размер окна приложения
        frame.setLocationRelativeTo(null); //устанавливаем окно приложения по центру экрана
        frame.setVisible(true); //показываем окно приложения

        client = new Client(responseField); //создаем экземпляр класса клиента
    }
    class testConnectionListener implements ActionListener { //вложенный класс для метода по нажатию кнопки теста соединения
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки теста соединения
            String[] parameters = {"test connection"};
            client.sendRequest(Integer.parseInt(portNumField.getText()), userNameField.getText(), Helper.makeMD5(passwordField.getText()), encryptPassField.getText(), parameters);
        }
    }
    class registrationListener implements ActionListener { //вложенный класс для метода по нажатию кнопки регистрации
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки регистрации
            String[] parameters = {"registration"};
            client.sendRequest(Integer.parseInt(portNumField.getText()), userNameField.getText(), Helper.makeMD5(passwordField.getText()), encryptPassField.getText(), parameters);
        }
    }
    class changePassListener implements ActionListener { //вложенный класс для метода по нажатию кнопки смены пароля
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки смены пароля
            String[] parameters = {"password changing",Helper.makeMD5(newPasswordField.getText())};
            client.sendRequest(Integer.parseInt(portNumField.getText()), userNameField.getText(), Helper.makeMD5(passwordField.getText()), encryptPassField.getText(), parameters);
        }
    }
}
