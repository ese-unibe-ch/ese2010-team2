package controllers;

import models.User;
import models.UserQuestionAnswerManager;

public class Security extends Secure.Security {

	private static UserQuestionAnswerManager manager = UserQuestionAnswerManager
			.getInstance();

	static boolean authenticate(String username, String password) {
		User user = manager.getUserByName(username);
		if (user != null && user.getPassword().equals(password)) {
			session.put("uid", user.getId());
			return true;
		}
		return false;
	}

}