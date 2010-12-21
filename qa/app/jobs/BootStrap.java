package jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.User;
import models.UserGroups;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import xml.XMLParser;

@OnApplicationStart
public class BootStrap extends Job {
	

	public void doJob() {
		User user = new User("admin", "admin@admin.ch", "admin");
		user.setGroup(UserGroups.admin);

		User user1 = new User("moderator", "moderator@moderator.ch",
				"moderator");
		user1.setGroup(UserGroups.moderator);

		User user2 = new User("user", "user@user.ch", "user");
		user2.setGroup(UserGroups.user);

		// ****** Loading small.xml sample data *******
		File data = new File(Play.applicationPath.getAbsolutePath()
				+ "/bootstrap/small.xml");
		File xmlDir = new File(Play.applicationPath.getAbsolutePath()
				+ "/public/data");
		if (!xmlDir.exists()) {
			xmlDir.mkdir();
		} else {
			File existingData = new File(xmlDir + "/data");
			if (existingData.exists())
				existingData.delete();
		}

		File newData = new File(xmlDir.getPath() + "/data");

		try {
			newData.createNewFile();
			FileInputStream in = new FileInputStream(data);
			FileOutputStream out = new FileOutputStream(newData);
			IOUtils.copy(in, out);
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			XMLParser.main();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

}
