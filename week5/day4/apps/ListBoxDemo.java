import java.awt.*;
import java.awt.event.*;

class MyFrame1 extends Frame implements ItemListener, ActionListener {
  List l;
  Choice c;
  TextArea t1;

  public MyFrame1() {
    super("ListBox Demo");

    // The 'List' is created with 4 visible rows and 'true' for multiple selections.
    l = new List(4, true);
    c = new Choice();
    t1 = new TextArea(10, 30); // Adjusted rows for better display

    l.add("Monday");
    l.add("Tuesday");
    l.add("Wednesday");
    l.add("Thursday");
    l.add("Friday");
    l.add("Saturday");
    l.add("Sunday");

    c.add("January");
    c.add("February"); // Corrected spelling
    c.add("March");
    c.add("April");

    setLayout(new FlowLayout());

    add(l);
    add(c);
    add(t1);

    // Add listeners
    l.addItemListener(this);
    c.addItemListener(this);
    l.addActionListener(this);

    // Add a WindowListener to handle closing the window
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // This is triggered by a double-click on an item in the List
    // It's a good place to show all selected items from the List
    String[] selectedItems = l.getSelectedItems();
    StringBuilder sb = new StringBuilder();
    for (String item : selectedItems) {
      sb.append(item).append("\n");
    }
    t1.setText(sb.toString());
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    // This is triggered by a single-click selection on the List or Choice
    if (e.getSource() == l) {
      // Get all selected items from the List and append them
      String[] selectedItems = l.getSelectedItems();
      StringBuilder sb = new StringBuilder();
      for (String item : selectedItems) {
        sb.append(item).append("\n");
      }
      t1.setText(sb.toString());
    } else if (e.getSource() == c) {
      // Get the single selected item from the Choice
      t1.setText(c.getSelectedItem());
    }
  }
}

public class ListBoxDemo {
  public static void main(String[] args) {
    MyFrame1 f2 = new MyFrame1();
    f2.setSize(400, 400);
    f2.setVisible(true);
  }
}