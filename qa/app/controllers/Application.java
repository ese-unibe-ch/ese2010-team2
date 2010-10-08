package controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.mchange.util.AssertException;

import models.*;

import play.data.validation.Required;
import play.mvc.*;

public class Application extends Controller {

	static Calendar calendar = Calendar.getInstance();

	private static UserQuestionAnswerManager manager = UserQuestionAnswerManager
			.getInstance();

	public static void index() {
		if (manager.getQuestions().isEmpty()) {
			String message = "no questions";
			render(message);
		} else {
			ArrayList<Question> questions = manager.getQuestionsSortedByScore();
			render(questions);
		}
	}

	public static void showRegister(String message, String name,
			String password, String password2, String email) {
		render(message, name, password, password2, email);
	}

	public static void register(String name, String password, String password2,
			String email) {
		if (name.equals(""))
			Application.showRegister("Please insert a name!", name, password,
					password2, email);
		else if (manager.checkUserNameIsOccupied(name))
			Application.showRegister("Sorry, this user already exists", "",
					password, password2, email);
		else if (email.equals("") || !email.contains("@")
				|| !email.contains("."))
			Application.showRegister("Please check your email!", name,
					password, password2, email);
		else if (password.equals("") || !password.endsWith(password2))
			Application.showRegister("Please check your password!", name,
					password, password2, email);
		else {
			@SuppressWarnings("unused")
			User user = new User(name, email, password);
			redirect("/");
		}
	}

	public static void showAnswers(String id) {
		int intId = Integer.parseInt(id);
		ArrayList<Answer> answers = manager.getAnswersSortedByScore(intId);
		Question question = manager.getQuestionById(intId);
		if (answers.size() == 0) {
			String message = "no answers";
			render(message, question);
		} else {
			render(answers, question);
		}
	}

	public static void showRecentQuestionsByDate() {
		// "recent" shall mean 5 days
		final Timestamp period = new java.sql.Timestamp(0, 0, 5, 0, 0, 0, 0);
		Timestamp oldest = new Timestamp(calendar.getTime().getTime()
				- period.getTime());

		ArrayList<Question> recentQuestions = new ArrayList<Question>();

		for (Question q : manager.getQuestions()) {
			if (q.getDate().compareTo(oldest) >= 0)
				recentQuestions.add(q);

		}
		if(recentQuestions.size()==0){
			String message= "recently no questions asked";
			render(message);
		}
		else
			render(recentQuestions);

	}

}