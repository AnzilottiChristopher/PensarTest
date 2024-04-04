import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client implements Runnable
{
    //TCP Data
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private ObjectInputStream questionInput = null;

    //UDP Data
    private DatagramSocket buzzer = null;
    private byte[] buffer = new byte[256];
    private byte[] questionBuffer = new byte[256];

    private static String[] question;

    //Client Data
    private String userName;

    private String clientID;
    private String button;
    private static boolean change;
    
    

    public static boolean isChange() {
        return change;
    }

    public static void setChange(boolean change) {
        change = change;
    }

    public static String[] getQuestion() {
        return question;
    }

    public void setQuestion(String[] question) {
        this.question = question;
    }

    public Client(String userName)
    {
        this.userName = userName;
        try
        {
            socket = new Socket("localhost", 5000);
            System.out.println("Connected");

            //Initialize TCP Input Outputs
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            questionInput = new ObjectInputStream(socket.getInputStream());

            //question = new String[5];


            //UDP
            this.buzzer = new DatagramSocket();

        } catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

     public void sendUsername() {
        try {
            InetAddress serverAddress = socket.getInetAddress(); 
            int serverPort = 5000;
            buffer = clientID.getBytes(StandardCharsets.UTF_8);
            //System.out.println(clientID);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            buzzer.send(packet);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void submitButton(int answer, boolean isSubmit){
        try {
//            InetAddress serverAddress = socket.getInetAddress();
//            int serverPort = 5000;
            if (isSubmit == true){
                System.out.println("Final answer: " + answer);
//                socket = new Socket(serverAddress, serverPort);
//                output = new DataOutputStream(socket.getOutputStream());
                output.writeInt(answer);
                output.flush();
            } else {
                System.out.println("Selected answer: " + answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void selectedAnswer(int answer) {
    //     try {
    //         output.writeInt(answer);
    //         output.flush();
    //         System.out.println("Selected answer: " + answer);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    @Override
    public void run()
    {
        while(socket.isConnected())
        {
            try
            {
                //Receiving ID
                clientID = input.readUTF();

                //System.out.println("right before");
                question = (String[]) questionInput.readObject();
                change = true;

                //System.out.println(question[0]);
                //System.out.println("Got it");
                System.out.println(change);

                String received = input.readUTF();
                System.out.println(received);
            } catch (IOException e)
            {
                try
                {
                    socket.close();
                } catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
                break;
            } catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }
        //System.out.println("Right after while");
    }
}
