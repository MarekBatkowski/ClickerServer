import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SocketThread extends Thread
{
    private String name;
    private Socket socket;
    private int ID;
    private ArrayList<SocketThread> users;
    private ArrayList<Game> games;
    private Game currGame;
    private PrintWriter clientOut;
    private BufferedReader clientIn;

    public SocketThread(Socket socket, int ID, ArrayList<SocketThread> users, ArrayList<Game> games)
    {
        this.socket = socket;
        this.ID = ID;
        this.name = "User_"+ID;
        this.users = users;
        this.games = games;
        this.currGame = null;
    }

    public void changeName(String newName)
    {
        this.name = newName;
    }

    public String returnName()     // getName already exists for Thread class : /
    {
        return name;
    }

    public PrintWriter getClientOut()
    {
        return clientOut;
    }

    public void respond(String message, PrintWriter clientOut)
    {
        System.out.println(name + "->: "+ message);
        clientOut.println("Server : "+ message);
    }

    @Override
    public void run()
    {
        try
        {
            clientOut = new PrintWriter(socket.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            clientOut.println("Connected new client: "+ name);
            String line = clientIn.readLine();
            System.out.println(name + ": " + line);

            while(true)
            {
                line = clientIn.readLine();
                System.out.println(name +": " + line); // print the command
                String[] command = line.split("\\s+");

                // exit
                if(command[0].equals("exit") && command.length==1)
                {
                    if(this.currGame != null)
                        currGame.deleteUser(this);

                    System.out.println(name + " is disconected");
                    users.remove(this);
                    socket.close();
                    clientIn.close();
                    clientOut.close();
                    return;
                }

                // users list
                else if(command[0].equals("users") && command[1].equals("list") && command.length==2)
                {
                    String message = "Users List:";
                    for(int i=0; i<users.size(); i++)
                        message += ("\n"+users.get(i).returnName());
                    respond(message, clientOut);
                }

                // games list
                else if(command[0].equals("games") && command[1].equals("list") && command.length==2)
                {
                    String message = "Games List:";
                    for(int i=0; i<games.size(); i++)
                        message += ("\n"+games.get(i).returnName());
                    respond(message, clientOut);
                }

                // change name <new name>
                else if(command[0].equals("change") && command[1].equals("name") && command.length==3)
                {
                    boolean exists = false;

                    for(SocketThread u : users)
                        if(u.returnName().equals(command[2]))   exists = true;

                    if(exists)
                        respond("Such user already exists", clientOut);
                    else
                    {
                        changeName(command[2]);
                        respond("name changed to " + command[2], clientOut);
                    }
                }

                //  create game <game name>
                else if(command[0].equals("create") && command[1].equals("game") && command.length==3)
                {
                    boolean exists = false;

                    for(Game g : games)
                        if(g.returnName().equals(command[2]))   exists = true;

                    if(exists)
                        respond("Game of such name already exists", clientOut);
                    else
                    {
                        Game newGame = new Game(command[2]);
                        games.add(newGame);
                        respond("Created game " + command[2], clientOut);
                        newGame.start();
                    }
                }

                //  join game <game name> <team>
                else if(command[0].equals("join") && command[1].equals("game") && command.length==4) // 'join' is followed' by 'game'
                {
                    if(this.currGame != null)
                        respond("You are a member of different game already", clientOut);
                    else
                    {
                        Game temp = null;

                        for(Game g : games)
                            if (g.returnName().equals(command[2]))  temp = g;

                        if(temp == null)
                            respond("Game of such name doesn't exist ", clientOut);
                        else if(command[3].equals("A"))
                        {
                            temp.addUserA(this);
                            respond("Added to game " + command[2] + " to team A", clientOut);
                            currGame = temp;
                        }
                        else if(command[3].equals("B"))
                        {
                            temp.addUserB(this);
                            respond("Added to game " + command[2] + " to team B", clientOut);
                            currGame = temp;
                        }
                        else respond("Pick a side! A or B", clientOut);
                    }
                }

                // quit game
                else if(command[0].equals("quit") && command[1].equals("game") && command.length==2)
                {
                    if(this.currGame == null)
                        respond("You're not in game!", clientOut);
                    else
                    {
                        respond("Exiting game " + this.currGame.returnName(), clientOut);
                        this.currGame.deleteUser(this);
                        this.currGame = null;
                    }
                }

                // clicking buton in game
                else if(command[0].equals("click") && command.length==1)
                {
                    int state = currGame.getState(this);

                    if(state>100)
                        respond("Game over Team B won!", clientOut);
                    else if(state<0)
                        respond("Game over Team A won!", clientOut);
                    else
                    {
                        currGame.updateGameState(this);
                    //    currGame.sendgameState();
                    }
                }
                else    respond("unknown command", clientOut);
            }
        }
        catch (SocketException s)
        {
            if(this.currGame != null)
                currGame.deleteUser(this);

            System.out.println(name + " is disconected");
            users.remove(this);
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
