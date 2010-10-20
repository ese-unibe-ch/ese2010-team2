import models.Answer;
import models.DbManager;
import models.Question;
import models.Search;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class SearchTest extends UnitTest {
	private static DbManager manager;
	private User admin;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void shouldSearchQuestionTags() {
		Question question1 = new Question("question1", admin);
		question1.addTags("test");

		Search search = new Search("test");

		search.searchQuestionTags();

		assertEquals(0, (int) search.getQuestionTagIndexes().get(0));
		assertEquals(
				"test",
				manager.getQuestions()
						.get(search.getQuestionTagIndexes().get(0)).getTags()
						.get(0));
	}

	@Test
	public void shouldSearchQuestionContent() {
		Question question1 = new Question("question1", admin);
		Search search = new Search("question1");
		search.searchQuestionContent();
		assertEquals(0, (int) search.getQuestionContentIndexes().get(0));
		assertEquals(
				"question1",
				manager.getQuestions()
						.get(search.getQuestionContentIndexes().get(0))
						.getContent());
	}

	@Test
	public void shouldSearchAnswerContent() {
		Question question1 = new Question("question1", admin);
		Answer answer1 = new Answer("answer1", admin, question1);

		Search search = new Search("answer1");
		search.searchAnswerContent();

		assertEquals(0, (int) search.getAnswerContentIndexes().get(0));
		assertEquals(
				"answer1",
				manager.getAnswers()
						.get(search.getAnswerContentIndexes().get(0))
						.getContent());

	}

	@After
	public void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
	}
}
