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
 * This Controller manages the mutation of answers, like add,
 * edit and delete answers.
 */
@With(Secure.class)
public class MutateAnswerController extends Controller {
	
	private static DbManager manager = DbManager.getInstance();
	private static Calendar calendar = Calendar.getInstance();
	
	public static void showEditAnswerForm(int answerId, int qid,
			String newContent, String message) {
		Answer answer = manager.getAnswerById(answerId);
		render(answer, answerId, qid, newContent, message);
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
	
	public static void deleteAnswer(int aid, int qid){
		Answer answer = manager.getAnswerById(aid);
		manager.deleteAnswer(answer);
		redirect("/question/" + qid + "/answers/");
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
	
	public static void restoreAnswer(int actualId, String oldContent) {
		String uname = session.get("username");
		Answer actualAnswer = manager.getAnswerById(actualId);
		actualAnswer.addVersion(oldContent, uname);
		redirect("/answer/" + actualAnswer.getId() + "/history");
	}
}
