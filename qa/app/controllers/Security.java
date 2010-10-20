package controllers;

import models.User;
import models.DbManager;

public class Security extends Secure.Security {

	private static DbManager manager = DbManager
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