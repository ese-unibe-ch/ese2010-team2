package controllers;

import models.User;
import play.mvc.Mailer;

public class Mails extends Mailer {

	public static void confirm(User user, String name) {
		setSubject("Confirmation mail %s", name);
		addRecipient(user.getEmail());
		setFrom("noreply@ese.ch");
		setReplyTo("help@ese.ch");
		setContentType("text/html");
		send(user);
	}

	/**
	 * TODO: Make SMTP server configurable
	 * 
	 * @param server
	 */
	public static void configure(String server) {
	}
}
