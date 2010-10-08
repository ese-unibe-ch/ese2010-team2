package models;

import java.util.ArrayList;

import javax.annotation.Generated;

public class User {

	private String name;
	private String email;
	private String password;
	private int id;
	public ArrayList<String> activity = new ArrayList<String>();

	private static int user_id = 0;

	private static UserQuestionAnswerManager manager = UserQuestionAnswerManager
			.getInstance();

	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.id = user_id;
		manager.getUsers().add(this);
		user_id++;
		activity.add(name + " is generated");
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void addActivity(String act) {
		this.activity.add(act);
	}

	public ArrayList<String> getActivities() {
		return this.activity;
	}

}