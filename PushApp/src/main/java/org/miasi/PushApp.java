package org.miasi;

import javax.swing.*;

public class PushApp {
    private JPanel jPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("PushApp");
        frame.setContentPane(new PushApp().jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
