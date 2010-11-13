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
 * The Class Admin controls access on the database such as adding Answers or
 * Questions or voting and also managing the users.
 */
@With(Secure.class)
public class Admin extends Controller {

	private static DbManager manager = DbManager.getInstance();
	private static Calendar calendar = Calendar.getInstance();

	public static void showNotifications() {
		User user = manager.getUserByName(session.get("username"));
		ArrayList<Notification> notifications = (ArrayList<Notification>) user
				.getAllNotifications().clone();
		user.clearAllNotifications();
		render(notifications);
	}

	public static void showAdminPage() {
		String uname = session.get("username");
		if (!manager.getUserByName(uname).isAdmin())
			redirect("/");
		else
			render(uname);
	}
}
