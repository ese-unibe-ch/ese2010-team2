package controllers;

import java.io.File;
import java.util.ArrayList;

import models.Answer;
import models.DbManager;
import models.Question;
import models.User;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.Controller;

/**
 * This Controller manages some basic actions about User which do not include
 * the Security Annotation.
 * 
 */
public class UserController extends Controller {

	private static DbManager manager = DbManager.getInstance();

	/**
	 * Check when a new User is registered if the User's Input is correct
	 * according to the rules mentioned in the method.
	 */
	public static void register(String name, String password, String password2,
			String email, String code, String randomID) throws Throwable {

		validation.equals(code, Cache.get(randomID));
		if (name.equals("")) {
			flash.error("Please insert a name!");
			UserController.showRegister(name, password, password2, email);
		} else if (manager.checkUserNameIsOccupied(name)) {
			flash.error("Sorry, this user already exists");
			UserController.showRegister("", password, password2, email);
		} else if (email.equals("") || !email.contains("@")
				|| !email.contains(".")) {
			flash.error("Please enter a valid email address!");
			UserController.showRegister(name, password, password2, email);
		} else if (password.equals("") || !password.equals(password2)) {
			flash.error("Passwords do not match!");
			UserController.showRegister(name, password, password2, email);
		} else if (DbManager.getInstance().isEmailRegistered(email)) {
			flash.error("This eMail-address is already registered.");
			UserController.showRegister(name, password, password2, email);
		} else if (validation.hasErrors()) {
			flash.error("Please check the CAPTCHA code");
			UserController.showRegister(name, password, password2, email);
		} else {
			User user = new User(name, email, password);
			String chosenName = user.getName();
			user.setFlag(manager.getUserByName(name));
			Mails.confirm(user, chosenName);
			flash.success("Please check your email");
			Cache.delete(randomID);
			Secure.redirectToOriginalURL();
		}
	}

	/** unflags user once link in confirmation mail has been clicked **/
	public static void finishRegistration(String flaggedUserName) {
		if (manager.checkUserNameIsOccupied(flaggedUserName)) {
			User user = manager.getUserByName(flaggedUserName);
			user.unFlag(user);
			session.put("uid", user.getId());
			session.put("username", user.getName());
			render(user);
		}
	}

	/**
	 * Renders the registration form with the proper error message to the user
	 * due to is wrong input.
	 */
	public static void showRegister(String name, String password,
			String password2, String email) {
		String randomID = Codec.UUID();
		render(name, password, password2, email, randomID);
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
			String email, String phone, String oldPassword, String password,
			String password2, String street, String town, String hobbies,
			String moto, String background, String quote) throws Throwable {

		// This block will be used when changing the user name and one of the
		// following attributes e.g. email
		// email must be changed for the new user name not the old, so you have
		// to determine if the user name was changed.
		String username;
		@SuppressWarnings("unused")
		boolean passwordChanged = false;
		// When username doesn't change
		if (name.equals("")) {
			username = session.get("username");
		} else {
			username = name;
		}

		// Checks if user name is already occupied
		if (!name.equals("")) {
			if (!manager.checkUserNameIsOccupied(username)) {
				manager.getUserByName(session.get("username"))
						.setName(username);
			} else if (!username.equals(session.get("username"))
					&& manager.checkUserNameIsOccupied(username)) {
				flash.error("Sorry, this user already exists!");
				UserController.showUserProfile("");
			}

		}

		// Checks if the email has a @ and a dot
		if (!email.equals("")) {
			if (email.contains("@") || email.contains(".")) {
				manager.getUserByName(username).setEmail(email);
			} else {
				flash.error("Please re-check your email address!");
				UserController.showUserProfile("");
			}
		}

		// Only check password if not all 3 fields are empty
		if (!(password.equals("") && password2.equals("") && oldPassword
				.equals(""))) {
			// Only when old password is correct new password will be changed.
			if (manager.getUserByName(username).getPassword()
					.equals(oldPassword)) {
				// Checks if the new password is similar in both fields
				if (!password.equals("") && !password2.equals("")) {
					if (password.equals(password2)) {
						manager.getUserByName(username).setPassword(password);
						passwordChanged = true;
					} else {
						flash.error("Passwords do not match.");
						UserController.showUserProfile("");
					}
				}
			} else if (oldPassword.equals("")) {
				flash.error("No old Password typed in!");
				UserController.showUserProfile("");
			} else {
				flash.error("Old Password incorrect!");
				UserController.showUserProfile("");
			}
			if (password.equals("") && password2.equals("")) {
				flash.error("No new Password typed in!");
				UserController.showUserProfile("");
			}
		}

		// Checks if two similar password were typed in.
		if (password.equals("") && !password2.equals("")) {
			flash.error("Passwords do not match!");
			UserController.showUserProfile("");
		}

		// Checks if two similar password were typed in.
		if (!password.equals("") && password2.equals("")) {
			flash.error("Passwords do not match!");
			UserController.showUserProfile("");
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

		// If username changed, put new username as session name.
		if (!username.equals(session.get("username"))) {
			session.put("username", username);
			redirect("/showUser/" + session.get("username"));
		} else {
			redirect("/showUser/" + session.get("username"));
		}
	}

	/**
	 * Update or set user's avatar.
	 * 
	 * @param avatar
	 * @throws Exception
	 */
	public static void setAvatar(File avatar) throws Exception {

		if (avatar != null) {
			User user = manager.getUserByName(session.get("username"));
			user.setAvatar(avatar);
		}

		redirect("/showUserProfile");
	}

}
