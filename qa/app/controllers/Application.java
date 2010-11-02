package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import models.Answer;
import models.DbManager;
import models.Post;
import models.Question;
import models.Search;
import models.SearchResult;
import models.User;
import models.algorithms.SearchResultAssembler;
import models.algorithms.SearchResultSorter;

import org.apache.commons.io.IOUtils;

import play.Play;
import play.mvc.Controller;
import annotations.Unused;

public class Application extends Controller {

	static Calendar calendar = Calendar.getInstance();

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

	public static void showRegister(String message, String name,
			String password, String password2, String email) {
		render(message, name, password, password2, email);
	}

	public static void showState() {
		int userCount = manager.countOfUsers();
		int questionCount = manager.countOfQuestions();
		int answerCount = manager.countOfAnswers();
		int commentCount = manager.countOfComments();
		int tagCount = manager.countOfTags();
		render(userCount, questionCount, answerCount, commentCount, tagCount);
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

	public static void showAnswers(String id, String newAnswer,
			String newMessage) {
		int intId = Integer.parseInt(id);
		ArrayList<Answer> answers = manager.getAnswersSortedByScore(intId);
		Post question = manager.getQuestionById(intId);
		if (answers.size() == 0) {
			String message = new String();
			;
			if (newMessage != null)
				message = newMessage;
			render(message, question, newAnswer);
		} else {
			String message = newMessage;
			render(answers, question, newAnswer, message);
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

	/**
	 * 
	 * @param qid
	 * @param aid
	 */
	public static void setBestAnswer(int qid, int aid) {
		Question q = manager.getQuestionById(qid);
		Answer a = manager.getAnswerById(aid);

		q.setBestAnswer(a);

		showAnswers(Integer.toString(qid), "", "");
	}

	/** renders the current user profile */
	public static void showUserProfile(String message) {
		ArrayList<User> currentUser = new ArrayList<User>();
		currentUser.add(manager.getUserByName(session.get("username")));
		render(message, currentUser);
	}

	/** renders user profile to admin */
	public static void showAdminUserProfile(String message) {
		ArrayList<User> informationOwner = new ArrayList<User>();
		informationOwner.add(manager.getUserByName(message));
		render(message, informationOwner);
	}

	/** shows a specific User */
	public static void showUser(String userName) {
		User profileOwner = manager.getUserByName(userName);

		ArrayList<Integer> reputations = manager.getReputations(profileOwner,
				30);
		ArrayList<Question> userQuestions = manager
				.getQuestionsByUserIdSortedByDate(profileOwner.getId());
		ArrayList<Answer> userAnswers = manager
				.getAnswersByUserIdSortedByDate(profileOwner.getId());
		ArrayList<String> userLog = manager.getUserLog(userName);
		if (manager.getUserByName(session.get("username")) != null) {
			Boolean isAdmin = manager.getUserByName(session.get("username"))
					.isAdmin();
			render(profileOwner, reputations, userQuestions, userAnswers,
					userLog, isAdmin);
		} else
			render(profileOwner, reputations, userQuestions, userAnswers,
					userLog);
	}

	/**
	 * Saves changes in user Profile
	 * 
	 * @throws Throwable
	 */
	public static void editUserProfile(String name, String birthdate,
			String email, String phone, String password, String password2,
			String street, String town, String hobbies, String moto,
			String background, String quote) throws Throwable {

		// This block will be used when changing the user name and one of the
		// following attributes e.g. email
		// email must be changed for the new user name not the old, so you have
		// to determine if the user name was changed.
		String username;
		if (name.equals("")) {
			username = session.get("username");
		} else {
			username = name;
		}

		// Checks if user name is already occupied
		if (!name.equals("")) {
			if (!manager.checkUserNameIsOccupied(name)) {
				manager.getUserByName(session.get("username")).setName(name);
			} else {
				Application.showUserProfile("Sorry, this user already exists!");
			}

		}
		// Checks if the email has a @ and a dot
		if (!email.equals("")) {
			if (email.contains("@") || email.contains(".")) {
				manager.getUserByName(username).setEmail(email);
			} else {
				Application
						.showUserProfile("Please re-check your email address!");
			}
		}
		// Checks if two similar password were typed in.
		if (!password.equals("")) {
			if (password.equals(password2)) {
				manager.getUserByName(username).setPassword(password);
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

		if (name.equals("")) {
			redirect("/showUser/" + session.get("username"));
		} else {
			Secure.logout();
		}
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
	public static void search(String text) {
		// When site is first time loaded
		if (text == null) {
			render();
		}
		// If no query is typed in
		if (text != null && text.equals("")) {
			String message = "Nothing to search";
			render(message);
		}
		// If a query is typed in
		if (!text.equals("")) {
			Search search = new Search(text);

			SearchResultAssembler assembler = new SearchResultAssembler(
					search.getAnswerContentResults(),
					search.getCommentResults(), search.getMergedQuestions(),
					search.getQuery());

			SearchResultSorter sorter = new SearchResultSorter(
					assembler.getSearchResults(), search.getQuery());

			ArrayList<SearchResult> results = sorter.getSearchResults();

			// If query has no results
			if (results.size() == 0) {
				String message = "No Results";
				render(message);
			} else {
				render(results);
			}
		}
		/*
		 * Old Code --> Do not delete will maybe usefull for Advanced Search
		 * 
		 * search.searchQuestionTags(); search.searchQuestionContent();
		 * search.searchAnswerContent(); search.searchComments();
		 * search.mergeQuestionTagWithQuestionContentList();
		 * 
		 * 
		 * ArrayList<Question> questionContentResults = search
		 * .getQuestionContentResults(); ArrayList<Question> questionTagResults
		 * = search .getQuestionTagsResults(); ArrayList<Answer>
		 * answerContentResults = search .getAnswerContentResults();
		 * ArrayList<Comment> commentResults = search.getCommentResults();
		 */
	}

	/*
	 * 
	 * // TODO: Übergabe der Werte aus radio check boxes & speichern dieser. /*
	 * public static void editUserGroup(User user, String group) { UserGroups
	 * ugroup; if (group == null) { String message = "Group is null";
	 * render(message); } else { if (group.equals("admin")) ugroup =
	 * UserGroups.admin; else { if (group.equals("moderator")) ugroup =
	 * UserGroups.moderator; else { if (group.equals("user")) ugroup =
	 * UserGroups.user; else throw new NoSuchElementException(); } }
	 * user.setGroup(ugroup); } // manager.getUsers().get // .setGroup(ugroup);
	 * }
	 */

	public static void showVersionHistory(String type, String id) {
		Post post;
		int intId = Integer.parseInt(id);
		if (type.equals("questions") || type.equals("question")) {
			post = manager.getQuestionById(intId);
			type = "question";
		} else {
			post = manager.getAnswerById(intId);
		}
		ArrayList<Post> history = post.getOldVersions();
		render(type, post, history);
	}

}
