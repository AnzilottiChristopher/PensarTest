import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientHandler implements Runnable
{
    //TCP
    private DataOutputStream out = null;

    private ObjectOutputStream questionOut = null;
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

    private byte[] buffer;

    private static final String KILL_MSG = "Kill Switch";


    public ClientHandler(Socket clientSocket, UDPhandler handler, String ClientID)
    {
        this.clientSocket = clientSocket;
        this.handler = handler;
        this.clientID = ClientID;

        buffer = new byte[256];
//        UDPhandler handler = new UDPhandler(portUDP);
//        Thread handlerThread = new Thread(handler);
//        handlerThread.start();
        questionProgress = GameState.SENDING;

        //TCP Setup
        try
        {
            out = new DataOutputStream(this.clientSocket.getOutputStream());
            in = new DataInputStream(this.clientSocket.getInputStream());
            questionOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
            //System.out.println(clientID);
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
            
            String incomingMessage;
            try {
                incomingMessage = in.readUTF();
                if (incomingMessage.equals(KILL_MSG)) {
                    try {
                        clientSocket.close();
                        break; 
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //System.out.println("here");
            if (questionProgress == GameState.SENDING)
            {
                try
                {
                    String filePath = "Hello";
                    //This can be used to send tcp data
                    //System.out.println("TCP Test");
                    switch (Server.returnQuestionNumber())
                    {
                        case 1:
                            filePath = "Questions/Question 1.txt";
                            break;
                        case 2:
                            filePath = "Questions/Question 2.txt";
                            break;
                        case 3:
                            filePath = "Questions/Question 3.txt";
                            break;
                        case 4:
                            filePath = "Questions/Question 4.txt";
                            break;
                        case 5:
                            filePath = "Questions/Question 5.txt";
                            break;
                        case 6:
                            filePath = "Questions/Question 6.txt";
                            break;
                        case 7:
                            filePath = "Questions/Question 7.txt";
                            break;
                        case 8:
                            filePath = "Questions/Question 8.txt";
                            break;
                        case 9:
                            filePath = "Questions/Question 9.txt";
                            break;
                        case 10:
                            filePath = "Questions/Question 10.txt";
                            break;
                        case 11:
                            filePath = "Questions/Question 11.txt";
                            break;
                        case 12:
                            filePath = "Questions/Question 12.txt";
                            break;
                        case 13:
                            filePath = "Questions/Question 13.txt";
                            break;
                        case 14:
                            filePath = "Questions/Question 14.txt";
                            break;
                        case 15:
                            filePath = "Questions/Question 15.txt";
                            break;
                        case 16:
                            filePath = "Questions/Question 16.txt";
                            break;
                        case 17:
                            filePath = "Questions/Question 17.txt";
                            break;
                        case 18:
                            filePath = "Questions/Question 18.txt";
                            break;
                        case 19:
                            filePath = "Questions/Question 19.txt";
                            break;
                        case 20:
                            filePath = "Questions/Question 20.txt";
                            break;
                    }
//                    if(!filePath.equals("Hello"))
//                    {
//                        System.out.println("It's the filepath");
//                    }

                    String[] question = toStringArray(filePath);

                    questionOut.writeObject(question);
                    questionOut.flush();

                    //out.writeUTF("This is testing");

                    questionProgress = GameState.ANSWERING;
                    //System.out.println("Sent");
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            } else if (questionProgress == GameState.ANSWERING)
            {
                //System.out.println(handler.peek());
                //System.out.println("Waiting");
                if (handler.peek() != null)
                {
                    if (handler.peek().equalsIgnoreCase(clientID))
                    {
                        waiting = true;
                        System.out.println("Here we are");
                        try
                        {
                            firstQueue();
                        } catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                        synchronized (lock)
                        {
                            waiting = false;
                            lock.notify();
                        }

                        handler.clearQueue();
                    }  else if (!handler.peek().equals(clientID) && Server.returnNumClients() != 1)
                    {
                        try
                        {
                            NACK();
                        } catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                        synchronized (lock)
                        {
                            while (waiting)
                            {
                                try
                                {
                                    //System.out.println("Here in nack");
                                    //System.out.println(clientID);
                                    lock.wait();
                                    //System.out.println("waiting");
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

        //Need to send who won
    }

    public String[] toStringArray(String path) throws FileNotFoundException
    {
        File file = new File(path);

        if (!file.exists())
        {
            throw new FileNotFoundException();
        }
        Scanner scan = new Scanner(file);

        List<String> lines = new ArrayList<>();

        while(scan.hasNextLine())
        {
            String line = scan.nextLine();
            lines.add(line);
        }

        return lines.toArray(new String[0]);

    }

    public void firstQueue() throws IOException
    {
        try
        {
            out.writeUTF("You were first");
            out.flush();

            int answer = in.readInt();
            Server.switchQuestion();
            //System.out.println(answer);
        } catch (IOException e)
        {
            clientSocket.close();
        }

        questionProgress = GameState.SENDING;
    }

    public String getClientID() {
        return clientID;
    }

    public void sendKillSwitchMessage() {
        try {
            out.writeUTF(KILL_MSG);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void NACK() throws IOException
    {
        try
        {
            out.writeUTF("You were not first");
            out.flush();

        } catch (IOException e)
        {
            clientSocket.close();
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
