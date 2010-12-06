import java.util.GregorianCalendar;

import models.Answer;
import models.DbManager;
import models.Notification;
import models.Question;
import models.User;
import models.Vote;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class UserTest extends UnitTest {
	private static DbManager manager;
	private User admin;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void shouldGetRightScore() {
		User topScorer = new User("scorer", "user@champion", "password");
		Question question = new Question(true, "Good Question", "title",
				topScorer);
		Answer answer = new Answer(true, "Good Answer", topScorer, question);
		question.setScore(1);
		answer.setScore(2);
		assertEquals(3, topScorer.getScore());
	}

	@Test
	public void shouldAddUserLog() {
		User logTester = new User("logTester", "test@log", "pw");
		logTester.addActivity("Activity1");
		logTester.addActivity("Activity2");
		assertEquals(3, logTester.getActivities().size());
		assertEquals("Activity2", logTester.getActivities().get(0));
	}

	@Test
	public void shouldUpdateReputation() {
		User reputatedUser = new User("reputatedUser", "rep.user@ese.ch",
				"1234");
		GregorianCalendar twoDaysAgo = new GregorianCalendar();
		twoDaysAgo.setTimeInMillis(twoDaysAgo.getTimeInMillis() - 2 * 24 * 60
				* 60 * 1000);
		reputatedUser.setLastTimeOfReputation(twoDaysAgo);
		reputatedUser.addReputation("admin",3);
		reputatedUser.setLastReputation("admin",4);
		assertEquals(2, reputatedUser.getReputations().size());
		assertEquals(4, (int) reputatedUser.getReputations().get(0)); // yesterday
		assertEquals(3, (int) reputatedUser.getReputations().get(1)); // the day
																		// before
																		// yesterday
	}

	@Test
	public void shouldUpdateLastReputation() {
		User reputatedUser = new User("reputatedUser", "rep.user@ese.ch",
				"1234");
		Question question = new Question(true, "question", "title",
				reputatedUser);
		Vote vote1 = new Vote(question, 3, admin);
		assertEquals(0, reputatedUser.getLastReputation());
		question.vote(vote1);
		assertEquals(3, reputatedUser.getLastReputation());
	}

	@Test
	public void shouldAddNotification() {
		User notifiedUser = new User("notifiedUser", "note.user@ese.ch", "1234");
		Question changedQuestion = new Question("some question", "title",
				notifiedUser);
		Notification notification = new Notification("something changed",
				notifiedUser, changedQuestion);
		assertEquals(1, notifiedUser.getAllNotifications().size());
		assertEquals(notification, notifiedUser.getAllNotifications().get(0));
	}

	@Test
	public void shouldRemoveNotification() {
		User notifiedUser = new User("notifiedUser", "note.user@ese.ch", "1234");
		Question changedQuestion = new Question("some question", "title",
				notifiedUser);
		Notification notification = new Notification("something changed",
				notifiedUser, changedQuestion);
		notifiedUser.removeNotification(notification);
		assertTrue(notifiedUser.getAllNotifications().isEmpty());
	}

	@Test
	public void shouldClearNotification() {
		User notifiedUser = new User("notifiedUser", "note.user@ese.ch", "1234");
		Question changedQuestion = new Question("some question", "title",
				notifiedUser);
		new Notification("something changed", notifiedUser, changedQuestion);
		new Notification("something changed", notifiedUser, changedQuestion);
		notifiedUser.clearAllNotifications();
		assertTrue(notifiedUser.getAllNotifications().isEmpty());
	}
	
	@Test
	public void shouldAvoidUpRatingEachOther(){
		new User("user1","user1@mail.com","user1");
		new User("user2","u2@u.u","user2");
		User u=new User("u","u@u.u","u");
		assertEquals(u.getScore(),0);
		u.addReputation("user1", 4);
		assertTrue(u.getReputations().get(0)==4); //assertEquals sei ambiguous für UserTest??
		u.addReputation("user1", 3);
		assertTrue(u.getReputations().get(0)==3);
		u.addReputation("user1", 10);
		assertTrue(u.getReputations().get(0)==10);
		u.addReputation("user1", 10);
		assertTrue(u.getReputations().get(0)==10);
		u.addReputation("user2", 10);
		assertTrue(u.getReputations().get(0)==10);
	}

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.clearQuestionsMap();
		manager.clearAnswerMap();
		manager.resetAllIdCounts();
		manager.getComments().clear();
		manager.getTagList().clear();
	}
}
