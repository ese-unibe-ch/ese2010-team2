package models;

import java.io.File;
import java.util.ArrayList;

import annotations.Testing;

/**
 * The Class User provides all functionality that users have.
 */
public class User {

	private String name;
	private String email;
	private String password;
	private int id;
	private static int user_id = 0;

	/** The reputation. */
	private int score;

	/** The fields describing a user's personality. */
	private String phone, street, town, birthdate, background, hobbies, moto,
			quote;
	
	private File avatar;

	/** The activity-log. */
	public ArrayList<String> activity = new ArrayList<String>();

	/** The application-manager. */
	private static DbManager manager = DbManager
			.getInstance();

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
		this.id = user_id;
		this.score = 0;
		manager.getUsers().add(this);
		user_id++;
		activity.add(name + " is generated");
	}

	@Testing
	public User(String name, String email, String password, int userId) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.id = userId;
		this.user_id = userId;
		this.score = 0;
		manager.getUsers().add(this);
		user_id++;
		activity.add(name + " is generated");
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
	 * @param avatar File instance representing new avatar
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
}