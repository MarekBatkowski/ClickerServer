import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        ArrayList<SocketThread> users = new ArrayList<>();
        ArrayList<Game> games = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Server port number:");
        int portNumber = scanner.nextInt();

        ServerSocket serverSocket = null;
        int newThreadID = 0;

        try
        {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server listening at port " + portNumber);
        }
        catch (IOException e)
        {
            System.out.println("port already used");
            e.printStackTrace();
        }

        while(true)
        {
            try
            {
                SocketThread temp = new SocketThread(serverSocket.accept(), newThreadID++, users, games);
                temp.start();
                users.add(temp);
            }
            catch (IOException e)
            {
                System.out.println("Connection Error");
                e.printStackTrace();
            }
        }
    }
}
