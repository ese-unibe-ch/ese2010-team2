package xml;

/*
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Answer;
import models.DbManager;
import models.Question;
import models.User;
import models.UserGroups;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import play.Play;

/**
 * 
 * This class implements the DefaultHandler helper class, which means that it
 * defines all the "callback" methods that the SAX parser will invoke to notify
 * the application. In this example we override the methods that we require.
 * 
 */
public class XML_Parser extends DefaultHandler {
	// The fields of a user
	private int uId, uScore;
	private String phone, street, town, birthdate, background, hobbies, moto,
			quote, uname, email, password;
	private UserGroups userGroup = UserGroups.user;
	private File avatar;

	// The fields for a post
	private int pScore, pId;
	private User owner, editedBy;
	private String content;
	private Date creationDate = new Date();
	private Date lastChangedDate = new Date();

	// The fields for an answer
	private int qId;
	private boolean isBestAnswer = false;

	// The fields for a question
	private ArrayList<String> tags = new ArrayList<String>();
	private Answer bestAnswer;

	// counters
	static int questions = 0;
	static int answers = 0;
	static int users = 0;

	private String mode = "init";
	private StringBuilder builder;
	private static ArrayList<String> message = new ArrayList<String>();
	private static ArrayList<String> report = new ArrayList<String>();
	String tempValue = new String();

	/**
	 * This method is called when a new element begins. It resets the
	 * StringBuilder and adds the element name to the string
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes atts) {
		builder = new StringBuilder();
		if (mode.equals("init")) {
			if (qName.equalsIgnoreCase("users")) {
				mode = "users";
			} else if (qName.equalsIgnoreCase("questions")) {
				mode = "questions";
			} else if (qName.equalsIgnoreCase("answers")) {
				mode = "answers";
			}

		} else if (mode.equals("users")) {
			if (qName.equalsIgnoreCase("user")) {
				uId = Integer.parseInt(atts.getValue(("id")));
			}

		} else if (mode.equals("questions")) {
			if (qName.equalsIgnoreCase("question"))
				pId = Integer.parseInt(atts.getValue("id"));

		} else if (mode.equals("answers")) {
			if (qName.equalsIgnoreCase("answer"))
				pId = Integer.parseInt(atts.getValue("id"));
		}
	}

	/**
	 * This method is called when the parser encounters plain text (not XML
	 * elements). This method stores the text in the StringBuilder
	 */
	public void characters(char[] ch, int start, int length) {
		tempValue = new String(ch, start, length);
		// for(char c:ch){
		// tempValue.append(c);
		// }
	}

