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

	public static void showEditAnswerForm(int answerId, int qid,
			String newContent, String message) {
		Answer answer = manager.getAnswerById(answerId);
		render(answer, answerId, qid, newContent, message);
	}

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

	/**
	 * Sets the content of the answer to the new value
	 * 
	 * @param answerId
	 * @param newContent
	 */
	public static void editAnswer(int answerId, int qid, String newContent,
			User user) {
		if (newContent.equals("") || newContent.equals(" ")) {
			String message = "Your answer is empty.";
			showEditAnswerForm(answerId, qid, newContent, message);
		}
		manager.getAnswerById(answerId).addVersion(newContent,
				session.get("username"));
		redirect("/question/" + qid + "/answers/");
	}

	public static void showQuestionCommentForm(String qid) {
		render(qid);
	}

	public static void showAnswerCommentForm(int answerId, String qid) {
		render(answerId, qid);
	}
	
	public static void deleteAnswer(int aid, int qid){
		Answer answer = manager.getAnswerById(aid);
		manager.deleteAnswer(answer);
		redirect("/question/" + qid + "/answers/");
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

	public static void addAnswer(String qid, String newAnswer) {
		int intId = Integer.parseInt(qid);
		User user = manager.getUserByName(session.get("username"));
		if (newAnswer.equals("") || newAnswer.equals(" ")) {
			String message = "Your answer is empty!";
			DisplayQuestionController.showAnswers(qid, newAnswer, message);
		} else {
			@SuppressWarnings("unused")
			Answer answer = new Answer(true, newAnswer, user, manager
					.getQuestionById(intId));
			redirect("/question/" + qid + "/answers/");
		}
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

	public static void voteAnswerUp(int qid, int aid) {
		voteAnswer(qid, aid, 1);
	}

	public static void voteAnswerDown(int qid, int aid) {
		voteAnswer(qid, aid, -1);
	}

	public static void voteAnswer(int qid, int aid, int vote) {
		Answer answer = manager.getAnswerById(aid);
		User user = manager.getUserByName(session.get("username"));
		user.addvotedAnswer(answer);
		Answer useranswer = user.getVotedAnswer(answer);
		if(useranswer.getVoteSetTime()==null){
			useranswer.setvoteSetTime();
		}
		if(useranswer.voteChangeable()==false){
			String message = "You already voted for this post!";
			render(message, qid);
		}
		if (vote==1&&useranswer.getcurrentVote()!=1) {
			useranswer.setcurrentVote(vote);
			answer.setTempVote(1);
			answer.setcurrentVote(vote);
			String message = "Your current vote is +1";
			answer.userVotedForPost(user);
			render(message, qid);
		}
		if (vote==-1&&useranswer.getcurrentVote()!=-1){
			useranswer.setcurrentVote(vote);
			answer.setTempVote(-1);
			answer.setcurrentVote(vote);
			String message = "Your current vote is -1";
			answer.userVotedForPost(user);
			render(message, qid);
		}
		redirect("/question/" + qid + "/answers/");
	}

	public static void showAnswerForm(String qid) {
		int intId = Integer.parseInt(qid);
		ArrayList<Answer> answers = manager.getAllAnswersByQuestionId(intId);
		Post question = manager.getQuestionById(intId);
		render(answers, question);
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

	public static void restoreAnswer(int actualId, String oldContent) {
		String uname = session.get("username");
		Answer actualAnswer = manager.getAnswerById(actualId);
		actualAnswer.addVersion(oldContent, uname);
		redirect("/answer/" + actualAnswer.getId() + "/history");
	}
}
