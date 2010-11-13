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
 * This Controller manages some basic actions about User which do not
 * include the Security Annotation.
 *
 */
public class UserController extends Controller {

	private static Calendar calendar = Calendar.getInstance();
	private static DbManager manager = DbManager.getInstance();
	
	/**
	 * Check wehen a new User is registered if the User's Input is correct
	 * according to the rules mentioned in the method.
	 */
	public static void register(String name, String password, String password2,
			String email) throws Throwable {
		if (name.equals(""))
			UserController.showRegister("Please insert a name!", name, password,
					password2, email);
		else if (manager.checkUserNameIsOccupied(name))
			UserController.showRegister("Sorry, this user already exists", "",
					password, password2, email);
		else if (email.equals("") || !email.contains("@")
				|| !email.contains("."))
			UserController.showRegister("Please check your email!", name,
					password, password2, email);
		else if (password.equals("") || !password.endsWith(password2))
			UserController.showRegister("Please check your password!", name,
					password, password2, email);
		else {
			@SuppressWarnings("unused")
			User user = new User(name, email, password);
			Secure.logout();
		}
	}

	/**
	 * Renders the registration form with the proper error message to the user
	 * due to is wrong input.
	 */
	public static void showRegister(String message, String name,
			String password, String password2, String email) {
		render(message, name, password, password2, email);
	}

	/** renders the current user profile */
	public static void showUserProfile(String message) {
		ArrayList<User> currentUser = new ArrayList<User>();
		currentUser.add(manager.getUserByName(session.get("username")));
		render(message, currentUser);
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
				UserController.showUserProfile("Sorry, this user already exists!");
			}

		}
		// Checks if the email has a @ and a dot
		if (!email.equals("")) {
			if (email.contains("@") || email.contains(".")) {
				manager.getUserByName(username).setEmail(email);
			} else {
				UserController
						.showUserProfile("Please re-check your email address!");
			}
		}
		// Checks if two similar password were typed in.
		if (!password.equals("")) {
			if (password.equals(password2)) {
				manager.getUserByName(username).setPassword(password);
			} else {
				UserController.showUserProfile("Passwords are not identical!");
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

}
