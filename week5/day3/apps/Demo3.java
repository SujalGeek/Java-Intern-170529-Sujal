import java.awt.*;
import java.awt.event.*;

// Demo for thne CheckBox and Radio Buttons and group them in Panel

class MyFrame extends Frame implements ItemListener {
  // Member variables for all components.
  Label l;
  Checkbox c1, c2, c3;
  Checkbox c4, c5, c6;
  // CheckboxGroup is crucial for creating radio buttons.
  CheckboxGroup cbg;

  public MyFrame() {
    super("Checkbox and Radio Button Demo");

    // Use a 2x1 GridLayout to structure the layout.
    // The top row will be for checkboxes, the bottom for radio buttons.
    setLayout(new GridLayout(2, 1));

    // Create a Panel for the Checkboxes to group them together.
    Panel checkboxPanel = new Panel();
    checkboxPanel.setLayout(new FlowLayout());

    l = new Label("Nothing Selected");
    c1 = new Checkbox("Java");
    c2 = new Checkbox("Python");
    c3 = new Checkbox("C#");

    // Add ItemListeners to the checkboxes.
    c1.addItemListener(this);
    c2.addItemListener(this);
    c3.addItemListener(this);

    // Add components to the checkbox panel.
    checkboxPanel.add(c1);
    checkboxPanel.add(c2);
    checkboxPanel.add(c3);

    // Add the label and checkbox panel to the frame's top row.
    add(l);
    add(checkboxPanel);

    // --- Radio Button Section ---

    // Create a CheckboxGroup. This is what makes them mutually exclusive.
    cbg = new CheckboxGroup();

    // Create a Panel for the Radio Buttons.
    Panel radioPanel = new Panel();
    radioPanel.setLayout(new FlowLayout());

    // Create the radio buttons, associating each with the CheckboxGroup.
    // The second argument 'false' means none are selected initially.
    c4 = new Checkbox("Male", false, cbg);
    c5 = new Checkbox("Female", false, cbg);
    c6 = new Checkbox("Other", false, cbg);

    // Add ItemListeners to the radio buttons.
    c4.addItemListener(this);
    c5.addItemListener(this);
    c6.addItemListener(this);

    // Add components to the radio button panel.
    radioPanel.add(c4);
    radioPanel.add(c5);
    radioPanel.add(c6);

    // Add the radio button panel to the frame's bottom row.
    add(radioPanel);

    // The addWindowListener must be in the constructor to work properly from the
    // start.
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    String str = "";

    // Checkboxes: Multiple can be selected at once.
    if (c1.getState()) {
      str += " " + c1.getLabel();
    }
    if (c2.getState()) {
      str += " " + c2.getLabel();
    }
    if (c3.getState()) {
      str += " " + c3.getLabel();
    }

    // Radio Buttons: Only one can be selected at a time.
    // We can get the label of the currently selected radio button from the
    // CheckboxGroup.
    if (cbg.getSelectedCheckbox() != null) {
      str += " " + cbg.getSelectedCheckbox().getLabel();
    }

    // If nothing is selected from either set, show the default message.
    if (str.trim().isEmpty()) {
      str = "Nothing is Selected";
    }

    l.setText(str.trim());
  }
}

public class Demo3 {
  public static void main(String[] args) {
    MyFrame f = new MyFrame();
    f.setSize(400, 200);
    f.setVisible(true);
  }
}