import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
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

    public Client()
    {
        try
        {
            socket = new Socket("", 5000);
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
            byte[] data = userName.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            buzzer.send(packet);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
