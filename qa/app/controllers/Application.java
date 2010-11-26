package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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

	private static Calendar calendar = Calendar.getInstance();
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
			render(questions, userName, isChanged);
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
			String message = "Nothing to search";
			render(message);
		}
		// If a query is typed in differentiate between searchtypes
		if (!text.equals("")) {
			SearchManager searchManager = new SearchManager(text);

			ArrayList<SearchResult> results = searchManager.getSearchResults();
			if (isUser) {
				// returns list of users without duplicates or user logged
				// into
				// session
				ArrayList<SearchResult> newList = new ArrayList();
				Set set = new HashSet();
				for (SearchResult result : results) {
					if (set.add(result.getOwner())
							&& !result.getOwner().equals(currentUser))
						newList.add(result);
				}
				results.clear();
				results.addAll(newList);
			}

			// If query has no results
			if (results.size() == 0) {
				String message = "No Results";
				render(message, menu, text);
			} else {
				render(results, isQuestion, isUser, menu, text);
			}
		}
	}
	
	/**
	 * Creates a captcha given a certain id. 
	 * @param id - the id of the captcha.
	 */
	public static void captcha(String id) {
	    Images.Captcha captcha = Images.captcha();
	    String code = captcha.getText("#E4EAFD");
	    Cache.set(id, code, "30mn");
	    renderBinary(captcha);
	}
}
