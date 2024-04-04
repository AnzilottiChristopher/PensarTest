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

    //UDP Data
    private DatagramSocket buzzer = null;
    private byte[] buffer = new byte[256];
    private byte[] questionBuffer = new byte[256];

    //Client Data
    private String userName;
    private String button;

    public Client(String userName)
    {
        this.userName = userName;
        try
        {
            socket = new Socket("LocalHost", 5000);
            System.out.println("Connected");

            //Initialize TCP Input Outputs
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());


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
            buffer = userName.getBytes(StandardCharsets.UTF_8);
            System.out.println(userName);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            buzzer.send(packet);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void submitButton(int answer, boolean isSubmit){
        try {
            InetAddress serverAddress = socket.getInetAddress();
            int serverPort = 5000;
            if (isSubmit == true){
                System.out.println("Final answer: " + answer);
                socket = new Socket(serverAddress, serverPort);
                output = new DataOutputStream(socket.getOutputStream());
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
                //System.out.println("right before");

                //Create a way to write received file
                FileOutputStream fileOutputStream = new FileOutputStream("Question.txt");
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

                //Reading the file
                int bytesRead;
                while((bytesRead = input.read(buffer)) != -1)
                {
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                }
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                //System.out.println("Got it");
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
            }
        }
        //System.out.println("Right after while");
    }
}
