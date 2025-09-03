import java.awt.*;
import java.awt.event.*;

// This is a demo for the TextArea component.
class MyFrame2 extends Frame implements ActionListener {
  // These are instance variables, accessible throughout the class.
  TextArea t1;
  TextField t2;
  Label l;
  Button b1;

  public MyFrame2() {
    super("TextArea Demo");

    // The following code was inside a misplaced initializer block.
    // It belongs directly in the constructor.
    l = new Label("No Text Entered");
    t1 = new TextArea(10, 30);
    t2 = new TextField(30);
    b1 = new Button("Click!!");

    // We register the current instance of MyFrame2 as the listener for the button.
    b1.addActionListener(this);

    // Set the layout and add components to the frame.
    setLayout(new FlowLayout());
    add(t1);
    add(l);
    add(t2);
    add(b1);

    // Add a WindowListener to handle the close button.
    // This is crucial for the application to terminate properly.
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  // This method is called when the button is clicked.
  @Override
  public void actionPerformed(ActionEvent e) {
    // We get the text from t2 and insert it into t1 at the current caret position.
    // This is a great way to handle text manipulation in a TextArea.
    t1.insert(t2.getText(), t1.getCaretPosition());

    // After inserting, we can clear the TextField for the next input.
    t2.setText("");
  }
}

public class TextAreaDemo {
  public static void main(String[] args) {
    MyFrame2 f2 = new MyFrame2();
    f2.setSize(400, 400);
    f2.setVisible(true);
  }
}