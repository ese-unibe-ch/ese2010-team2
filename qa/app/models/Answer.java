package models;

import java.util.ArrayList;
import java.util.Arrays;

public class Answer extends Votable {

	private ArrayList<User> userVotedForAnswer = new ArrayList<User>();
	private int questionId;
	private boolean isBestAnswer;
	private static int answer_id = 0;

	public Answer(String content, User user, Question question) {
		this.questionId = question.getId();
		this.content = content;
		this.owner = user;
		this.id = answer_id;
		isBestAnswer = false;
		currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
		userQuestionAnswerManager.getAnswer().add(this);
		answer_id++;
	}

	public boolean checkUserVotedForAnswer(User user) {
		for (int i = 0; i < userVotedForAnswer.size(); i++) {
			if (user.getName().equals(userVotedForAnswer.get(i).getName())) {
				return true;
			}
		}
		return false;
	}

	public void userVotedForAnswer(User user) {
		userVotedForAnswer.add(user);
	}

	public int getQuestionId() {
		return questionId;
	}
	
	public boolean isBestAnswer() {
		return isBestAnswer;
	}
	
	public void markAsBestAnswer(boolean status) {
		isBestAnswer = status;
	}
	
	public boolean belongsToQuestion(int qid) {
		return qid == questionId;
	}
}