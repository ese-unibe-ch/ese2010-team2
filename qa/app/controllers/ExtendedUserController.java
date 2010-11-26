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
 * This Controller manages some extended actions about users
 * which need the Security Annotation
 */
@With(Secure.class)
public class ExtendedUserController extends Controller {

	private static DbManager manager = DbManager.getInstance();
	public static void deleteUser(String uname){
		manager.deleteUser(uname);
		redirect("/editUserGroup");
	}

	public static void showUsers() {
		if (manager.getUsers().isEmpty()) {
			String message = "no users";
			render(message);
		} else {
			ArrayList<User> users = manager.getUsers();
			render(users);
		}
	}
	
	/**
	 * Shows all notifications of the current user.
	 */
	public static void showNotifications() {
		User user = manager.getUserByName(session.get("username"));
		ArrayList<Notification> notifications = (ArrayList<Notification>) user
				.getAllNotifications().clone();
		user.clearAllNotifications();
		render(notifications);
	}

	public static void showUserLog(String uname) {
		ArrayList<String> userLog = manager.getUserLog(uname);
		if (userLog.size() == 1) {
			String message = "no activities so far.";
			render(message);
		}
		render(userLog);
	}
	
	public static void editUserGroup(String uname, String group) {
		ArrayList<User> users = manager.getUsers();
		User user = manager.getUserByName(uname);
		UserGroups ugroup;

		if (manager.getUserByName(session.get("username")).isAdmin()) {
			if (uname == null && group == null)
				render(users);
			else {
				if (group.equals("admin")) {
					ugroup = UserGroups.admin;
					user.setGroup(ugroup);
				}
				if (group.equals("moderator")) {
					ugroup = UserGroups.moderator;
					user.setGroup(ugroup);
				}
				if (group.equals("user")) {
					ugroup = UserGroups.user;
					user.setGroup(ugroup);
				}
				String message = "Settings changed.";
				render(users, message);
			}
		} else {
			redirect("/");
		}
	}
}
