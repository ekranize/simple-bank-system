import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

//класс для создания графического интерфейса клиентского приложения
public class ClientMain {
    private Client client; //ссылка на экземпляр класса клиента
    private JTextField userNameField; //поле с именем пользователя
    private JPasswordField passwordField; //поле с паролем пользователя
    private JPasswordField newPasswordField; //поле с новым паролем пользователя
    private JTextField transferAmountField;
    private JTextField transferUserNameField;
    private static JTextArea logArea; //поле для отображения ответа от сервера
    private JLabel balanceLabel;
    private String serverIP;
    private int portNum;
    private String encryptPass;
    private String salt;
    private String algorithm;
    private String workString;
    private int soTimeout;
    private String userName;
    private String password;
    private String newPassword;
    private String transferUserName;
    private int transferAmount;
    private ClientMain () {
        Properties properties = Helper.getProperties("client");
        this.serverIP = properties.getProperty("serverIP");
        this.portNum = Integer.parseInt(properties.getProperty("portNum"));
        this.encryptPass = properties.getProperty("encryptPass");
        this.salt = properties.getProperty("salt");
        this.algorithm = properties.getProperty("algorithm");
        this.soTimeout = Integer.parseInt(properties.getProperty("soTimeout"));
        this.workString = properties.getProperty("workString");
        this.userName = properties.getProperty("userName");
        this.password = properties.getProperty("password");
        this.newPassword = properties.getProperty("newPassword");
        this.transferUserName = properties.getProperty("transferUserName");
        this.transferAmount = Integer.parseInt(properties.getProperty("transferAmount"));
    }
    public static void main(String[] args) {
        new ClientMain().start(); //создание GUI и его запуск
    }
    public void start() {
        JButton registrationButton; //кнопка регистрации в системе
        JButton changePassButton; //кнопка смены пароля
        JButton connectButton; //кнопка теста соединения
        JButton disconnectButton; //кнопка теста соединения
        JButton transferButton; //кнопка перевода денег


        JFrame frame = new JFrame("SBS - Client"); //окно приложения
        Font bigFont = new Font("sanserif", Font.BOLD,12); //шрифт

        JPanel leftPanel = new JPanel(); //главная панель в окне приложения
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); //компоновщик панели - BoxLayout
        balanceLabel = new JLabel("Balance: ? RR");
        JLabel userNameLabel = new JLabel("userName:");
        userNameField = new JTextField(5); //размер поля в условных "колонках"
        userNameField.setMaximumSize(new Dimension(200,20));
        userNameField.setText(userName); //текст в поле
        JLabel passwordLabel = new JLabel("password:");
        passwordField = new JPasswordField(5); //размер поля в условных "колонках"
        passwordField.setMaximumSize(new Dimension(200,20));
        passwordField.setText(password); //текст в поле
        connectButton = new JButton("Connect");
        connectButton.addActionListener(new connectListener()); //добавляем кнопку соединения в прослушиваемые
        connectButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(new disconnectListener()); //добавляем кнопку соединения в прослушиваемые
        disconnectButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        registrationButton = new JButton("Register");
        registrationButton.addActionListener(new registerListener()); //добавляем кнопку регистрации в прослушиваемые
        registrationButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        changePassButton = new JButton("Change password");
        changePassButton.addActionListener(new passwordChangeListener()); //добавляем кнопку смены пароля в прослушиваемые
        changePassButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        JLabel newPasswordLabel = new JLabel("New password"); //надпись
        newPasswordField = new JPasswordField(5); //размер поля в условных "колонках"
        newPasswordField.setMaximumSize(new Dimension(200,20));
        newPasswordField.setText("password2"); //текст в поле
        //добавляем все элементы на левую панель:
        balanceLabel.setAlignmentX(0.5f);
        userNameField.setAlignmentX(0.5f);
        passwordField.setAlignmentX(0.5f);
        connectButton.setAlignmentX(0.5f);
        disconnectButton.setAlignmentX(0.5f);
        registrationButton.setAlignmentX(0.5f);
        changePassButton.setAlignmentX(0.5f);
        newPasswordLabel.setAlignmentX(0.5f);
        newPasswordField.setAlignmentX(0.5f);
        leftPanel.add(balanceLabel); //balanceLabel.setVisible(false);
        leftPanel.add(userNameField);
        leftPanel.add(passwordField);
        leftPanel.add(connectButton);
        leftPanel.add(disconnectButton); //disconnectButton.setVisible(false);
        leftPanel.add(registrationButton);
        leftPanel.add(changePassButton); //changePassButton.setVisible(false);
        leftPanel.add(newPasswordLabel); //newPasswordLabel.setVisible(false);
        leftPanel.add(newPasswordField); //newPasswordField.setVisible(false);


