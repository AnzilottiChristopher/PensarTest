import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

public class UDPhandler implements Runnable
{
    //This class is so Clienthandler doesn't get blocked

    //UDP info
    private DatagramSocket socketUDP;
    private byte[] buffer;

    private static final int MAX_QUEUE_SIZE = 1000; // Define a max size for the queue
    private static Queue<String> queue = new LinkedList<>();

    public UDPhandler()
    {
        try
        {
            this.socketUDP = new DatagramSocket(5000);
            //queue.add("THIS IS IMPOSSIBLE");
            //buffer = new byte[256];
        } catch (SocketException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run()
    {
        buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while(Server.returnState() == GameState.RUNNING && socketUDP != null)
        {
            try
            {
                socketUDP.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                addQueue(received);

            } catch (IOException e)
            {
                closeEverything();
            }
        }
    }

    public void closeEverything()
    {
        try
        {
            if (socketUDP != null)
            {
                socketUDP.close();
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public synchronized void addQueue(String received)
    {
        if(queue.size() < MAX_QUEUE_SIZE) {
            queue.add(received);
            System.out.println(queue.peek());
        } else {
            // Queue is full, drop the message or handle overflow
            System.out.println("Queue is full, dropping incoming UDP message.");
        }
    }

    public synchronized void clearQueue()
    {
        queue.clear();
    }

    public synchronized String peek()
    {
        return queue.peek();
    }
}
