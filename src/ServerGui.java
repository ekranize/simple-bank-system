import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGui {
    private JButton startServerButton;
    private JButton stopServerButton;
    private JTextField portNumField;
    private Server server;
    public void start() {
        JFrame frame = new JFrame("SBS - Server");
        server = new Server();
        Font bigFont = new Font("sanserif", Font.BOLD,18);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        JLabel portNumLabel = new JLabel("Server socket port number ");
        portNumLabel.setBounds(70, 50, 160, 30);
        portNumField = new JTextField(5);
        portNumField.setBounds(80, 100, 140, 20);
        portNumField.setText("12021");
        startServerButton = new JButton("Start server");
        startServerButton.addActionListener(new startServerListener());
        startServerButton.setBounds(80, 200, 140, 50);
        startServerButton.setFont(bigFont);
        stopServerButton = new JButton("Stop server");
        stopServerButton.addActionListener(new stopServerListener());
        stopServerButton.setBounds(80, 250, 140, 50);
        stopServerButton.setFont(bigFont);
        stopServerButton.setEnabled(false);
        mainPanel.add(portNumLabel);
        mainPanel.add(portNumField);
        mainPanel.add(startServerButton);
        mainPanel.add(stopServerButton);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(300,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    class startServerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (portNumField.getText().matches("[-+]?\\d+") && !server.isStarted()) {
                server.startServer(Integer.parseInt(portNumField.getText()));
                startServerButton.setEnabled(false);
                stopServerButton.setEnabled(true);
            }
        }
    }
    class stopServerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (server.isStarted()) {
                server.stopServer();
                stopServerButton.setEnabled(false);
                startServerButton.setEnabled(true);
            }
        }
    }
}
