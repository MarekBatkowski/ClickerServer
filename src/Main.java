import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        ArrayList<SocketThread> users = new ArrayList<>();
        ArrayList<Game> games = new ArrayList<>();

        ServerSocket serverSocket = null;
        int newThreadID = 0;

        try
        {
            System.out.println("This host adress:");
        //    System.out.println(InetAddress.getLocalHost());
            System.out.println(InetAddress.getLocalHost().getHostAddress());

            Scanner scanner = new Scanner(System.in);
            System.out.println("Server port number:");
            int portNumber = scanner.nextInt();

            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server listening at port " + portNumber);
        }
        catch (UnknownHostException e)
        {
            System.out.println("Cannot get host name");
            e.printStackTrace();
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
