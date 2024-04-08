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

    private int answer;

    private int score;

    private static final String KILL_MSG = "Kill Switch";

    private boolean closeBool;


    public ClientHandler(Socket clientSocket, UDPhandler handler, String ClientID)
    {
        this.clientSocket = clientSocket;
        this.handler = handler;
        this.clientID = ClientID;
        score = 0;
        closeBool = false;

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
            if (clientSocket.isConnected())
            {
                closeEverything(clientSocket, in, out, questionOut);
            }
        }
    }
    @Override
    public void run()
    {
        System.out.println(clientID);
        //System.out.println(Server.returnState());
        while (Server.returnState() == GameState.RUNNING && clientSocket.isConnected())
        {
            
//            String incomingMessage;
//            try {
//                incomingMessage = in.readUTF();
//                if (incomingMessage.equals(KILL_MSG)) {
//                    try {
//                        clientSocket.close();
//                        break;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

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
                            answer = 2;
                            break;
                        case 2:
                            filePath = "Questions/Question 2.txt";
                            answer = 3;
                            //System.out.println("Case 2");
                            break;
                        case 3:
                            filePath = "Questions/Question 3.txt";
                            answer = 3;
                            break;
                        case 4:
                            filePath = "Questions/Question 4.txt";
                            answer = 1;
                            break;
                        case 5:
                            filePath = "Questions/Question 5.txt";
                            answer = 1;
                            break;
                        case 6:
                            filePath = "Questions/Question 6.txt";
                            answer = 4;
                            break;
                        case 7:
                            filePath = "Questions/Question 7.txt";
                            answer = 1;
                            break;
                        case 8:
                            filePath = "Questions/Question 8.txt";
                            answer = 2;
                            break;
                        case 9:
                            filePath = "Questions/Question 9.txt";
                            answer = 1;
                            break;
                        case 10:
                            filePath = "Questions/Question 10.txt";
                            answer = 4;
                            break;
                        case 11:
                            filePath = "Questions/Question 11.txt";
                            answer = 1;
                            break;
                        case 12:
                            filePath = "Questions/Question 12.txt";
                            answer = 2;
                            break;
                        case 13:
                            filePath = "Questions/Question 13.txt";
                            answer = 4;
                            break;
                        case 14:
                            filePath = "Questions/Question 14.txt";
                            answer = 2;
                            break;
                        case 15:
                            filePath = "Questions/Question 15.txt";
                            answer = 2;
                            break;
                        case 16:
                            filePath = "Questions/Question 16.txt";
                            answer = 1;
                            break;
                        case 17:
                            filePath = "Questions/Question 17.txt";
                            answer = 3;
                            break;
                        case 18:
                            filePath = "Questions/Question 18.txt";
                            answer = 2;
                            break;
                        case 19:
                            filePath = "Questions/Question 19.txt";
                            answer = 1;
                            break;
                        case 20:
                            filePath = "Questions/Question 20.txt";
                            answer = 4;
                            break;
                    }
//                    if(!filePath.equals("Hello"))
//                    {
//                        System.out.println("It's the filepath");
//                    }

                   // System.out.println(Server.returnQuestionNumber()); //Why is this constantly running
                    String[] question = toStringArray(filePath);

                    questionOut.writeObject(question);
                    questionOut.flush();

                    //out.writeUTF("This is testing");

                    questionProgress = GameState.ANSWERING;
                    //System.out.println("Sent");
                } catch (IOException e)
                {
                    //throw new RuntimeException(e);
                    if (clientSocket.isConnected())
                    {
                        closeEverything(clientSocket, in, out, questionOut);
                        //System.out.println("Closing here");
                    }
                    break;
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
                        //System.out.println("Here we are");
                        try
                        {
                            firstQueue();
                        } catch (IOException e)
                        {
                            if (clientSocket.isConnected())
                            {
                                closeEverything(clientSocket, in, out, questionOut);
                                //System.out.println("Closing here2");

                            }
                            break;
                        }
                        synchronized (lock)
                        {
                            waiting = false;
                            lock.notify();
                        }

                        handler.clearQueue();
                        //System.out.println("made it");
                    }  else if (!handler.peek().equals(clientID) && Server.returnNumClients() != 1)
                    {
                        try
                        {
                            NACK();
                        } catch (IOException e)
                        {
                            if (clientSocket.isConnected())
                            {
                                closeEverything(clientSocket, in, out, questionOut);
                                //System.out.println("Closing here");

                            }
                            break;
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
                                    if (clientSocket.isConnected())
                                    {
                                        closeEverything(clientSocket, in, out, questionOut);
                                        //System.out.println("Closing here2");

                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }

        //Need to send who won
    }

    public void closeEverything(Socket clientSocket, DataInputStream in, DataOutputStream out, ObjectOutputStream questionOut)
    {
        //System.out.println("Close");
        if (!closeBool)
        {
            try
            {
                if (clientSocket != null)
                {
                    clientSocket.close();
                }
                if (in != null)
                {
                    in.close();
                }
                if (out != null)
                {
                    out.close();
                }
                if (questionOut != null)
                {
                    questionOut.close();
                }
                Server.removeClient(this);
                closeBool = true;
        } catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }


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
            System.out.println(answer);
            if (answer == this.answer)
            {
                score += 10;
                out.writeUTF("Correct");
                out.flush();
            }
            else
            {
                score -= 10;
                out.writeUTF("Incorrect");
                out.flush();
            }

            Server.switchQuestion();
            //System.out.println(answer);
        } catch (IOException e)
        {
            if (clientSocket.isConnected())
            {
                closeEverything(clientSocket, in, out, questionOut);
                //System.out.println("Closing here");
            }
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
            if (clientSocket.isConnected())
            {
                closeEverything(clientSocket, in, out, questionOut);
                //System.out.println("Closing here");

            }
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
