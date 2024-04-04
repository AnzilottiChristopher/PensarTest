import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ClientHandler implements Runnable
{
    //TCP
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private Socket clientSocket;




    //UDP
//    private DatagramSocket socketUDP;
//    private byte[] buffer;

    //Basic Information
    private String username;

    private static int nextCounter;

    private GameState questionProgress;


    public ClientHandler(Socket clientSocket, int portUDP)
    {
        this.clientSocket = clientSocket;

        UDPhandler handler = new UDPhandler(portUDP);
        Thread handlerThread = new Thread(handler);
        handlerThread.start();
        questionProgress = GameState.RUNNING;

        //TCP Setup
        try
        {
            out = new DataOutputStream(this.clientSocket.getOutputStream());
            in = new DataInputStream(this.clientSocket.getInputStream());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void run()
    {
        System.out.println(Server.returnState());
        while (Server.returnState() == GameState.RUNNING)
        {
            //System.out.println("here");
            if (Server.questionProgress == GameState.SENDING && questionProgress == GameState.RUNNING)
            {
                try
                {
                    System.out.println("TCP Test");
                    out.writeUTF("This is testing");
                    out.flush();
                    questionProgress = GameState.ANSWERING;
                    System.out.println("Sent");
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            } else if (questionProgress == GameState.ANSWERING)
            {
                //System.out.println("Waiting");
            }
        }
    }

    public synchronized boolean nextQuestionCounter()
    {
        //This is so that when every client is finished we can move on
        //Might not be needed if only one person is answering
        nextCounter++;

        if (nextCounter == Server.returnNumClients())
        {
            return true;
        }

        return false;
    }
}
