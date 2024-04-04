import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    //Client Data
    private String userName;

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

    @Override
    public void run()
    {
        while(socket.isConnected())
        {
            try
            {
                //System.out.println("right before");
                String received = input.readUTF();
                System.out.println(received);
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
