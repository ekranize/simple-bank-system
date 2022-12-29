import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGui {
    private Client client;
    public void start() {
        JButton registrationButton;
        JButton changePassButton;
        JButton testConnectionButton;
        JTextField portNumField;
        JTextField encryptPassField;
        JTextField userNameField;
        JTextField passwordField;
        JTextField responseField;
        JFrame frame = new JFrame("SBS - Client");
        Font bigFont = new Font("sanserif", Font.BOLD,14);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        JLabel portNumLabel = new JLabel("Server socket port number ");
        portNumLabel.setBounds(70, 10, 160, 30);
        portNumField = new JTextField(5);
        portNumField.setBounds(80, 40, 140, 20);
        portNumField.setText("12021");
        JLabel encryptPassLabel = new JLabel("Server encrypt pass ");
        encryptPassLabel.setBounds(90, 60, 160, 30);
        encryptPassField = new JTextField(10);
        encryptPassField.setBounds(80, 90, 140, 20);
        encryptPassField.setText("testPass");
        JLabel userNameLabel = new JLabel("User name");
        userNameLabel.setBounds(100, 110, 160, 30);
        userNameField = new JTextField(5);
        userNameField.setBounds(80, 140, 140, 20);
        userNameField.setText("user1");
        JLabel passwordLabel = new JLabel("User password");
        passwordLabel.setBounds(90, 160, 160, 30);
        passwordField = new JTextField(5);
        passwordField.setBounds(80, 190, 140, 20);
        passwordField.setText("password1");
        responseField = new JTextField(50);
        responseField.setBounds(10, 230, 260, 20);
        responseField.setText("");
        responseField.setEnabled(false);
        testConnectionButton = new JButton("Test connection");
        testConnectionButton.addActionListener(new testConnectionListener());
        testConnectionButton.setBounds(80, 270, 140, 50);
        testConnectionButton.setFont(bigFont);
        registrationButton = new JButton("Register");
        registrationButton.addActionListener(new registrationListener());
        registrationButton.setBounds(80, 320, 140, 50);
        registrationButton.setFont(bigFont);
        changePassButton = new JButton("Change password");
        changePassButton.addActionListener(new changePassListener());
        changePassButton.setBounds(80, 370, 140, 50);
        changePassButton.setFont(bigFont);
        mainPanel.add(portNumLabel);
        mainPanel.add(portNumField);
        mainPanel.add(encryptPassLabel);
        mainPanel.add(encryptPassField);
        mainPanel.add(userNameLabel);
        mainPanel.add(userNameField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(responseField);
        mainPanel.add(registrationButton);
        mainPanel.add(changePassButton);
        mainPanel.add(testConnectionButton);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(300,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        client = new Client(responseField);
    }
    class testConnectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            client.testConnection();
        }
    }
    static class registrationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Registration Listener pressed");
        }
    }
    static class changePassListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Change Password Listener pressed");
        }
    }
}
