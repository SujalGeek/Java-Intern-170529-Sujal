import java.awt.*;
import java.awt.event.*;

// This is the demo for the Textfield.
class MyFrame1 extends Frame implements TextListener, ActionListener {

  Label l1, l2;
  TextField tf;

  public MyFrame1() {
    super("TextField Demo");
    l1 = new Label("No Text Entered");
    l2 = new Label("Enter key is not yet hit");
    tf = new TextField(20);

    // This is a comment to show an example of a password field.
    // tf.setEchoChar('*');

    // This is the key fix. The listeners are now "this" (the MyFrame1 instance).
    // MyFrame1 now handles the events, and its methods have direct access to l1,
    // l2, and tf.
    tf.addTextListener(this);
    tf.addActionListener(this);

    setLayout(new FlowLayout());
    add(l1);
    add(l2);
    add(tf);

    // Add a WindowListener to handle closing the window.
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  // Since the listeners are "this," we need to provide the implementation for the
  // events directly here.
  @Override
  public void textValueChanged(TextEvent e) {
    // We can directly access tf because this method is part of MyFrame1.
    l1.setText(tf.getText());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // We can directly access tf because this method is part of MyFrame1.
    l2.setText(tf.getText());
  }
}

public class TextFieldDemo {
  public static void main(String[] args) {
    MyFrame1 f = new MyFrame1();
    f.setSize(400, 400);
    f.setVisible(true);
  }
}