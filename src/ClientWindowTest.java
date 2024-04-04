public class ClientWindowTest
{
	public static void main(String[] args)
	{
		ClientWindow window = new ClientWindow();

			if (Client.isChange() == true){
				String[] nextQuestion = Client.getQuestion();
				String question = nextQuestion[0];
				String option1 = nextQuestion[1];
				String option2 = nextQuestion[2];
				String option3 = nextQuestion[3];
				String option4 = nextQuestion[4];
                window.updateQuestionText(question, option1, option2, option3, option4);
				if (Client.isChange() == true){
					Client.setChange(false);
				}
			}
		}
	}
