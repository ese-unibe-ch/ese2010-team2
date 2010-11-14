package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import models.Answer;
import models.DbManager;
import models.Post;
import models.Question;
import play.mvc.Controller;
import annotations.Unused;

/**
 * This Controller manages the view of questions
 */
public class DisplayQuestionController extends Controller {

	private static Calendar calendar = Calendar.getInstance();
	private static DbManager manager = DbManager.getInstance();
	
	public static void questions() {
		ArrayList<Question> questions = manager.getQuestionsSortedByScore();
		render(questions);
	}
	
	public static void showAnswers(String id, String newAnswer,
			String newMessage) {
		int intId = Integer.parseInt(id);
		ArrayList<Answer> answers = manager.getAnswersSortedByScore(intId);
		Question question = manager.getQuestionById(intId);

		ArrayList<Post> similar = new ArrayList<Post>();
		similar.addAll(question.similarQuestions());

		if (answers.size() == 0) {
			String message = new String();
			if (newMessage != null)
				message = newMessage;
			render(message, question, newAnswer, similar);
		} else {
			String message = newMessage;
			render(answers, question, newAnswer, message, similar);
		}
	}
	
	@Unused
	public static void showRecentQuestionsByDate() {
		// "recent" shall mean 5 days
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -5);
		Date oldest = cal.getTime();

		ArrayList<Question> recentQuestionsByDate = new ArrayList<Question>();

		for (Question q : manager.getQuestions()) {
			if (q.getDate().compareTo(oldest) >= 0)
				recentQuestionsByDate.add(q);

		}
		if (recentQuestionsByDate.size() == 0) {
			String message = "recently no questions asked";
			render(message);
		} else
			render(recentQuestionsByDate);

	}

	/**
	 * Renders the preferred amount of newest questions
	 */
	public static void showRecentQuestionsByNumber() {
		final int number = 25; // The number of questions rendered
		ArrayList<Question> recentQuestionsByNumber = manager
				.getRecentQuestionsByNumber(number);

		render(recentQuestionsByNumber);
	}

}
