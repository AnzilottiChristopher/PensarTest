import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
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

    public Client()
    {
        try
        {
            socket = new Socket("10.111.112.215", 5000);
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
}
