package controllers;

import models.User;
import play.mvc.Mailer;

public class Mails extends Mailer {

	public static void confirm(User user) {
		setSubject("Confirmation mail %s", user.getName());
		addRecipient(user.getEmail());
		setFrom("noreply@ese.ch");
		setReplyTo("help@ese.ch");
		setContentType("text/html");
		send(user);
	}

}