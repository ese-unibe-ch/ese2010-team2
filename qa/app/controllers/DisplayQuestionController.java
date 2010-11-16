package controllers;

import java.util.ArrayList;
import java.util.Calendar;

import models.Answer;
import models.DbManager;
import models.Post;
import models.Question;
import play.mvc.Controller;

/**
 * This Controller manages the view of questions
 */
public class DisplayQuestionController extends Controller {

	private static Calendar calendar = Calendar.getInstance();
	private static DbManager manager = DbManager.getInstance();
	
	public static void questions(String mod) {
		ArrayList<Question> questions = null;
		if (mod.equals("recent")) {
			questions = manager.getRecentQuestionsByNumber(25);
			render(questions, mod);
		} else if (mod.equals("all")) {
			questions = manager.getQuestionsSortedByScore();
			render(questions, mod);
		}
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

	/**
	 * Renders the preferred amount of newest questions
	 */
	public static void showRecentQuestionsByNumber() {
		ArrayList<Question> recentQuestionsByNumber = manager
				.getRecentQuestionsByNumber(25);

		render(recentQuestionsByNumber);
	}

}
