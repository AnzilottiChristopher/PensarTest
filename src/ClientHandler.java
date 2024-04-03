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


    public ClientHandler(Socket clientSocket, int portUDP)
    {
        this.clientSocket = clientSocket;


        UDPhandler handler = new UDPhandler(portUDP);
        Thread handlerThread = new Thread(handler);
        handlerThread.start();

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
        while (Server.returnState() == GameState.RUNNING)
        {

        }
    }
}