        JPanel mainPanel = new JPanel(); //главная панель в окне приложения
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); //компоновщик панели - BoxLayout
        JLabel transferUserNameLabel = new JLabel("TO userName");
        transferUserNameField = new JTextField(5); //размер поля в условных "колонках"
        transferUserNameField.setMaximumSize(new Dimension(200,20));
        transferUserNameField.setText("user2"); //текст в поле
        JLabel transferAmountLabel = new JLabel("amount");
        transferAmountField = new JTextField(5); //размер поля в условных "колонках"
        transferAmountField.setMaximumSize(new Dimension(200,20));
        transferAmountField.setText("100"); //текст в поле
        transferButton = new JButton("Transfer");
        transferButton.addActionListener(new moneyTransferListener()); //добавляем кнопку соединения в прослушиваемые
        transferButton.setFont(bigFont); //устанавливаем для кнопки шрифт
        logArea = new JTextArea(20,15);
        logArea.setLineWrap(true);
        logArea.setEnabled(false);
        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane logPane = new JScrollPane(logArea);
        logPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //добавляем все элементы на центральную панель:
        transferUserNameLabel.setAlignmentX(0.5f);
        transferUserNameField.setAlignmentX(0.5f);
        transferAmountLabel.setAlignmentX(0.5f);
        transferAmountField.setAlignmentX(0.5f);
        transferButton.setAlignmentX(0.5f);
        mainPanel.add(transferUserNameLabel); //transferUserNameLabel.setVisible(false);
        mainPanel.add(transferUserNameField); //transferUserNameField.setVisible(false);
        mainPanel.add(transferAmountLabel); //transferAmountLabel.setVisible(false);
        mainPanel.add(transferAmountField); //transferAmountField.setVisible(false);
        mainPanel.add(transferButton); //transferButton.setVisible(false);
        mainPanel.add(logPane);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //действие по умолчанию при закрытии окна приложения - EXIT
        frame.getContentPane().add(BorderLayout.WEST, leftPanel); //устанавливаем левую панель слева
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel); //устанавливаем главную панель в центре компоновщика BorderLayout окна приложения
        frame.setSize(800,600); //размер окна приложения
        frame.setLocationRelativeTo(null); //устанавливаем окно приложения по центру экрана



        frame.setVisible(true); //показываем окно приложения

        client = Client.getInstance(); //создаем экземпляр класса клиента
    }
    public static void addToLogArea (String text) {
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logArea.append(formatForDateNow.format(dateNow) + " " + text + "\n");
    }
    public void changeBalanceLabel (int amount) {
        balanceLabel.setText("Balance: " + (Integer.parseInt(balanceLabel.getText()) + amount) + " RR");
    }
    class connectListener implements ActionListener { //вложенный класс для метода по нажатию кнопки соединения
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки соединения
            if (portNum <= 65536 && portNum >=1025) { //если с введенным номером порта все в порядке
                client.connect(serverIP, portNum, encryptPass, workString, soTimeout, userNameField.getText(), Helper.makeMD5(passwordField.toString()), salt, algorithm);
            }
        }
    }
    class disconnectListener implements ActionListener { //вложенный класс для метода по нажатию кнопки разъединения
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки соединения
            client.disconnect();
        }
    }
    class registerListener implements ActionListener { //вложенный класс для метода по нажатию кнопки регистрации
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки регистрации
            String[] parameters = {"register", userNameField.getText(), Helper.makeMD5(String.valueOf(passwordField.getPassword()))};
            client.sendRequest(workString, encryptPass, parameters);
        }
    }
    class passwordChangeListener implements ActionListener { //вложенный класс для метода по нажатию кнопки смены пароля
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки смены пароля
            String[] parameters = {"passwordChange",Helper.makeMD5(String.valueOf(newPasswordField.getPassword()))};
            client.sendRequest(workString, encryptPass, parameters);
        }
    }
    class moneyTransferListener implements ActionListener { //вложенный класс для метода по нажатию кнопки смены пароля
        @Override
        public void actionPerformed(ActionEvent e) { //метод по нажатию кнопки смены пароля
            if (transferAmountField.getText().matches("[-+]?\\d+")) {
                String[] parameters = {"moneyTransfer", transferUserNameField.getText(), transferAmountField.getText()};
                client.sendRequest(workString, encryptPass, parameters);
            }
        }
    }
}
