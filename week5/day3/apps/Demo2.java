import java.awt.*;
import java.awt.event.*;

class MyFrame extends Frame implements ActionListener {
  // Member variables for the counter, label, and button.
  int count = 0;
  Label l;
  Button b;
  // A new button to reset the count.
  Button resetButton;

  // Constructor for our custom Frame class.
  public MyFrame() {
    // Calls the parent class's (Frame) constructor to set the title.
    super("Super Cool Button Demo");

    // Use a FlowLayout to arrange components in a row.
    setLayout(new FlowLayout());

    // Initialize the label with the starting count.
    l = new Label("Count: " + count);

    // Initialize the button that increments the counter.
    b = new Button("Click Me!");
    // Add an action listener to the "Click Me!" button.
    // The 'this' keyword means this class (MyFrame) will handle the button's
    // action.
    b.addActionListener(this);

    // Initialize the new reset button.
    resetButton = new Button("Reset");
    // Add an action listener to the "Reset" button.
    resetButton.addActionListener(this);

    // Add the components to the frame in the order they should appear.
    add(l);
    add(b);
    add(resetButton);

    // Add a WindowListener to handle the close button (the 'X').
    // Without this, the program would keep running in the background.
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        // Terminate the application when the window is closed.
        System.exit(0);
      }
    });
  }

  // This method is called whenever an action is performed (e.g., a button is
  // clicked).
  @Override
  public void actionPerformed(ActionEvent e) {
    // Get the source of the action (which button was clicked).
    String command = e.getActionCommand();

    // Use an if-else statement to check which button was clicked.
    if (command.equals("Click Me!")) {
      // Increment the counter.
      count++;
      // Update the label's text to show the new count.
      l.setText("Count: " + count);
    } else if (command.equals("Reset")) {
      // Reset the counter to zero.
      count = 0;
      // Update the label's text to show the reset count.
      l.setText("Count: " + count);
    }
  }
}

public class Demo2 {
  public static void main(String[] args) {
    // Create an instance of MyFrame.
    MyFrame f = new MyFrame();

    // Set the size of the window (in pixels).
    f.setSize(300, 300);

    // Make the window visible on the screen.
    f.setVisible(true);
  }
}