	/**
	 * This method is called when an element is closed.
	 */
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("users")
				|| qName.equalsIgnoreCase("questions")
				|| qName.equalsIgnoreCase("answers"))
			mode = "init";
		if (mode.equalsIgnoreCase("users")) {

			if (qName.equalsIgnoreCase("displayname"))
				uname = tempValue.toString();

			else if (qName.equalsIgnoreCase("ismoderator")) {
				if (tempValue.toString().equalsIgnoreCase("true"))
					userGroup = UserGroups.moderator;
			} else if (qName.equalsIgnoreCase("email"))
				email = tempValue.toString();

			else if (qName.equalsIgnoreCase("password"))
				password = tempValue.toString();

			else if (qName.equalsIgnoreCase("aboutme"))
				hobbies = tempValue.toString();

			else if (qName.equalsIgnoreCase("location"))
				town = tempValue.toString();

			else if (qName.equalsIgnoreCase("website"))
				background = tempValue.toString();

			else if (qName.equalsIgnoreCase("user")) {
				if (uname.isEmpty() || password.isEmpty()) {
					message
							.add("ERROR: User " + uId
									+ " couldn't be imported.");
					this.cleanUser();
				} else {
					User user = new User(uname, email, password);
					user.setId(uId);
					user.setGroup(userGroup);
					user.setHobbies(hobbies);
					user.setTown(town);
					user.setBackground(background);
					users++;
					this.cleanUser();
				}
			}

			else if (qName.equalsIgnoreCase("users")) {
				// set mode to "init" again
				mode = "init";
			}

		} else if (mode.equalsIgnoreCase("questions")) {
			if (qName.equalsIgnoreCase("ownerid")) {
				int oId = Integer.parseInt(tempValue.toString());
				owner = DbManager.getInstance().getUserById(oId);
			} else if (qName.equalsIgnoreCase("creationdate"))
				creationDate.setTime(Integer.parseInt(tempValue.toString()));

			else if (qName.equalsIgnoreCase("lastactivity"))
				lastChangedDate.setTime(Integer.parseInt(tempValue.toString()));

			else if (qName.equalsIgnoreCase("body"))
				content = tempValue;

			else if (qName.equalsIgnoreCase("tag"))
				tags.add(tempValue.toString());

			else if (qName.equalsIgnoreCase("question")) {
				if (content.isEmpty() || owner == null) {
					message.add("ERROR: question " + pId
							+ " couldn't be imported.");
					this.cleanQuestion();
				} else {
					Question question = new Question(false, content, owner);
					question.addTags(tags);
					question.setDate(creationDate);
					question.setLastChanged(lastChangedDate);
					question.setId(pId);
					DbManager.getInstance().addQuestion(question, pId);
					questions++;
					this.cleanQuestion();
				}
			}

			else if (qName.equalsIgnoreCase("questions")) {
				// set mode to "init" again
				mode = "init";
			}

		} else if (mode.equalsIgnoreCase("answers")) {
			if (qName.equalsIgnoreCase("ownerid")) {
				owner = DbManager.getInstance().getUserById(
						Integer.parseInt(tempValue.toString()));
			} else if (qName.equalsIgnoreCase("questionid"))
				qId = Integer.parseInt(tempValue.toString());

			else if (qName.equalsIgnoreCase("creationdate"))
				creationDate.setTime(Integer.parseInt(tempValue.toString()));

			else if (qName.equalsIgnoreCase("lastactivity"))
				lastChangedDate.setTime(Integer.parseInt(tempValue.toString()));

			else if (qName.equalsIgnoreCase("body"))
				content = tempValue.toString();

			else if (qName.equalsIgnoreCase("accepted")) {
				if (tempValue.toString().equals("true"))
					isBestAnswer = true;
			}

			else if (qName.equalsIgnoreCase("answer")) {
				if (DbManager.getInstance().getQuestionById(qId) == null
						|| content.isEmpty() || owner == null) {
					message.add("ERROR: Answer " + pId
							+ " couldn't be created.");
					this.cleanAnswer();
				} else {
					Answer a = new Answer(content, owner, DbManager
							.getInstance().getQuestionById(qId));
					a.markAsBestAnswer(isBestAnswer);
					a.setDate(creationDate);
					a.setLastChanged(lastChangedDate);
					answers++;
					this.cleanAnswer();
				}
			}

			else if (qName.equalsIgnoreCase("answers"))
				mode = "init";

		}
		tempValue = new String();
	}

	private void cleanUser() {
		uname = new String();
		email = new String();
		password = new String();
		userGroup = UserGroups.user;
		hobbies = new String();
		town = new String();
		background = new String();
	}

	private void cleanQuestion() {
		owner = null;
		creationDate = new Date();
		lastChangedDate = new Date();
		content = new String();
		tags.clear();
	}

	private void cleanAnswer() {
		owner = null;
		content = new String();
		isBestAnswer = false;
	}

	/** This method is called when warnings occur */
	public void warning(SAXParseException exception) {
		System.err.println("WARNING: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/** This method is called when errors occur */
	public void error(SAXParseException exception) {
		System.err.println("ERROR: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/** This method is called when non-recoverable errors occur. */
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("FATAL: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		throw (exception);
	}

	/** The main method sets things up for parsing */
	public static void main(/* String[] args */) throws IOException,
			SAXException, ParserConfigurationException {

		// Clear the messages-string and counters.
		message.clear();
		questions = 0;
		answers = 0;
		users = 0;

		// Create a JAXP "parser factory" for creating SAX parsers
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser saxParser = saxParserFactory.newSAXParser();

		// Create a SAX input source for the file argument
		InputSource input = new InputSource(new FileReader(Play.applicationPath
				.getAbsolutePath()
				+ "/public/data/data"));

		// Create an instance of this class; it defines all the handler methods
		XML_Parser handler = new XML_Parser();

		// Finally, tell the parser to parse the input and notify the handler
		saxParser.parse(input, handler);

		// Generate report
		report.add("Imported " + users + " users, " + questions
				+ " questions and " + answers + " answers.");

	}

	public static ArrayList<String> getMessage() {
		return message;
	}

	public static ArrayList<String> getReport() {
		return report;
	}

}