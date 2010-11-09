package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * The Class User provides all functionality that users have.
 */
public class User {

	private String name;
	private String email;
	private String password;
	private int id;
	
	/** The current reputation. */
	private int score;
	
	/** The last know reputation */
	private int lastScore;
	
	/** The reputation over time */
	private LinkedList<Integer> reputations = new LinkedList<Integer>();
	
	/** The last time reputations were updated */
	private GregorianCalendar lastReputationUpdate;

	/** The group the user belongs to. */
	private UserGroups userGroup;

	/** The fields describing a user's personality. */
	private String phone, street, town, birthdate, background, hobbies, moto,
			quote;

	private File avatar;

	/** The activity-log. */
	public ArrayList<String> activity = new ArrayList<String>();

	/** The application-manager. */
	private static DbManager manager = DbManager.getInstance();
	
	/** A list of recent changes */
	private ArrayList<Notification> notifications;

	/** A list of voted questions */
	public ArrayList<Question> votedQuestions;
	
	/** A list of voted answers */
	public ArrayList<Answer> votedAnswers;
	
	/**
	 * Instantiates a new user.
	 * 
	 * @param name
	 *            - the username
	 * @param email
	 *            - the email
	 * @param password
	 *            - the password
	 */
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.score = 0;
		manager.addUser(this);
		this.userGroup = UserGroups.user;
		activity.add(name + " is generated");
		this.reputations = new LinkedList<Integer>();
		this.lastReputationUpdate = new GregorianCalendar();
		this.lastScore = 0;
		notifications = new ArrayList<Notification>();
		votedQuestions = new ArrayList<Question>();
		votedAnswers = new ArrayList<Answer>();
	}

	/**
	 * Logs activity of the user.
	 * 
	 * @param act
	 *            - the activity to be logged.
	 */
	public void addActivity(String act) {
		this.activity.add(0, act);
	}

	/**
	 * Gets all activities of a user that have been logged.
	 * 
	 * @return - the log of this user.
	 */
	public ArrayList<String> getActivities() {
		return this.activity;
	}

	/**
	 * Computes the score (=reputation) of this user.
	 */
	private void computeScore() {
		int userScore = 0;
		ArrayList<Post> usersVotables = manager.getVotablesByUserId(this
				.getId());
		for (Post currentVotable : usersVotables) {
			userScore += currentVotable.getScore();
		}
		this.setScore(userScore);
	}

	/*
	 * Getter methods
	 */
	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getStreet() {
		return street;
	}

	public String getTown() {
		return town;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public String getBackground() {
		return background;
	}

	public String getHobbies() {
		return hobbies;
	}

	public String getMoto() {
		return moto;
	}

	public String getQuote() {
		return quote;
	}

	public int getId() {
		return id;
	}

	public int getScore() {
		this.computeScore();
		return score;
	}
	
	public UserGroups getGroup(){
		return this.userGroup;
	}

	/*
	 * Setter methods
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	public void setMoto(String moto) {
		this.moto = moto;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

	/**
	 * @param avatar
	 *            File instance representing new avatar
	 */
	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	/**
	 * @return avatar filename
	 */
	public String getAvatarFileName() {
		return avatar.getName();
	}

	/**
	 * 
	 * @return File instance representing avatar image
	 * @see File
	 */
	public File getAvatar() {
		return avatar;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasAvatar() {
		return avatar != null;
	}

	/**
	 * Checks whether the user belongs to the administrators.
	 * 
	 * @return - true if the user is an admin and false otherwise.
	 */
	public boolean isAdmin() {
		return this.userGroup.equals(UserGroups.admin);
	}

	/**
	 * Checks whether the user belongs to the moderators.
	 * 
	 * @return - true if the user is a moderator and false otherwise.
	 */
	public boolean isModerator() {
		return this.userGroup.equals(UserGroups.moderator);
	}

	public void setId(int userId) {
		this.id = userId;
	}

	/**
	 * Sets the user group the user belongs to
	 * 
	 * @param group
	 *            - The group the user belongs to. The group is an entry of the
	 *            enum {@link #UserGroup.java}
	 */
	public void setGroup(UserGroups group) {
		this.userGroup = group;
	}
	
	/**
	 * returns the reputations as an ArrayList
	 */
	public ArrayList<Integer> getReputations() {
		this.updateReputation();
		ArrayList<Integer> reputations = new ArrayList<Integer>();
		reputations.addAll(this.reputations);
		return reputations;
	}
	
	public void updateReputation() {
		GregorianCalendar now = new GregorianCalendar();
		int counter = now.get(Calendar.DAY_OF_YEAR) - this.lastReputationUpdate.get(Calendar.DAY_OF_YEAR)-1;
		for (int i = 0; i < counter; i++) {
			this.addReputation(this.lastScore);
		}
		this.setLastTimeOfReputation(now);
		this.lastScore = this.getScore();
	}
	
	public void addReputation(int reputation) {
		this.reputations.addFirst(reputation);
	}
	
	public GregorianCalendar getLastTimeOfReputation() {
		return this.lastReputationUpdate;
	}
	
	public void setLastTimeOfReputation(GregorianCalendar date) {
		this.lastReputationUpdate = date;
	}
	
	public void setLastReputation(int reputation) {
		this.lastScore = reputation;
	}
	
	public int getLastReputation() {
		return this.lastScore;
	}
	
	public void addNotification(Notification newChange) {
		this.notifications.add(newChange);
	}
	
	public void removeNotification(Notification oldChange) {
		this.notifications.remove(oldChange);
	}
	
	public void clearAllNotifications() {
		this.notifications.clear();
	}
	
	public ArrayList<Notification> getAllNotifications() {
		return this.notifications;
	}

	public void notifyChange(String message, Question changedQuestion) {
		Notification change = new Notification(message, this, changedQuestion);	
	}
	
	public boolean isChanged() {
		return !notifications.isEmpty();
	}
	
	/**
	 * Adds a new question similar to the old question to the list of voted questions
	 * if it isn't already in it
	 * 
	 * @param post
	 */
	public void addvotedQuestion(Question question){
		Question newQuestion = question;
		if(this.votedQuestions.contains(question)!=true){
			this.votedQuestions.add(newQuestion);
		}
	}
	
	public Question getVotedQuestion(Question question){
		if(this.votedQuestions.contains(question)){
			int x = this.votedQuestions.indexOf(question);
			return this.votedQuestions.get(x); 
		}
		return null;
	}
	
	/**
	 * Adds a new answer similar to the old answer to the list of voted answer
	 * if it isn't already in it
	 * 
	 * @param post
	 */
	public void addvotedAnswer(Answer answer){
		Answer newAnswer = answer;
		if(this.votedAnswers.contains(answer)!=true){
			this.votedAnswers.add(newAnswer);
		}
	}
	
	public Answer getVotedAnswer(Answer answer){
		if(this.votedAnswers.contains(answer)){
			int x = this.votedAnswers.indexOf(answer);
			return this.votedAnswers.get(x); 
		}
		return null;
	}
	
	public ArrayList<Question> getVotedQuetions(){
		return this.votedQuestions;
	}
	
	public ArrayList<Answer> getVotedAnswers(){
		return this.votedAnswers;
	}
}