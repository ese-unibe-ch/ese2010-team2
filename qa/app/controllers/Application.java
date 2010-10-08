package controllers;

import java.util.ArrayList;

import com.mchange.util.AssertException;

import models.Answer;
import models.Question;
import models.User;
import models.UserQuestionAnswerManager;

import play.data.validation.Required;
import play.mvc.*;

public class Application extends Controller {

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

}