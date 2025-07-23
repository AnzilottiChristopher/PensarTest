import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client implements Runnable
{
    //TCP Data
    private SSLSocket socket = null;
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

    private int userScore;


    private int questionCounter;
    private boolean duration;

    // Custom ObjectInputStream to restrict deserialization to allowed classes
    private static class SafeObjectInputStream extends ObjectInputStream {
        public SafeObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String className = desc.getName();
            // Only allow deserialization of String[] class
            if ("[Ljava.lang.String;".equals(className)) {
                return super.resolveClass(desc);
            } else {
                throw new InvalidClassException("Unauthorized deserialization attempt", className);
            }
        }
    }

    public static boolean isChange() {
        return change;
    }

    public static void setChange(boolean change) {
        Client.change = change;
    }

    public static String[] getQuestion() {
        return question;
        return question;
    }

    public void setQuestion(String[] question) {
        this.question = question;
    }

    public Client(String userName)
    {
        this.userName = userName;
        userScore = 0;
        duration = false;
        questionCounter = 1;
        try
        {
            String serverIP = getUserInput("Enter the server IP address:");
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            //Initialize TCP Input Outputs
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            questionInput = new SafeObjectInputStream(socket.getInputStream());

            //UDP
            this.buzzer = new DatagramSocket();

        } catch (UnknownHostException e)
        } catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

        try {
            InetAddress serverAddress = socket.getInetAddress(); 
            int serverPort = 5000;
            buffer = clientID.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            buzzer.send(packet);
        } catch (IOException ex) {
            ex.printStackTrace();
            ex.printStackTrace();
    }

    public void submitButton(int answer, boolean isSubmit){
        try {
            if (isSubmit == true){
                System.out.println("Final answer: " + answer);
                output.writeInt(answer);
                output.flush();
            } else {
                System.out.println("Selected answer: " + answer);
                System.out.println("Selected answer: " + answer);
            }
            closeEverything();
        }
    }

    @Override
    public void run()
    {
        while(socket.isConnected())
        {
            try
            {
                //Receiving ID
                if (clientID == null)
                {
                    clientID = input.readUTF();
                }

                if (questionCounter == 21)
                {
                    System.out.println("In here");
                    String message = input.readUTF();
                    System.out.println(message);
                    closeEverything();
                    break;
                }

                question = (String[]) questionInput.readObject();
                questionCounter++;

                System.out.println(questionCounter);

                change = true;
                if (change && !duration)
                {
                    duration = true;
                }

                received = input.readUTF();
                String ack = received;
                System.out.println(received);

                //If they are the first to poll they can receive more information
                if (ack.equalsIgnoreCase("You were first"))
                {
                    ack = "done";
                    String response = input.readUTF();
                    if (response.equalsIgnoreCase("Correct"))
                    {
                        userScore += 10;
                    } else if (response.equalsIgnoreCase("Incorrect"))
                    {
                        userScore -= 10;
                    }
                    System.out.println(response);
                }

            } catch (IOException e)
            {
                closeEverything();
                break;
            } catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public Socket returnSocket()
    {
        return socket;
    }


    public synchronized String returnACK()
    {
        try
        {
            wait(500);

        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        if (received.equalsIgnoreCase("You were first"))
        {
            return received;
        } else return "Nothing";
    }

    public synchronized void closeEverything()
    {
        try
        {
            if (socket != null)
            {
                socket.close();
            }
            if (input != null)
            {
                input.close();
            }
            if (output != null)
            {
                output.close();
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    public boolean returnChange()
    {
        return duration;
    }

    private static String getUserInput(String prompt) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(prompt);
        return reader.readLine();
    }

    public int returnScore()
    {
        return userScore;
    }
}
