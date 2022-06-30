package org.example;

import Tool.Client;
import Tool.MainWindow;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() ->{
            MainWindow mainWindow = new MainWindow();
        });
    }
}