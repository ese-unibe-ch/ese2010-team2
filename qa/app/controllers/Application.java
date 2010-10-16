package controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import models.Answer;
import models.Question;
import models.Search;
import models.User;
import models.UserQuestionAnswerManager;
import play.mvc.Controller;
import annotations.Unused;

public class Application extends Controller {

	static Calendar calendar = Calendar.getInstance();

	private static UserQuestionAnswerManager manager = UserQuestionAnswerManager
			.getInstance();

	public static void index() {
		String user = session.get("username");
		int score = 0;
		if (user != null)
			score = manager.getUserByName(user).getScore();
		if (manager.getQuestions().isEmpty()) {
			String message = "no questions";
			render(message, user, score);
		} else {
			ArrayList<Question> questions = manager.getQuestionsSortedByScore();
			render(questions, user, score);
		}
	}

	public static void showRegister(String message, String name,
			String password, String password2, String email) {
		render(message, name, password, password2, email);
	}

	public static void register(String name, String password, String password2,
			String email) throws Throwable {
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
			Secure.logout();
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

	@Unused
	public static void showRecentQuestionsByDate() {
		// "recent" shall mean 5 days
		final Timestamp period = new java.sql.Timestamp(0, 0, 5, 0, 0, 0, 0);
		Timestamp oldest = new Timestamp(calendar.getTime().getTime()
				- period.getTime());

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
		ArrayList<Question> recentQuestionsByNumber = manager.getRecentQuestionsByNumber(number); 

		render(recentQuestionsByNumber);
	}

	public static void setBestAnswer(int qid, int aid) {
		Question q = manager.getQuestionById(qid);
		Answer a = manager.getAnswerById(aid);
		
		q.setBestAnswer(a);
		
		showAnswers(Integer.toString(qid));
	}
	
	/**renders the current user profile*/
	public static void showUserProfile(String message) {
		ArrayList<User> currentUser = new ArrayList<User>();
		currentUser.add(manager.getUserByName(session.get("username")));
		render(message, currentUser);
	}
	
	/**Saves changes in user Profile
	 * @throws Throwable */
	public static void editUserProfile(String name, String birthdate, String email, String phone, String password, String password2, String street, String town, String hobbies, String moto, String background, String quote) throws Throwable {
		String username;
		if(name.equals("")){
			username = session.get("username");
		}
		else{
			username = name;
		}
		
		if (!name.equals("")) {
			if (!manager.checkUserNameIsOccupied(name)) {
				manager.getUserByName(session.get("username")).setName(name);
			} else {
				Application.showUserProfile("Sorry, this user already exists!");
			}

		}
		if (!email.equals("")) {
			if (email.contains("@") || email.contains(".")) {
				manager.getUserByName(username).setEmail(email);
			} else {
				Application.showUserProfile("Please re-check your email address!");
			}
		}
		if (!password.equals("")) {
			if (password.equals(password2)) {
				manager.getUserByName(username).setPassword(
						password);
			} else {
				Application.showUserProfile("Passwords are not identical!");
			}
		}
		if (!phone.equals("")) {
			manager.getUserByName(username).setPhone(phone);
		}
		if (!street.equals("")) {
			manager.getUserByName(username).setStreet(street);
		}
		if (!town.equals("")) {
			manager.getUserByName(username).setTown(town);
		}
		if (!birthdate.equals("")) {
			manager.getUserByName(username).setBirthdate(birthdate);
		}
		if (!background.equals("")) {
			manager.getUserByName(username).setBackground(background);
		}
		if (!hobbies.equals("")) {
			manager.getUserByName(username).setHobbies(hobbies);
		}
		if (!moto.equals("")) {
			manager.getUserByName(username).setMoto(moto);
		}
		if (!quote.equals("")) {
			manager.getUserByName(username).setQuote(quote);
		}
		if(name.equals("")){
			redirect("/");
		}
		else{
			Secure.logout();
		}
	}
	
	public static void search(String text) {
		if (text == null) {
			render();
		}
		if (text != null && text.equals("")) {
			String message = "You have not inserted a Search Phrase";
			render(message);
		}
		if (!text.equals("")) {
			Search search = new Search(text);
			search.fullTextOnly();

			ArrayList<Question> questionResults = new ArrayList<Question>();
			ArrayList<Answer> answerResults = new ArrayList<Answer>();

			for (int i = 0; i < search.getQuestionIndexes().size(); i++) {
				int index = search.getQuestionIndexes().get(i);
				questionResults.add(manager.getQuestions().get(index));
			}

			for (int i = 0; i < search.getAnswerIndexes().size(); i++) {
				int index = search.getAnswerIndexes().get(i);
				answerResults.add(manager.getAnswers().get(index));
			}

			render(answerResults, questionResults);
		}
	}

	public static void searchResults(String text) {
		if (text == null) {
			Application.searchResults("Text is null");
			render();
		} else {
			Application.searchResults("text has a value");
			render();
	}
	}
}