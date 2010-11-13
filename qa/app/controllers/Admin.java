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

	
	public static void showEditQuestionCommentForm(int qid, int cid) {
		Comment comment = manager.getCommentById(cid);
		render(qid, cid, comment);
	}
	
	public static void showEditAnswerCommentForm(int qid, int cid) {
		Comment comment = manager.getCommentById(cid);
		render(qid, cid, comment);
	}

	/**
	 * Sets the content of the comment to the new value
	 * 
	 * @param comment
	 * @param newContent
	 */
	public static void editQuestionComment(int qid, int cid,
			String newContent, Comment comment) {
		Comment QComment = manager.getCommentById(cid);
		QComment.setContent(newContent, session.get("Username"));
		redirect("/question/" + qid + "/answers/");
	}
	
	/**
	 * Sets the content of the comment to the new value
	 * 
	 * @param comment
	 * @param newContent
	 */
	public static void editAnswerComment(int qid, int cid,
			String newContent, Comment comment) {
		Comment AComment = manager.getCommentById(cid);
		AComment.setContent(newContent, session.get("Username"));
		redirect("/question/" + qid + "/answers/");
	}

	public static void showQuestionCommentForm(String qid) {
		render(qid);
	}

	public static void showAnswerCommentForm(int answerId, String qid) {
		render(answerId, qid);
	}
	
	public static void deleteComment(int qid, int cid){
		Comment comment = manager.getCommentById(cid);
		manager.deleteComment(comment);
		redirect("/question/" + qid + "/answers/");
	}
	
	public static void deleteUser(String uname){
		manager.deleteUser(uname);
		redirect("/editUserGroup");
	}

	
	public static void addCommentToQuestion(int qid, String newComment) {
		User user = manager.getUserByName(session.get("username"));
		Post question = manager.getQuestionById(qid);
		if (newComment.equals("") || newComment.equals(" ")) {
			String message = "Your comment is empty!";
			render(message);
		} else {
			Comment comment = new Comment(user, question, newComment);
			redirect("/question/" + qid + "/answers/");
		}
	}

	public static void addCommentToAnswer(int answerId, int qid,
			String newComment) {
		User user = manager.getUserByName(session.get("username"));
		Answer answer = manager.getAnswerById(answerId);
		if (newComment.equals("") || newComment.equals(" ")) {
			String message = "Your comment is empty!";
			render(message);
		} else {
			Comment comment = new Comment(user, answer, newComment);
			redirect("/question/" + qid + "/answers/");
		}
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

	public static void showUserLog(String uname) {
		ArrayList<String> userLog = manager.getUserLog(uname);
		if (userLog.size() == 1) {
			String message = "no activities so far.";
			render(message);
		}
		render(userLog);
	}

	public static void showNotifications() {
		User user = manager.getUserByName(session.get("username"));
		ArrayList<Notification> notifications = (ArrayList<Notification>) user
				.getAllNotifications().clone();
		user.clearAllNotifications();
		render(notifications);
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

	public static void showAdminPage() {
		String uname = session.get("username");
		if (!manager.getUserByName(uname).isAdmin())
			redirect("/");
		else
			render(uname);
	}
}
