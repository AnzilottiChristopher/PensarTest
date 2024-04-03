import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    //TCP
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private Socket clientSocket;


    //UDP
    private DatagramSocket socketUDP;
    private byte[] buffer;

    //Basic Information
    private String username;


    public ClientHandler(Socket clientSocket)
    {
        this.clientSocket = clientSocket;

        //TCP Setup
        try
        {
            out = new DataOutputStream(this.clientSocket.getOutputStream());
            in = new DataInputStream(this.clientSocket.getInputStream());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        //UDP Setup
    }
    @Override
    public void run()
    {

    }
}
