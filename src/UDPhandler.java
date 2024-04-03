import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPhandler implements Runnable
{
    //This class is so Clienthandler doesn't get blocked

    //UDP info
    private DatagramSocket socketUDP;
    private byte[] buffer;

    public UDPhandler()
    {
        try
        {
            this.socketUDP = new DatagramSocket(5000);
        } catch (SocketException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run()
    {
        while(Server.returnState() == GameState.RUNNING)
        {

        }
    }
}
