import models.Answer;
import models.DbManager;
import models.Question;
import models.User;

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
		Question question = new Question("Good Question", topScorer);
		Answer answer = new Answer("Good Answer", topScorer, question);
		question.vote("1");
		answer.vote("2");
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

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.getTagList().clear();
		manager.resetAllIdCounts();
	}
}
