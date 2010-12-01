package controllers;

import models.DbManager;
import models.Question;
import models.User;
import models.Vote;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;

/**
 * This Controller manages mutations of questions, like add, 
 * edit and delete question.
 */
@With(Secure.class)
public class MutateQuestionController extends Controller {

	private static DbManager manager = DbManager.getInstance();
	public static void showEditQuestionForm(int qid, String newContent,
			String tags) {
		Question question = manager.getQuestionById(qid);
		render(question, newContent, qid, tags);
	}
	
	/**
	 * Sets the content of the question to the new value
	 * 
	 * @param qid
	 * @param newContentQuestion
	 * @param newContentTag
	 */
	public static void editQuestion(int qid, String newContentQuestion,
			String newContentTag) {
		String copyTags = "" + newContentTag;

		if (newContentQuestion.equals("") || newContentQuestion.equals(" ")) {
			flash.error("Your question is empty!");
			showEditQuestionForm(qid, newContentQuestion, newContentTag);
		} else if (!Question.checkTags(copyTags).isEmpty()) {
			flash.error("The following tags already exist: "
					+ Question.checkTags(copyTags)
					+ ". Please review your tags.");
			showEditQuestionForm(qid, newContentQuestion, newContentTag);
		}
		else {
			manager.getQuestionById(qid).addVersion(newContentQuestion,
					newContentTag, session.get("username"));
			redirect("/question/" + qid + "/answers/");
		}
	}
	
	public static void showQuestionForm(String title, String newQuestion, String tags) {
		String randomID = Codec.UUID();
		render(title, newQuestion, tags, randomID);
	}
	
	public static void deleteQuestion(int qid){
		Question question = manager.getQuestionById(qid);
		manager.deleteQuestion(question);
		redirect("/");
	}
	
	public static void addQuestion(String newQuestion, String title,
			String tags, String code, String randomID) {
		// Store the overgiven tags in another object to prevent information
		// loss due to splitting the tag list.
		String copyTags = "" + tags;

		validation.equals(code, Cache.get(randomID));
		User user = manager.getUserByName(session.get("username"));
		if (newQuestion.equals("") || newQuestion.equals(" ")) {
			flash.error("Your question is empty!");
			showQuestionForm(title, newQuestion, tags);
		} else if (manager.checkQuestionDuplication(newQuestion)) {
			flash.error("Your question already exists!");
			showQuestionForm(title, newQuestion, tags);
		} else if (!Question.checkTags(copyTags).isEmpty()) {
			flash.error("The following tags already exist: "
					+ Question.checkTags(copyTags)
					+ ". Please review your tags.");
			showQuestionForm(title, newQuestion, tags);
		} else if(validation.hasErrors()){
			flash.error("Please check the CAPTCHA code");
			showQuestionForm(title, newQuestion, tags);
		}
		else {
			Question question = new Question(true, newQuestion, title, user);
			question.addTags(tags);
			redirect("/question/" + question.getId() + "/answers/");
		}
	}
	
	public static void voteQuestionUp(int qid) {
		voteQuestion(qid, 1);
	}

	public static void voteQuestionDown(int qid) {
		voteQuestion(qid, -1);
	}

	public static void voteQuestion(int qid, int vote) {
		Question question = manager.getQuestionById(qid);
		User user = manager.getUserByName(session.get("username"));
		Vote oldVote = question.getVoteForUser(user);
		boolean check = oldVote.voteChangeable();
		question.userVotedForPost(user);
		if(question.checkUserVotedForPost(user)==true && check == false){
			flash.error("You already voted for this post !");
//			render(qid);
		}
		if(vote == 1 && check == true && oldVote.getVote()==0){
			oldVote.setVote(1);
			question.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(message, qid);
		}
		if(vote == 1 && check == true && oldVote.getVote()==-1){
			oldVote.setVote(0);
			question.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(message, qid);
		}
		if(vote == -1 && check == true && oldVote.getVote()==0){
			oldVote.setVote(-1);
			question.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(message, qid);
		}
		if(vote == -1 && check == true && oldVote.getVote()==1){
			oldVote.setVote(0);
			question.vote(oldVote);
			flash.success("You're current vote is "+oldVote.getVote());
//			render(message, qid);
		}
		redirect("/question/" + qid + "/answers/");
	}
	
	public static void restoreQuestion(int actualId, String oldContent,
			String oldTags) {
		String uname = session.get("username");
		// unschön, aber es erfüllt den zweck:
		String tags = oldTags.replace("[", "").replace("]", "")
				.replace(",", "");
		Question actualQuestion = manager.getQuestionById(actualId);
		actualQuestion.addVersion(oldContent, tags, uname);
		redirect("/question/" + actualQuestion.getId() + "/history");
	}

}
