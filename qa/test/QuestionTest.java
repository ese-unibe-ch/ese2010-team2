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

	@Before
	public void setUp() {
		u = new User("haha", "hah@ha.com", "");
		question = new Question("what", u);
		a1 = new Answer("hah", u, question);
		a2 = new Answer("hah", u, question);
		manager = DbManager.getInstance();
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
		question.addTags("hello world planet earth");
		assertEquals(4, question.getTags().size());
		assertEquals(4, DbManager.getTagList().size());
		assertTrue(question.getTags().contains("hello"));
		assertTrue(question.getTags().contains("world"));
		assertTrue(question.getTags().contains("planet"));
		assertTrue(question.getTags().contains("earth"));

	}

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
	}

}
