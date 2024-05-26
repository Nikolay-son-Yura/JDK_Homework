package org.example.lesson1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerWindow extends JFrame {
    private static final int POS_X = 500;
    private static final int POS_Y = 550;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private static final String LOG_PATH = "src/main/java/org/example/lesson1/log.txt";

    private JButton btnStart = new JButton("Start");
    private JButton btnStop = new JButton("Stop");
    private JPanel panBottom = new JPanel(new GridLayout(1, 2));

    private JTextArea log = new JTextArea();
    private boolean isServerWorking;
    private ArrayList<ClientGUI> clientGUIArrayList;


    public ServerWindow() {
        clientGUIArrayList = new ArrayList<>();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat server");
        setAlwaysOnTop(true);
        createPanel();

        setVisible(true);
    }

    //подключение пользователей
    public boolean connectUser(ClientGUI clientGUI) {
        if (!isServerWorking) {
            return false;
        }
        clientGUIArrayList.add(clientGUI);
        return true;
    }

    public String getLog() {
        return readLog();
    }

    public void disconnectUser(ClientGUI clientGUI) {
        clientGUIArrayList.remove(clientGUI);
        if (clientGUI != null) {
            clientGUI.disconnectFromServer();
        }
    }

    public void message(String text) {
        if (!isServerWorking) {
            return;
        }
        appendLog(text);
        answerAll(text);
        saveMessageInLog(text);
    }

    private void answerAll(String text) {
        for (ClientGUI clientGUI : clientGUIArrayList) {
            clientGUI.answer(text);
        }
    }

    //запись файла
    private void saveMessageInLog(String text) {
        try (FileWriter writer = new FileWriter(LOG_PATH, true)) {
            writer.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(LOG_PATH)) {
            int c;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void appendLog(String text) {
        log.append(text + "\n");
    }

    private void createPanel() {
        add(log);
        add(createButtons(), BorderLayout.SOUTH);
    }

    //добавление start/stop серверу
    public Component createButtons() {
        btnStart.addActionListener(e -> {
            if (isServerWorking) {
                appendLog(date());
                appendLog("Сервер уже был запущен");
            } else {
                isServerWorking = true;
                appendLog(date());
                appendLog("Сервер запущен!");
            }
        });
        btnStop.addActionListener(e -> {
            if (!isServerWorking) {
                appendLog(date());
                appendLog("Сервер уже был остановлен");
            } else {
                isServerWorking = false;
                while (!clientGUIArrayList.isEmpty())
                    disconnectUser(clientGUIArrayList.getLast());
                appendLog(date());
                appendLog("Успешная остановка сервера!");
            }
        });

        panBottom.add(btnStart);
        panBottom.add(btnStop);
        return panBottom;
    }

    public String date() {
        Date date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return formater.format(date);
    }
}
