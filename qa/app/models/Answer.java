package models;

import java.util.ArrayList;
import java.util.Date;

/**
 * The Class Answer delivers all functionality of the answers that other
 * votables don't have as well (those would be located in the class
 * Votable.java).
 */
public class Answer extends Post {

	/** The ID of the question the answer belongs to. */
	private int questionId;

	/** The is best answer. */
	private boolean isBestAnswer;

	/**
	 * Instantiates a new answer.
	 * 
	 * @param content
	 *            - the content of the answer.
	 * @param user
	 *            - the user that is answering.
	 * @param question
	 *            - the question the answer belongs to.
	 */
	public Answer(Boolean addAnswerToList, String content, User user,
			Question question) {
		this.questionId = question.getId();
		this.content = content;
		this.owner = user;
		isBestAnswer = false;
		date = new Date();
		question.setLastChangedDate(new Date());
		if (addAnswerToList) {
			oldVersions = new ArrayList<Post>();
			user.addActivity("Answered question <" + question.getContent()
					+ "> by writing: <" + content + ">");
			manager.addAnswer(this);
		}
	}

	/**
	 * Instantiates a new answer and adds it to the answer list.
	 * 
	 * @param content
	 *            - The content of the answer
	 * @param user
	 *            - The user who is answering
	 * @param question
	 *            - the question the answer belongs to.
	 */
	public Answer(String content, User user, Question question) {
		new Answer(true, content, user, question);
	}

	/**
	 * Gets the ID of the question the answer belongs to.
	 * 
	 * @return - the ID of the question the answer belongs to.
	 */
	public int getQuestionId() {
		return questionId;
	}

	public Post getQuestion() {
		return manager.getQuestionById(questionId);
	}

	/**
	 * Checks if this is the best Answer to the question. The best answer is
	 * selected by the user that asked the question.
	 * 
	 * @return - true if this answer is the best one and false if this is not
	 *         the case.
	 */
	public boolean isBestAnswer() {
		return isBestAnswer;
	}

	/**
	 * Set the answer being the best answer.
	 * 
	 * @param status
	 *            - true if the answer shall be the best or false if this isn't
	 *            the case.
	 */
	public void markAsBestAnswer(boolean status) {
		isBestAnswer = status;
		score += isBestAnswer ? 5 : -5;
	}

	/**
	 * Checks if the answer belongs to the question with a certain ID.
	 * 
	 * @param qid
	 *            - The id of the question you want to check the belonging.
	 * @return - true if the answer belongs to the question with the id qid or
	 *         false if not.
	 */
	public boolean belongsToQuestion(int qid) {
		return qid == questionId;
	}

	/**
	 * Gets all Comments which belongs to this answer
	 * 
	 * @return - a sorted list of comments
	 */
	public ArrayList<Comment> getComments() {
		return manager.getAllCommentsByAnswerIdSortedByDate(this.getId());
	}

	public void setContent(String content, String uname) {
		// this.oldVersions.add(0, new Answer(false, this.content, this.owner,
		// manager
		// .getQuestionById(questionId)));
		super.setContent(content, uname);
		// manager.getUserByName(uname).addActivity(
		// "Edited Answer " + this.id + " by writing: <" + content + ">.");
	}

	public void addVersion(String content, String uname) {
		Answer answer = new Answer(false, this.content, this.owner, manager
				.getQuestionById(this.questionId));
		this.oldVersions.add(0, answer);
		this.editedBy.add(manager.getUserByName(uname));
		super.setContent(content, uname);
		manager.getUserByName(uname).addActivity(
				"Edited Answer" + this.id + " by writing: <" + content + ">.");
		this.setLastChanged(getDate());
	}

	public void restoreOldVersion(String oldContent, String uname) {
		addVersion(oldContent, uname);
	}
}