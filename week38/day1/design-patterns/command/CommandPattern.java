package command;


// Command Interface
interface Command{
    void execute();
    void undo();
}

// Receivers
/*
Action ko object bana do
Request ko object me convert karna (encapsulate karna), taaki usko pass, store, undo ya queue kar sake

Command pattern encapsulates a request as an object,
allowing parameterization, queuing, logging, and undoable operations.

Button → Command → Receiver
*/

class Light{
    public void on(){
        System.out.println("Light is ON");
    }

    public void of(){
        System.out.println("Light is OFF");
    }
}

class Fan{
    public void on(){
        System.out.println("Fan is ON");
    }

    public void of(){
        System.out.println("Fan is OFF");
    }
}


// Concrete command for Light

class LightCommand implements Command{
    private Light light;

    public LightCommand(Light l)
    {
        this.light=l;
    }

    @Override
    public void execute() {
       light.on();
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void undo() {
        light.of();
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}


class FanCommand implements Command
{
    private Fan fan;

    public FanCommand(Fan f)
    {
        this.fan=f;
    }

    @Override
    public void execute() {
       fan.on();
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void undo() {
        fan.of();
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}

class RemoteController{
    private static final int numButtons = 4;
    private Command[] buttons;
    private boolean[] buttonPressed;

    public RemoteController(){
        buttons = new Command[numButtons];
        buttonPressed = new boolean[numButtons];

        for(int i=0;i<numButtons;i++)
        {
            buttons[i]= null;
            buttonPressed[i]= false;
        }
    }

    public void setCommand(int idx,Command cmd)
    {
        if(idx >= 0 && idx < numButtons)
        {
            buttons[idx] = cmd;
            buttonPressed[idx] = false;
        }
    }

    public void pressButton(int idx)
    {
        if(idx >=0 && idx < numButtons && buttons[idx] != null)
        {
            if(!buttonPressed[idx])
            {
                buttons[idx].execute();
            }
            else{
                buttons[idx].undo();
            }
            buttonPressed[idx] = !buttonPressed[idx];
        }else{
            System.out.println("No command assigned at button: "+idx);
        }
    }
}

public class CommandPattern {
    public static void main(String[] args) {
        Light livingRoomLight = new Light();
        Fan celingFan = new Fan();

        RemoteController remote = new RemoteController();

        remote.setCommand(0, new LightCommand(livingRoomLight));
        remote.setCommand(1, new FanCommand(celingFan));

         // Simulate button presses (toggle behavior)
        System.out.println("--- Toggling Light Button 0 ---");
        remote.pressButton(0);  // ON
        remote.pressButton(0);  // OFF

        System.out.println("--- Toggling Fan Button 1 ---");
        remote.pressButton(1);  // ON
        remote.pressButton(1);  // OFF

        // Press unassigned button to show default message
        System.out.println("--- Pressing Unassigned Button 2 ---");
        remote.pressButton(2);
    }    
}
