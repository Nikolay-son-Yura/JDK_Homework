package org.example.lesson1;

public class Main {
    public static void main(String[] args) {
        ServerWindow serverWindow=new ServerWindow();
        new ClientGUI(serverWindow);
        new ClientGUI(serverWindow);
    }
}
