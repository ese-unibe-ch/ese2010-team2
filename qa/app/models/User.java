package models;

import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.Generated;

public class User {

	private String name;
	private String email;
	private String password;
	private int id;
	private String phone, street, town, birthdate, background, hobbies, moto, quote;
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
	public void addActivity(String act) {
		this.activity.add(0, act);
	}

	public ArrayList<String> getActivities() {
		return this.activity;
	}

	/**List of getters*/
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

	/**List of setters*/
	public void setName(String name){
		this.name = name;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	public void setStreet(String street){
		this.street = street;
	}
	public void setTown(String town){
		this.town = town;
	}
	public void setBirthdate(String birthdate){
		this.birthdate = birthdate;
	}
	public void setBackground(String background){
		this.background = background;
	}
	public void setHobbies(String hobbies){
		this.hobbies = hobbies;
	}
	public void setMoto(String moto){
		this.moto = moto;
	}
	public void setQuote(String quote){
		this.quote = quote;
	}
	
	
	public String toString() {
		return name;
	}

}