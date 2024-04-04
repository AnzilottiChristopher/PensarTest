import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
    private String clientID;

    private static int nextCounter;

    private GameState questionProgress;

    private UDPhandler handler;

    private static final Boolean lock = false;

    private static boolean waiting = true;

    public ClientHandler(Socket clientSocket, UDPhandler handler, String ClientID)
    {
        this.clientSocket = clientSocket;
        this.handler = handler;
        this.clientID = clientID;

//        UDPhandler handler = new UDPhandler(portUDP);
//        Thread handlerThread = new Thread(handler);
//        handlerThread.start();
        questionProgress = GameState.SENDING;

        //TCP Setup
        try
        {
            out = new DataOutputStream(this.clientSocket.getOutputStream());
            in = new DataInputStream(this.clientSocket.getInputStream());
            out.writeUTF(clientID);
            out.flush();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run()
    {
        System.out.println(clientID);
        //System.out.println(Server.returnState());
        while (Server.returnState() == GameState.RUNNING)
        {
            //System.out.println("here");
            if (questionProgress == GameState.SENDING)
            {
                try
                {
                    //This can be used to send tcp data
                    //System.out.println("TCP Test");
                    switch (Server.returnQuestionNumber())
                    {
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                        case 8:
                            break;
                        case 9:
                            break;
                        case 10:
                            break;
                        case 11:
                            break;
                        case 12:
                            break;
                        case 13:
                            break;
                        case 14:
                            break;
                        case 15:
                            break;
                        case 16:
                            break;
                        case 17:
                            break;
                        case 18:
                            break;
                        case 19:
                            break;
                        case 20:
                            break;
                    }
                    out.writeUTF("This is testing");
                    out.flush();
                    questionProgress = GameState.ANSWERING;
                    //System.out.println("Sent");
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            } else if (questionProgress == GameState.ANSWERING)
            {
                //System.out.println("Waiting");
                if (handler.peek() == clientID)
                {
                    waiting = true;
                    firstQueue();
                    synchronized (lock)
                    {
                        waiting = false;
                        lock.notify();
                    }
                } else if (handler.peek() != null && handler.peek() != clientID)
                {
                    NACK();
                    synchronized (lock)
                    {
                        while(waiting)
                        {
                            try
                            {
                                lock.wait();
                                System.out.println("waiting");
                            } catch (InterruptedException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    public void firstQueue()
    {
        try
        {
            out.writeUTF("You were first");
            out.flush();

            int answer = in.readInt();
            System.out.println(answer);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        questionProgress = GameState.SENDING;
    }

    public String getClientID() {
        return clientID;
    }
    
    public void NACK()
    {
        try
        {
            out.writeUTF("You were not first");
            out.flush();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        questionProgress = GameState.SENDING;
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
