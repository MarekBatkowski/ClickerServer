import java.util.ArrayList;

public class Game
{
    String Name;
    ArrayList<SocketThread> TeamA;
    ArrayList<SocketThread> TeamB;

    private int state;

    public Game(String Name)
    {
        this.Name = Name;
        this.TeamA = new ArrayList<>();
        this.TeamB = new ArrayList<>();
        this.state = 50;
    }

    public void updateGameState(SocketThread user)
    {
        for(SocketThread u : TeamA)
            if(u == user)   state--;

        for(SocketThread u : TeamB)
            if(u == user)   state++;
    }

    public void sendgameState()
    {
        for (SocketThread u : TeamA)
            u.getClientOut().println("Game state: " + state);

        for (SocketThread u : TeamB)
            u.getClientOut().println("Game state: " + state);
    }

    public int getState(SocketThread user)
    {
        return state;
    }

    public String returnName()
    {
        return Name;
    }

    void addUserA(SocketThread user)
    {
        TeamA.add(user);
    }

    void addUserB(SocketThread user)
    {
        TeamB.add(user);
    }

    void deleteUser(SocketThread user)
    {
        for(SocketThread s : TeamA)
        {
            if(s.getName().equals(user.getName()))
            {
                String name = s.returnName();
                TeamA.remove(s);
                System.out.println("Removed user " + name);
                return;
            }
        }

        for(SocketThread s : TeamB)
        {
            if(s.getName().equals(user.getName()))
            {
                String name = s.returnName();
                TeamB.remove(s);
                System.out.println("Removed user " + name);
                return;
            }
        }
    }

    ArrayList<String> returnNamesA()
    {
        ArrayList<String> names = new ArrayList<>();
        for(SocketThread s : TeamA)
           names.add(s.returnName());
        return names;
    }

    ArrayList<String> returnNamesB()
    {
        ArrayList<String> names = new ArrayList<>();
        for(SocketThread s : TeamB)
            names.add(s.returnName());
        return names;
    }
}