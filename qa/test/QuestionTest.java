import models.Answer;
import models.DbManager;
import models.Question;
import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.test.UnitTest;

public class QuestionTest extends UnitTest {
	private Question question;
	private Answer a1, a2;
	private User u;
	private static DbManager manager;
	private User admin;

	@Before
	public void setUp() {
		u = new User("haha", "hah@ha.com", "");
		question = new Question("what", u);
		a1 = new Answer("hah", u, question);
		a2 = new Answer("hah", u, question);
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Ignore
	public void shouldNotChangeBestAnswerAfter30Minutes() {
		// TODO find out how to test this
		assertTrue(false);
	}

	@Test
	public void shouldNotAllowToSetArbitrayAnswerAsBestAnswer() {
		Answer a3 = new Answer("haha", u, new Question("", u));
		question.setBestAnswer(a3);
		assertFalse(question.hasBestAnswer());
	}

	@Test
	public void shouldNotHaveBestAnswerAfterCreation() {
		assertFalse(question.hasBestAnswer());
	}

	@Test
	public void shouldAllowToChangeBestAnswerIfLastChangeLessThan30MinAgo() {
		question.setBestAnswer(a1);
		assertTrue(question.bestAnswerChangeable());
		assertTrue(question.hasBestAnswer());
		assertEquals(a1, question.getBestAnswer());

		question.setBestAnswer(a2);
		assertTrue(question.bestAnswerChangeable());
		assertTrue(question.hasBestAnswer());
		assertEquals(a2, question.getBestAnswer());
	}

	@Test
	public void shouldAddTags() {
		// Before checking size before adding 4 tags
		assertEquals(4, DbManager.getTagList().size());

		question.addTags("hello world planet earth");
		assertEquals(4, question.getTags().size());

		// Size after adding 4 tags should now incremented by 4
		assertEquals(8, DbManager.getTagList().size());

		assertTrue(question.getTags().contains("hello"));
		assertTrue(question.getTags().contains("world"));
		assertTrue(question.getTags().contains("planet"));
		assertTrue(question.getTags().contains("earth"));

	}

	@Test
	public void shouldCheckUserAlreadyVotedQuestion() {
		Question question = new Question("content of question", admin);
		assertFalse(question.checkUserVotedForQuestion(admin));
		question.userVotedForQuestion(admin);
		assertTrue(question.checkUserVotedForQuestion(admin));
	}

	@Test
	public void shouldVoteQuestion() {
		Question question = new Question("content of question", admin);
		assertEquals(0, question.getScore());
		// Vote Up
		question.vote(1);
		assertEquals(1, question.getScore());
		// Vote down
		question.vote(-1);
		assertEquals(0, question.getScore());
	}

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
	}

}
