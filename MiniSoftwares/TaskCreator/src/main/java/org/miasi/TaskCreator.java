package org.miasi;

import javax.swing.*;

public class TaskCreator {
    private JButton button1;
    private JPanel jPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("TaskCreator");
        frame.setContentPane(new TaskCreator().jPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
