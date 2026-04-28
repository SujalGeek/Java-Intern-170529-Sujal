

interface WalkableRobot{
    void walk();
}

class NormalWalk implements WalkableRobot{

    @Override
    public void walk() {
        // throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("Walking normally...");
    }

}

class NoWalk implements WalkableRobot{
     @Override
    public void walk() {
        // throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("No walk.");
    }
}

interface TalkableRobot {
    void talk();
}

class NormalTalk implements TalkableRobot {
    public void talk() {
        System.out.println("Talking normally...");
    }
}

class NoTalk implements TalkableRobot{

    @Override
    public void talk() {
        // throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("No talk....");
    }
}

interface FlyableRobot{
    void fly();
}

class NormalFly implements FlyableRobot{

    @Override
    public void fly() {
        System.out.println("Flying normally...");
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}

class NoFly implements FlyableRobot{

    @Override
    public void fly() {
      System.out.println("Cannot fly...");
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}

abstract class Robot{
    protected WalkableRobot walkableRobot;
    protected TalkableRobot talkableRobot;
    protected FlyableRobot flyableRobot;

    public Robot(WalkableRobot w,TalkableRobot t,FlyableRobot fy)
    {
        this.walkableRobot = w;
        this.talkableRobot = t;
        this.flyableRobot = fy;
    }

    public void walk(){
        walkableRobot.walk();
    }

    public void talk(){
        talkableRobot.talk();
    }

    public void fly(){
        flyableRobot.fly();
    }

    public abstract void projection();
}

class CompanionRobot extends  Robot{

    public CompanionRobot(WalkableRobot w,TalkableRobot t,FlyableRobot fy)
    {
        super(w, t, fy);
    }

    @Override
    public void projection() {
     System.out.println("Displaying worker efficiency stats..");
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}


class WorkerRobot extends Robot {
    public WorkerRobot(WalkableRobot w, TalkableRobot t, FlyableRobot f) {
        super(w, t, f);
    }

    public void projection() {
        System.out.println("Displaying worker efficiency stats...");
    }
}

public class StrategyPattern{
    public static void main(String[] args) {
          Robot robot1 = new CompanionRobot(new NormalWalk(), new NormalTalk(), new NoFly());
        robot1.walk();
        robot1.talk();
        robot1.fly();
        robot1.projection();

        System.out.println("--------------------");

        Robot robot2 = new WorkerRobot(new NoWalk(), new NoTalk(), new NormalFly());
        robot2.walk();
        robot2.talk();
        robot2.fly();
        robot2.projection();
    }
}