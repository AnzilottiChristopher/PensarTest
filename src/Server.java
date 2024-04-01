import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable
{
    private ServerSocket socket;
    private int portNum;
    private GameState state;

    public Server(int portNum)
    {
        this.portNum = portNum;
        state = GameState.RUNNING;
        try
        {
            //Start the server with inputted port number
            socket = new ServerSocket(portNum);
            System.out.println("Server is running");

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run()
    {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try
        {
            //While the game is in the state running accept new players
            while(state == GameState.RUNNING)
            {
                //Accepts new connections
                Socket clientSocket = null;
                clientSocket = socket.accept();

                if (clientSocket != null)
                {
                    //Create a new clienthandler object and spit it off into a thread
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    executorService.execute(clientHandler);
                }
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
