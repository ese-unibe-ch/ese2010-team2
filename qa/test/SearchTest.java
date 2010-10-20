import models.Answer;
import models.Comment;
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
	public void shouldNotBeUpperLowerCaseSensitive() {
		Question question3 = new Question("question1", admin);
		Answer answer3 = new Answer("answer1", admin, question3);
		question3.addTags("test");
		Comment commentQuestion = new Comment(admin, question3, "comment");
		Comment commentAnswer = new Comment(admin, answer3, "comment1");

		Search search = new Search("QuEstion1");
		search.searchQuestionContent();
		assertEquals("question1", search.getQuestionContentResults().get(0)
				.getContent());

		Search search1 = new Search("AnSWer1");
		search1.searchAnswerContent();
		assertEquals("answer1", search1.getAnswerContentResults().get(0)
				.getContent());

		Search search2 = new Search("cOMMent");
		search2.searchComments();
		assertEquals("comment", search2.getCommentResults().get(0).getContent());
		assertEquals("comment1", search2.getCommentResults().get(1)
				.getContent());

		Search search3 = new Search("TesT");
		search3.searchQuestionTags();
		assertEquals("test", search3.getQuestionTagsResults().get(0).getTags()
				.get(0));
	}

	@Test
	public void shouldSearchQuestionTags() {
		Question question1 = new Question("question1", admin);
		question1.addTags("test");

		Search search = new Search("test");

		search.searchQuestionTags();

		assertEquals("test", search.getQuestionTagsResults().get(0).getTags()
				.get(0));
	}

	@Test
	public void shouldSearchQuestionContent() {
		Question question1 = new Question("question1", admin);
		Search search = new Search("question1");
		search.searchQuestionContent();
		assertEquals("question1", search.getQuestionContentResults().get(0)
				.getContent());
	}

	@Test
	public void shouldSearchAnswerContent() {
		Question question1 = new Question("question1", admin);
		Answer answer1 = new Answer("answer1", admin, question1);

		Search search = new Search("answer1");
		search.searchAnswerContent();

		assertEquals("answer1", search.getAnswerContentResults().get(0)
				.getContent());

	}

	@Test
	public void shouldSearchComments() {
		Question question1 = new Question("question1", admin);
		Answer answer1 = new Answer("answer1", admin, question1);

		Comment commentQuestion = new Comment(admin, question1,
				"commentToQuestion1");
		Comment commentAnswer = new Comment(admin, answer1, "commentToAnswer1");

		Search search = new Search("commentToQuestion1");
		search.searchComments();
		assertEquals("commentToQuestion1", search.getCommentResults().get(0)
				.getContent());

		Search search1 = new Search("commentToAnswer1");
		search1.searchComments();

		assertEquals("commentToAnswer1", search1.getCommentResults().get(0)
				.getContent());
	}

	@Test
	public void shouldAddNotingWhenNoMatches() {
		Question question1 = new Question("question1", admin);
		Answer answer1 = new Answer("answer1", admin, question1);
		question1.addTags("test");
		Comment commentQuestion = new Comment(admin, question1,
				"commentToQuestion1");
		Comment commentAnswer = new Comment(admin, answer1, "commentToAnswer1");

		Search search = new Search("nothing");
		search.searchComments();
		search.searchQuestionContent();
		search.searchAnswerContent();
		search.searchQuestionTags();

		assertEquals(0, search.getAnswerContentResults().size());
		assertEquals(0, search.getCommentResults().size());
		assertEquals(0, search.getQuestionContentResults().size());
		assertEquals(0, search.getQuestionTagsResults().size());
	}

	@Test
	public void shouldAddQuestionOnlyOnceWhenMoreThanOneTagMatches() {
		Question question1 = new Question("question1", admin);
		question1.addTags("test");
		question1.addTags("test");

		Search search = new Search("test");
		search.searchQuestionTags();

		assertEquals(2, question1.getTags().size());
		assertEquals(1, search.getQuestionTagsResults().size());

	}

	@After
	public void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
		manager.getTagList().clear();
	}
}
