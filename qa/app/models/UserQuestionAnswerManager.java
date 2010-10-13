package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class UserQuestionAnswerManager {

	public static ArrayList<Question> questions;
	public static ArrayList<Answer> answers;
	public static ArrayList<User> users;
	public static ArrayList<String> tags;

	private static final UserQuestionAnswerManager INSTANCE = new UserQuestionAnswerManager();

	/**
	 * Statische Methode „getInstance()“ liefert die einzige Instanz der Klasse
	 * zurück.
	 */
	public static UserQuestionAnswerManager getInstance() {
		return INSTANCE;
	}

	private UserQuestionAnswerManager() {
		questions = new ArrayList<Question>();
		answers = new ArrayList<Answer>();
		users = new ArrayList<User>();
		tags=new ArrayList<String>();
	}

	public boolean checkUserNameIsOccupied(String name) {
		if (users.size() != 0) {
			for (int i = 0; i < users.size(); i++) {
				if (name.equals(users.get(i).getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkQuestionDuplication(String content) {
		if (questions.size() != 0) {
			for (int i = 0; i < questions.size(); i++) {
				if (content.equals(questions.get(i).getContent())) {
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public ArrayList<Answer> getAnswer() {
		return answers;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public User getUserByName(String name) {
		for (User user : users) {
			if (user.getName().equals(name))
				return user;
		}
		return null;
	}

	public ArrayList<Answer> getAllAnswersByQuestionId(int id) {
		ArrayList<Answer> questionAnswers = new ArrayList<Answer>();
		for (Answer answer : answers) {
			if (answer.getQuestionId() == id) {
				questionAnswers.add(answer);
			}
		}
		return questionAnswers;
	}

	public Question getQuestionById(int id) {
		for (Question question : questions)
			if (question.getId() == id)
				return question;
		return null;
	}

	public Answer getAnswerById(int id) {
		for (Answer answer : answers)
			if (answer.getId() == id)
				return answer;
		return null;
	}

	public ArrayList<Question> getQuestionsSortedByScore() {
		ArrayList<Question> sortedQuestions = questions;

		Collections.sort(sortedQuestions, new VotableComparator());

		return sortedQuestions;
	}

	public ArrayList<Answer> getAnswersSortedByScore(int id) {
		ArrayList<Answer> sortedAnswers = this.getAllAnswersByQuestionId(id);

		Collections.sort(sortedAnswers, new VotableComparator());

		return sortedAnswers;
	}
	
	public ArrayList<Question> getQuestionsSortedByDate(){
		ArrayList<Question> sortedQuestions= this.getQuestions();
		
		Collections.sort(sortedQuestions, new DateComparator());
		
		return sortedQuestions;
	}
	
	public ArrayList<String> getUserLog(String username){
		return this.getUserByName(username).getActivities();
	}
	
	public ArrayList<Votable> getVotablesByUserId(int userId) {
		ArrayList<Votable> usersVotables = new ArrayList<Votable>();
		for (Question currentQuestion : questions) {
			if (currentQuestion.getOwner().getId()  == userId) {
				usersVotables.add(currentQuestion);
			}
		}
		for (Answer currentAnswer : answers) {
			if (currentAnswer.getOwner().getId() == userId) {
				usersVotables.add(currentAnswer);
			}
		}
		return usersVotables;
	}
	
	public void addTag(String singleTag){
		if(!this.tags.contains(singleTag))
			this.tags.add(singleTag);
	}
}