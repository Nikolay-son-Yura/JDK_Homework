package org.example.lesson1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JTextArea log = new JTextArea();

    private JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private JTextField tfIPAddress = new JTextField("127.0.0.1");
    private JTextField tfPort = new JTextField("8189");
    private JTextField tfLogin = new JTextField("ivan_Igorovich");
    private JPasswordField tfPassword = new JPasswordField("123456");
    private JButton btnLogin = new JButton("Login");

    private JPanel panelBottom = new JPanel(new BorderLayout());
    private JTextField tfMessage = new JTextField();
    private JButton btnSend = new JButton("Send");

    private ServerWindow server;
    private boolean connected;
    private String name;

    public ClientGUI(ServerWindow server) {
        this.server = server;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setSize(WIDTH, HEIGHT);
        setLocation(server.getX() + 500, server.getY());
        setTitle("Chat client");

        createPanel();

        setVisible(true);
    }

    public void answer(String text) {
        appendLog(text);
    }

    private void connectToServer() {
        if (server.connectUser(this)) {
            appendLog(server.date());
            appendLog("Вы подключились к серверу!\n");
            panelTop.setVisible(false);
            connected = true;
            name = tfLogin.getText();
            String log = server.getLog();
            if (log != null) {
                appendLog(log);
            }
        } else {
            appendLog(server.date());
            appendLog("Нет подключения к серверу");
        }
    }

    public void disconnectFromServer() {
        if (connected) {
            panelTop.setVisible(true);
            connected = false;
            server.disconnectUser(this);
            appendLog(server.date());
            appendLog("Вы были отключены от сервера!");
        }
    }

    public void message() {
        if (connected) {
            String text = tfMessage.getText();
            if (!text.isEmpty()) {
                server.date();
                server.message(name + ": " + text);
                tfMessage.setText("");
            }
        } else {
            appendLog("Нет ответа от сервера");
        }
    }

    private void appendLog(String text) {
        log.append(text + "\n");
    }
    private void createPanel(){
        add(createPanelUser(), BorderLayout.NORTH);
        add(createChat());
        add(createToSend(), BorderLayout.SOUTH);
    }

    private Component createPanelUser() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(new JPanel());
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);
        return panelTop;
    }

    private Component createChat() {
        log.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(log);
        add(scrollLog);
        return scrollLog;
    }

    private Component createToSend() {
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    message();
                }
            }
        });
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();
            }
        });
        panelBottom.add(tfMessage);
        panelBottom.add(btnSend, BorderLayout.EAST);
        return panelBottom;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            disconnectFromServer();
        }
        super.processWindowFocusEvent(e);
    }

}
