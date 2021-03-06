package controllers;

import java.util.ArrayList;

import models.DbManager;
import models.Question;
import models.SearchManager;
import models.SearchResult;
import models.User;
import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;

/**
 * This Controller manages some general actions.
 */
public class Application extends Controller {

	private static DbManager manager = DbManager.getInstance();

	public static void index() {
		String userName = session.get("username");
		int score = 0;

		boolean isChanged = true;
		if (userName != null) {
			User user = manager.getUserByName(userName);
			isChanged = user.isChanged();
		}
		if (manager.getQuestions().isEmpty()) {
			String message = "no questions";
			render(message, userName, score);
		} else {
			ArrayList<Question> questions = manager.getQuestionsSortedByScore();
			render(questions, userName, isChanged/* , isNotUser */);
		}
	}

	/**
	 * Renders some general informations about the application
	 */
	public static void showState() {
		int userCount = manager.countOfUsers();
		int questionCount = manager.countOfQuestions();
		int answerCount = manager.countOfAnswers();
		int commentCount = manager.countOfComments();
		int tagCount = manager.countOfTags();
		render(userCount, questionCount, answerCount, commentCount, tagCount);
	}

	/** Renders Search Results */
	public static void search(String text, String menu) {
		// When site is first time loaded
		if (text == null) {
			render();
		}

		boolean isQuestion = menu.equals("search");
		boolean isUser = menu.equals("similarUser");
		User currentUser = manager.getUserByName(session.get("username"));

		// If no query is typed in
		if (text != null && text.equals("")) {
			flash.error("Search query was empty.");
			render();
		}
		// If a query is typed in differentiate between searchtypes
		if (!text.equals("")) {
			SearchManager searchManager = new SearchManager(text);
			ArrayList<SearchResult> results = searchManager.getSearchResults();
			ArrayList<User> userResult = new ArrayList<User>();
			if (isUser) {
				results.clear();
				for (User user : manager.getUsers()) {
					if (user.getName().toLowerCase()
							.contains(text.toLowerCase())
							&& !user.equals(currentUser))
						userResult.add(user);
				}
			}

			// If query has no results
			if (results.size() == 0 && userResult.size() == 0) {
				String message = "No Results";
				render(message, menu, text);
			} else {
				render(results, userResult, isQuestion, isUser, menu, text);
			}
		}
	}

	/**
	 * Creates a captcha given a certain id.
	 * 
	 * @param id
	 *            - the id of the captcha.
	 */
	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#AA00A2");
		Cache.set(id, code, "30mn");
		renderBinary(captcha);
	}
}
