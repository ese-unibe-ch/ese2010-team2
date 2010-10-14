import org.junit.Before;
import org.junit.Test;

import models.Answer;
import models.Question;
import models.User;
import models.UserQuestionAnswerManager;
import play.test.UnitTest;

public class UserQuestionAnswerManagerTest extends UnitTest {
	private UserQuestionAnswerManager manager;
	private User admin;

	@Before
	public void setUp() {
		manager = UserQuestionAnswerManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void souldGetRecentQuestions() {
		Question newQuestion1 = new Question("question1", admin);
		Question newQuestion2 = new Question("question2", admin);

		assertEquals(1, manager.getRecentQuestionsByNumber(1).size());
		assertEquals(2, manager.getRecentQuestionsByNumber(2).size());
		assertTrue(manager.getRecentQuestionsByNumber(1).contains(newQuestion2));
		assertTrue(manager.getRecentQuestionsByNumber(2).contains(newQuestion1)
				&& manager.getRecentQuestionsByNumber(2).contains(newQuestion2));
	}

	@Test
	public void shouldGetUserLog() {
		User newUser = new User("user", "user@ese", "user");
		assertEquals(1, manager.getUserLog("user").size());

		Question newQuestion = new Question("question1", newUser);
		assertEquals(2, manager.getUserLog("user").size());
		assertEquals("Asked question <question1>", manager
				.getUserLog("user").get(0));

		Answer newAnswer = new Answer("answer1", newUser, newQuestion);
		assertEquals(3, manager.getUserLog("user").size());
		assertEquals("Answered question <question1> by writing: <answer1>",
				manager.getUserLog("user").get(0));
	}

}
