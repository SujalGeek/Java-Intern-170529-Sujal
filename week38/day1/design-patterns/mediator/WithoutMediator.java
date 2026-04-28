package mediator;

import java.util.ArrayList;
import java.util.List;

class User{
    private String name;
    private List<User> peers;
    private List<String> mutedUsers;


    public User(String n)
    {
        this.name = n;
        peers = new ArrayList<>();
        mutedUsers = new ArrayList<>();
    }

    // must manaually correct every pair -> N^2 wiring

    public void addPeer(User u)
    {
        peers.add(u);
    }

    // duplication: everyone has its own mute list
    public void mute(String userToMute)
    {
        mutedUsers.add(userToMute);
    }

    // broadcast to all users
    public void send(String msg)
    {
        System.out.println("[" + name + "broadcasts]: "+msg);

    for(User peer: peers)
    {
        // if they have muted me dont send.
        if(!peer.isMuted(name))
        {
            peer.receive(name, msg);
        }
    }
}

public boolean isMuted(String userName){
    for(String name: mutedUsers)
    {
        if(name.equals(userName))
        {
            return true;
        }
    }
    return false;
}

 // private send - duplicated in every class
 public void sendTo(User target,String msg)
 {
    System.out.println(" ["+ name + " ->"+target.name + " "+msg);
    if(!target.isMuted(name))
    {
        target.receive(name,msg);
    }
}

public void receive(String from,String msg)
{
    System.out.println("    "+name+ "  got from "+ from+": "+msg);
}
}

public class WithoutMediator {
    public static void main(String[] args) {
        User user1 = new User("Rohan");
        User user2 = new User("Neha");
        User user3 = new User("Mohan");


          // wire up peers (each knows each other) → n*(n-1)/2 connections
        user1.addPeer(user2);   
        user2.addPeer(user1);

        user1.addPeer(user3);   
        user3.addPeer(user1);
        
        user2.addPeer(user3); 
        user3.addPeer(user2);
        
        // mute example: Mohan mutes Rohan (Hence Rohan add Mohan to its muted list).
        user1.mute("Mohan");
        
        // broadcast
        user1.send("Hello everyone!");
        
        // private
        user3.sendTo(user2, "Hey Neha!");
    }
}

