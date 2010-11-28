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
import xml.XMLParser;

//@OnApplicationStart
public class TestBootStrap extends Job {
	

	public void doJob() {
		User user = new User("admin", "admin@admin.ch", "admin");
		user.setGroup(UserGroups.admin);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
