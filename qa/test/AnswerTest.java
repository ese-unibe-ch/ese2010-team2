import models.Answer;
import models.DbManager;
import models.Question;
import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class AnswerTest extends UnitTest {
	private static DbManager manager;
	private User admin;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void shouldCheckUserAlreadyVotedAnswer() {
		Question question = new Question(true, "content of question", admin);
		Answer answer = new Answer(true, "content of answer", admin, question);
		assertFalse(answer.checkUserVotedForPost(admin));
		answer.userVotedForPost(admin);
		assertTrue(answer.checkUserVotedForPost(admin));
	}

	@Test
	public void shouldVoteAnswer() {
		Question question = new Question(true, "content of question", admin);
		Answer answer = new Answer(true, "content of answer", admin, question);
		assertEquals(0, answer.getScore());
		// Vote Up
		answer.vote(1);
		assertEquals(1, answer.getScore());
		// Vote down
		answer.vote(-1);
		assertEquals(0, answer.getScore());
	}

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
	}

}
