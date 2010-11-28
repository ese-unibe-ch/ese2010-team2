package controllers;

import models.DbManager;
import models.User;

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
	static void onDisconnected() {
		Application.index();
	}
}