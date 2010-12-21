package controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import models.User;
import play.Play;
import play.mvc.Mailer;

public class Mails extends Mailer {
	private static String configFile = Play.applicationPath + "/conf/mail.conf";

	public static void confirm(User user, String name) {
		setSubject("Confirmation mail %s", name);
		addRecipient(user.getEmail());
		setFrom("noreply@ese.ch");
		setReplyTo("help@ese.ch");
		setContentType("text/html");
		send(user);
	}

	/**
	 * 
	 * @return clone of config file name
	 */
	public static String getConfigFileName() {
		return new String(configFile);
	}

	/**
	 * Writes new SMTP host to properties file.
	 * 
	 * @param server
	 */
	public static void configure(String server) {
		Properties prop = new Properties();
		FileInputStream is = null;
		OutputStream out = null;

		try {
			is = new FileInputStream(configFile);
			prop.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		prop.put("mail.smtp.host", server);

		try {
			out = new FileOutputStream(configFile);
			prop.store(out, "");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
