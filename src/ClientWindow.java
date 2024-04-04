import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;
public class ClientWindow implements ActionListener
{
	private JButton poll;
	private JButton submit;
	private JButton enter;
	private JRadioButton options[];
	private ButtonGroup optionGroup;
	JLabel question;
	private JLabel timer;
	private JLabel score;
	private TimerTask clock;
	private JTextField user;
	private JLabel scoreText;
	
	private JFrame window;
	private JFrame username;

	private String name;
	private int scoreNumber;
	private int guess;
	
	private static SecureRandom random = new SecureRandom();

	private String[] option = new String[4];

	Client client;
	
	public JTextField getUser() {
		return user;
	}

	public ClientWindow()
	{

		JOptionPane.showMessageDialog(window, "Enter A username to start");
		
		username = new JFrame("Username");
		username.setLayout(new FlowLayout());
		username.setBounds(10, 5, 250, 250);
		JTextField user = new JTextField();
		user.setPreferredSize(new Dimension(250, 40));
		username.setLocationRelativeTo(null);

		JButton enter = new JButton("Enter");
		enter.addActionListener(e -> System.out.println(user.getText()));
		enter.addActionListener(this);
		name = user.getText();

		client = new Client(name);
		Thread clientThread = new Thread(client);
		clientThread.start();
		//System.out.println(name);

		username.add(user);
		username.add(enter);
		username.pack();
		username.setVisible(true);
		//window.setVisible(false);
		
		window = new JFrame("Trivia");
		question = new JLabel("Q1. This is a sample question"); // represents the question
		window.add(question);

		question.setBounds(10, 5, 350, 100);;
		
		options = new JRadioButton[4];
		optionGroup = new ButtonGroup();
		for(int index=0; index<options.length; index++)
		{
			options[index] = new JRadioButton("Option " + (index+1));  // represents an option
			// if a radio button is clicked, the event would be thrown to this class to handle
			options[index].addActionListener(this);
			options[index].setBounds(10, 110+(index*20), 350, 20);
			window.add(options[index]);
			optionGroup.add(options[index]);
		}
		timer = new JLabel("TIMER");  // represents the countdown shown on the window
		timer.setBounds(250, 250, 100, 20);
		clock = new TimerCode(30);  // represents clocked task that should run after X seconds
		Timer t = new Timer();  // event generator
		t.schedule(clock, 0, 1000); // clock is called every second
		window.add(timer);
		
		
		score = new JLabel("SCORE"); // represents the score
		score.setBounds(50, 250, 100, 20);
		window.add(score);
		scoreNumber = 0;
		scoreText = new JLabel("0");
		scoreText.setBounds(100, 250, 100, 20);
		scoreText.setText(scoreNumber + "");
		window.add(scoreText);

		poll = new JButton("Poll");  // button that use clicks/ like a buzzer
		poll.setBounds(10, 300, 100, 20);
		poll.addActionListener(this);
		window.add(poll);
//		poll.addActionListener(new ActionListener() { // calls actionPerformed of this class
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                client.sendUsername(); // When the button is clicked, send username over UDP
//            }
//        });
		window.add(poll);
		
		submit = new JButton("Submit");  // button to submit their answer
		submit.setBounds(200, 300, 100, 20);
		submit.addActionListener(this);  // calls actionPerformed of this class
		window.add(submit);
		
		
		window.setSize(400,400);
		window.setBounds(50, 50, 400, 400);
		window.setLayout(null);
		window.setLocationRelativeTo(null);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
	}
	// this method is called when you check/uncheck any radio button
	// this method is called when you press either of the buttons- submit/poll
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//System.out.println("You clicked " + e.getActionCommand());

		// input refers to the radio button you selected or button you clicked
		String input = e.getActionCommand();  
		if (input == "Enter"){
			window.setVisible(true);
			username.setVisible(false);
		}


		if (input.equalsIgnoreCase(option[0]))
		{
			guess = 1;
		} else if (input.equalsIgnoreCase(option[1]))
		{
			guess = 2;
		} else if (input.equalsIgnoreCase(option[2]))
		{
			guess = 3;
		} else if (input.equalsIgnoreCase(option[3]))
		{
			guess = 4;
		} else if (input.equalsIgnoreCase("Poll"))
		{
			client.sendUsername();
		} else if (input.equalsIgnoreCase("Submit"))
		{
			client.submitButton(guess, true);
		} else if (input.equalsIgnoreCase("Enter"))
		{

		} else
		{
			System.out.println("Incorrect Option");
		}


//		switch(input)
//		{
//			case "Option 1":	// Your code here
//				//clientHandler.setQuestionProgress(GameState.ANSWERING);
//				guess = 1;
//				//client.submitButton(guess, false);
//								break;
//			case "Option 2":	// Your code here
//				//clientHandler.setQuestionProgress(GameState.ANSWERING);
//				guess = 2;
//				//client.submitButton(guess, false);
//								break;
//			case "Option 3":	// Your code here
//				//clientHandler.setQuestionProgress(GameState.ANSWERING);
//				guess = 3;
//				//client.submitButton(guess, false);
//								break;
//			case "Option 4":	// Your code here
//				//clientHandler.setQuestionProgress(GameState.ANSWERING);
//				guess = 4;
//				//client.submitButton(guess, false);
//								break;
//			case "Poll":		// Your code here
//				client.sendUsername();
//								break;
//			case "Submit":		// Your code here
//				//clientHandler.setQuestionProgress(GameState.SENDING);
//				client.submitButton(guess, true);
//								break;
//			case "Enter":
//								break;
//			default:
//								System.out.println("Incorrect Option");
//		}




		// test code below to demo enable/disable components
		/*// DELETE THE CODE BELOW FROM HERE***
		if(poll.isEnabled())
		{
			poll.setEnabled(false);
			submit.setEnabled(true);
		}
		else
		{
			poll.setEnabled(true);
			submit.setEnabled(false);
		}
		
		// question.setText("Q2. This is another test problem " + random.nextInt());
		
		// you can also enable disable radio buttons
		options[random.nextInt(4)].setEnabled(false);
		options[random.nextInt(4)].setEnabled(true);
		// TILL HERE ***
		*/
		
	}
	
	// this class is responsible for running the timer on the window
	public class TimerCode extends TimerTask
	{
		private int duration;  // write setters and getters as you need
		public TimerCode(int duration)
		{
			this.duration = duration;
		}
		@Override
		public void run()
		{

			if(duration < 0)
			{
				timer.setText("Timer expired");
				poll.setEnabled(false);
				window.repaint();
				this.cancel();  // cancel the timed task
				return;
				// you can enable/disable your buttons for poll/submit here as needed
			}
			
			if(duration < 6)
				timer.setForeground(Color.red);
			else
				timer.setForeground(Color.black);
			
			timer.setText(duration+"");
			duration--;
			window.repaint();
		}
	}

	public void updateQuestionText(String nextQuestion, String option1, String option2, String option3, String option4) {
        question.setText(nextQuestion);
		options[0].setText(option1);
		options[1].setText(option2);
		options[2].setText(option3);
		options[3].setText(option4);

		option[0] = option1;
		option[1] = option2;
		option[2] = option3;
		option[3] = option4;


	}
}