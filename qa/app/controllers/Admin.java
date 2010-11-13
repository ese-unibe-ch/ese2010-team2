package controllers;

import java.util.ArrayList;
import java.util.Calendar;

import models.Answer;
import models.Comment;
import models.DbManager;
import models.Notification;
import models.Post;
import models.Question;
import models.User;
import models.UserGroups;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This Controller manages administration
 */
@With(Secure.class)
public class Admin extends Controller {

	private static DbManager manager = DbManager.getInstance();
	private static Calendar calendar = Calendar.getInstance();
	
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
				Admin.showAdminUserProfile("Sorry, this user already exists!");
			}

		}
		// Checks if the email has a @ and a dot
		if (!email.equals("")) {
			if (email.contains("@") || email.contains(".")) {
				manager.getUserByName(username).setEmail(email);
			} else {
				Admin.showAdminUserProfile("Please re-check your email address!");
			}
		}
		// Checks if two similar password were typed in.
		if (!password.equals("")) {
			if (password.equals(password2)) {
				manager.getUserByName(username).setPassword(password);
			} else {
				Admin.showAdminUserProfile("Passwords are not identical!");
			}
		}

		redirect("/editUserGroup");
	}

	public static void showAdminPage() {
		String uname = session.get("username");
		if (!manager.getUserByName(uname).isAdmin())
			redirect("/");
		else
			render(uname);
	}
}
