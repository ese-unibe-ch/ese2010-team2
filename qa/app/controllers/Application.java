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

	/** renders user profile to admin */
	public static void showAdminUserProfile(String message) {
		ArrayList<User> informationOwner = new ArrayList<User>();
		informationOwner.add(manager.getUserByName(message));
		render(message, informationOwner);
	}

	/** Admin form for saving changes in user profiles */
	public static void editInformation(String owner, String name, String email,
			String password, String password2) throws Throwable {

		String username;
		if (name.equals("")) {
			username = owner;
		} else {
			username = name;
		}

		// Checks if user name is already occupied
		if (!name.equals("") && !name.equals(" ")) {
			if (!manager.checkUserNameIsOccupied(name)) {
				manager.getUserByName(owner).setName(name);
			} else {
				Application
						.showAdminUserProfile("Sorry, this user already exists!");
			}

		}
		// Checks if the email has a @ and a dot
		if (!email.equals("")) {
			if (email.contains("@") || email.contains(".")) {
				manager.getUserByName(username).setEmail(email);
			} else {
				Application
						.showAdminUserProfile("Please re-check your email address!");
			}
		}
		// Checks if two similar password were typed in.
		if (!password.equals("")) {
			if (password.equals(password2)) {
				manager.getUserByName(username).setPassword(password);
			} else {
				Application
						.showAdminUserProfile("Passwords are not identical!");
			}
		}

		redirect("/editUserGroup");
	}

	/**
	 * Update or set user's avatar.
	 * 
	 * Copy image file from play tmp directory to our avatar directory, delete
	 * old avatar if exists, update filename.
	 * 
	 * @param title
	 * @param avatar
	 * @throws Exception
	 */
	public static void setAvatar(File avatar) throws Exception {

		if (avatar != null) {
			User user = manager.getUserByName(session.get("username"));
			File avatarDir = new File(Play.applicationPath.getAbsolutePath()
					+ "/public/images/avatars");

			if (!avatarDir.exists()) {
				avatarDir.mkdir();
			} else {
				if (user.hasAvatar()) {
					File old = user.getAvatar();
					if (!old.delete())
						throw new IOException("Could not delete old avatar.");
				}
			}

			File newAvatar = new File(avatarDir.getPath() + "/"
					+ avatar.getName());
			user.setAvatar(newAvatar);

			try {
				newAvatar.createNewFile();
				FileInputStream input = new FileInputStream(avatar);
				FileOutputStream output = new FileOutputStream(newAvatar);
				IOUtils.copy(input, output);
				output.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		redirect("/showUserProfile");
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

	public static void showVersionHistory(String type, String id) {
		Post post;
		int intId = Integer.parseInt(id);
		if (type.equals("questions") || type.equals("question")) {
			post = manager.getQuestionById(intId);
			type = "question";
			ArrayList<Post> history = post.getOldVersions();
			render(type, post, history);
		} else {
			post = manager.getAnswerById(intId);
			Post question = manager.getAnswerById(intId).getQuestion();
			ArrayList<Post> history = post.getOldVersions();
			render(type, post, history, question);
		}

	}

}
