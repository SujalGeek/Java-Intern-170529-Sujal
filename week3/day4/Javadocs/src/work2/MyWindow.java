package work2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyWindow {
    public static void main(String[] args) {

        // window: object Jframe
        JFrame frame = new JFrame("My Window");
        frame.setSize(400,400);
        frame.setLayout(new FlowLayout());

        JButton button = new JButton("Click Me");
        frame.add(button);

        button.addActionListener((e)->{
            System.out.println("Button Click");
            JOptionPane.showMessageDialog(null,"Hey , Button Clicked");
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }
}
