package controllers;

import java.util.ArrayList;

import models.Answer;
import models.DbManager;
import models.Post;
import models.User;
import models.Vote;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This Controller manages the mutation of answers, like add,
 * edit and delete answers.
 */
@With(Secure.class)
public class MutateAnswerController extends Controller {
	
	private static DbManager manager = DbManager.getInstance();
	public static void showEditAnswerForm(int answerId, int qid,
			String newContent) {
		Answer answer = manager.getAnswerById(answerId);
		render(answer, answerId, qid, newContent);
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
			flash.error("Your answer is empty.");
			showEditAnswerForm(answerId, qid, newContent);
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
	
	public static void addAnswer(String qid, String newAnswer, String code, String randomID) {
		int intId = Integer.parseInt(qid);
		User user = manager.getUserByName(session.get("username"));
		validation.equals(code, Cache.get(randomID));
		if (newAnswer.equals("") || newAnswer.equals(" ")) {
			flash.error("Your answer is empty.");
			DisplayQuestionController.showAnswers(qid, newAnswer);
		} else if(validation.hasErrors()){
			flash.error("Please check your code.");
			DisplayQuestionController.showAnswers(qid, newAnswer);
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
		Vote oldVote = answer.getVoteForUser(user);
		boolean check = oldVote.voteChangeable();
		answer.userVotedForPost(user);
		if(answer.checkUserVotedForPost(user)==true && check == false){
			manager.updateReputation(answer.getOwner());
			flash.error("You already voted for this post !");
//			render(aid, qid);
		}
		if(vote == 1 && check == true && oldVote.getVote()==0){
			oldVote.setVote(1);
			answer.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(aid, qid);
		}
		if(vote == 1 && check == true && oldVote.getVote()==-1){
			oldVote.setVote(0);
			answer.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(aid, qid);
		}
		if(vote == -1 && check == true && oldVote.getVote()==0){
			oldVote.setVote(-1);
			answer.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(aid, qid);
		}
		if(vote == -1 && check == true && oldVote.getVote()==1){
			oldVote.setVote(0);
			answer.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(aid, qid);
		}
		redirect("/question/" + qid + "/answers/");
	}

	public static void showAnswerForm(String qid) {
		int intId = Integer.parseInt(qid);
		String randomID = Codec.UUID();
		ArrayList<Answer> answers = manager.getAllAnswersByQuestionId(intId);
		Post question = manager.getQuestionById(intId);
		render(answers, question, randomID);
	}
	
	public static void restoreAnswer(int actualId, String oldContent) {
		String uname = session.get("username");
		Answer actualAnswer = manager.getAnswerById(actualId);
		actualAnswer.addVersion(oldContent, uname);
		redirect("/answer/" + actualAnswer.getId() + "/history");
	}
}
