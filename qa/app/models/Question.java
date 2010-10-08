package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Question extends Votable {

	private ArrayList<User> userVotedForQuestion = new ArrayList<User>();
	private static int question_id = 0;

	public Question(String content, User questionOwner) {
		this.owner = questionOwner.getName();
		this.content = content;
		currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
		this.id = question_id;
		this.score = 0;
		userQuestionAnswerManager.getQuestions().add(this);
		question_id++;
	}

	public boolean checkUserVotedForQuestion(User user) {
		for (int i = 0; i < userVotedForQuestion.size(); i++) {
			if (user.getName().equals(userVotedForQuestion.get(i).getName())) {
				return true;
			}
		}
		return false;
	}

	public void userVotedForQuestion(User user) {
		userVotedForQuestion.add(user);
	}
	
}