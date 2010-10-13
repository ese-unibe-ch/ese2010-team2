package controllers;

import java.util.ArrayList;
import java.util.Calendar;

import models.*;
import play.data.validation.Required;
import play.mvc.*;

@With(Secure.class)
public class Admin extends Controller {

	private static UserQuestionAnswerManager manager = UserQuestionAnswerManager
			.getInstance();
	private static Calendar calendar = Calendar.getInstance();

	public static void showQuestionForm() {
		render();
	}

	public static void addQuestion(String newQuestion, String tags) {
		User user = manager.getUserByName(session.get("username"));
		if (newQuestion.equals("") || newQuestion.equals(" ")) {
			String message = "Your question is empty!";
			render(message);
		} else if (manager.checkQuestionDuplication(newQuestion)) {
			String message = "Your question already exists!";
			render(message);
		} else {
			@SuppressWarnings("unused")
			Question question = new Question(newQuestion, user);
			user.addActivity("Asked question <" + newQuestion + ">");
			redirect("/");
		}
	}

	public static void addAnswer(String qid, String newAnswer) {
		int intId = Integer.parseInt(qid);
		User user = manager.getUserByName(session.get("username"));
		if (newAnswer.equals("") || newAnswer.equals(" ")) {
			String message = "Your answer is empty!";
			render(message, qid);
		} else {
			@SuppressWarnings("unused")
			Answer answer = new Answer(newAnswer, user, manager
					.getQuestionById(intId));
			user.addActivity("Answered question <"
					+ manager.getQuestionById(intId).getContent()
					+ "> by writing: <" + newAnswer + ">");
			redirect("/question/" + qid + "/answers/");
		}
	}

	public static void voteQuestion(String qid, String vote) {
		int id = Integer.parseInt(qid);
		User user = manager.getUserByName(session.get("username"));
		if (manager.getQuestionById(id).getOwner().equals(
				session.get("username"))) {
			String message = "You cannot vote your own question!";
			render(message, qid);
		} else if (manager.getQuestionById(id).checkUserVotedForQuestion(user)) {
			String message = "You already voted this question";
			render(message, qid);
		} else {
			manager.getQuestionById(id).vote(vote);
			manager.getQuestionById(id).userVotedForQuestion(user);
			redirect("/");
		}
	}

	public static void voteAnswer(String qid, String aid, String vote) {
		int id = Integer.parseInt(aid);
		User user = manager.getUserByName(session.get("username"));
		@SuppressWarnings("unused")
		Answer answer = manager.getAnswerById(id);
		if (manager.getAnswerById(id).getOwner()
				.equals(session.get("username"))) {
			String message = "You cannot vote your own answer!";
			render(message, qid);
		} else if (manager.getAnswerById(id).checkUserVotedForAnswer(user)) {
			String message = "You already voted this question";
			render(message, qid);
		} else {
			manager.getAnswerById(id).vote(vote);
			manager.getAnswerById(id).userVotedForAnswer(user);
			redirect("/question/" + qid + "/answers/");
		}
	}

	public static void showAnswerForm(String qid) {
		int intId = Integer.parseInt(qid);
		ArrayList<Answer> answers = manager.getAllAnswersByQuestionId(intId);
		Question question = manager.getQuestionById(intId);
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

}
