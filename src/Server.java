import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable
{
    private ServerSocket socket;
    private int portNum;
    private static GameState state;

    //private int counter;

    private static int numClients;

    private static int questionNumber;

    private UDPhandler handler;

    private String clientID;

    private static List<ClientHandler> clientHandlers;

    private static final String KILL_MSG = "Kill Switch";

    private static int finalScore = 0;

    public Server(int portNum)
    {
        this.portNum = portNum;
        //counter = 0;
        numClients = 0;
        questionNumber = 1;
        this.clientHandlers = new ArrayList<>();
        state = GameState.RUNNING;
        try
        {
            //Start the server with inputted port number
            socket = new ServerSocket(portNum);
            handler = new UDPhandler();
            Thread handlerThread = new Thread(handler);
            handlerThread.start();
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
            while(state == GameState.RUNNING && questionNumber < 21)
            {
                //Accepts new connections
                Socket clientSocket = null;
                if (questionNumber < 21)
                {
                    clientSocket = socket.accept();
                }

                if (clientSocket != null)
                {
                    //Create a new clienthandler object and spit it off into a thread
                    clientID = "Client" + (clientHandlers.size() + 1);
                    //System.out.println(clientID);
                    ClientHandler clientHandler = new ClientHandler(clientSocket, handler, clientID);
                    executorService.execute(clientHandler);
                    synchronized(Server.class) {
                        clientHandlers.add(clientHandler);
                    }
                    //counter++;
                    numClients++;
                }


            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            socket.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static GameState returnState()
    {
        return state;
    }

    public static int returnNumClients()
    {
        return numClients;
    }

    public static void endGame()
    {
        //System.out.println("In endgame");
        finalScore = -500;
        state = GameState.END;
        for(ClientHandler clientHandler : clientHandlers)
        {
            finalScore = clientHandler.returnScore();
            if (clientHandler.returnScore() > finalScore)
            {
                //System.out.println("in if");
                finalScore = clientHandler.returnScore();
                //System.out.println(finalScore);
            }
        }
    }

    public static int returnFinalScore()
    {
        return finalScore;
    }

    public static synchronized void removeClient(ClientHandler clientHandler) {
        System.out.println("Client disconnected: " + clientHandler.getClientID());
        clientHandlers.remove(clientHandler);
    }

    public List<String> getAllClientIDs() {
        List<String> clientIDs = new ArrayList<>();
        for (ClientHandler handler : clientHandlers) {
            clientIDs.add(handler.getClientID());
        }
        return clientIDs;
    }

     public void sendKillSwitchMessage(String clientID) {
        for (ClientHandler handler : clientHandlers) {
            if (handler.getClientID().equals(clientID)) {
                handler.sendKillSwitchMessage();
                break;
            }
        }
    }

    public void sendKillSwitchMessageToAll() {
        for (ClientHandler handler : clientHandlers) {
            handler.sendKillSwitchMessage();
        }
    }

    public synchronized static int returnQuestionNumber()
    {
        return questionNumber;

        //return 1;
    }

    public static synchronized void switchQuestion()
    {
        questionNumber++;


        System.out.println(questionNumber);
    }
    public static void main(String[] args)
    {
        Server server = new Server(5000);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(server);

        while(state == GameState.RUNNING)
        {
            //System.out.println(returnQuestionNumber());
            if (returnQuestionNumber() == 21)
            {
                //System.out.println("End game1");

                endGame();
                //System.out.println("End game2");
            }
        }
    }
}
