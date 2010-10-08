package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Answer extends Votable {

	private ArrayList<User> userVotedForAnswer = new ArrayList<User>();
	private int questionId;
	private static int answer_id = 0;

	public Answer(String content, User user, Question question) {
		this.questionId = question.getId();
		this.content = content;
		this.owner = user.getName();
		this.id = answer_id;
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
}