package controllers;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import models.Answer;
import models.DbManager;
import models.Post;
import models.Question;
import models.SearchManager;
import models.SearchResult;
import models.User;

import org.apache.commons.io.IOUtils;

import play.Play;
import play.cache.Cache;
import play.mvc.Controller;
import annotations.Unused;

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
			Cache.set("questions", questions);
			render(questions, userName, isChanged);
		}
	}

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

		boolean isQuestion = menu.equals("similarQuestion");
		boolean isUser = menu.equals("similarUser");
		User currentUser = manager.getUserByName(session.get("username"));

		// If no query is typed in
		if (text != null && text.equals("")) {
			String message = "Nothing to search";
			render(message);
		}
		// If a query is typed in
		if (!text.equals("")) {
			SearchManager searchManager = new SearchManager(text);
			ArrayList<SearchResult> results = searchManager.getSearchResults();
			if (isUser) {
				// returns list of users without duplicates or user logged into
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
				render(message);
			} else {
				render(results, isQuestion, isUser);
			}
		}
	}
}
