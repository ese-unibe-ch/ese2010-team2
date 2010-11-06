import models.Answer;
import models.Comment;
import models.DbManager;
import models.Post;
import models.Question;
import models.Search;
import models.SearchQueryParser;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class SearchTest extends UnitTest {
	private static DbManager manager;
	private User admin;
	private SearchQueryParser parser;
	private String query;


	@Before
	public void setUp() {
		query = "";
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
		parser = new SearchQueryParser(query);

	}

	@Test
	public void sentenceSearchShouldNotBeCaseSensitive() {
		Question question3 = new Question(true, "question1 is the best", admin);
		Answer answer3 = new Answer(true, "answer1 is the best", admin,
				question3);
		Comment commentQuestion = new Comment(admin, question3,
				"comment is the best");

		parser = new SearchQueryParser("\"question1 is the best\"");
		Search search = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("question1 is the best", search.getQuestions().get(0)
				.getContent());

		parser = new SearchQueryParser("\"answer1 is the best\"");
		Search search1 = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("answer1 is the best", search1.getAnswerContentResults()
				.get(0)
				.getContent());

		parser = new SearchQueryParser("\"comment is the best\"");
		Search search2 = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("comment is the best", search2.getCommentResults().get(0)
				.getContent());
	}

	@Test
	public void shouldSearchQuestionTags() {
		Question question1 = new Question(true, "question1", admin);
		question1.addTags("test");

		parser = new SearchQueryParser("test");
		Search search = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("test", search.getQuestions().get(0).getTags()
				.get(0));
	}

	@Test
	public void shouldSearchQuestionContent() {
		Post question1 = new Question(true, "question1", admin);

		parser = new SearchQueryParser("question1");
		Search search = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("question1", search.getQuestions().get(0)
				.getContent());
	}

	@Test
	public void shouldSearchAnswerContent() {
		Question question1 = new Question(true, "question1", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);

		parser = new SearchQueryParser("answer1");
		Search search = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("answer1", search.getAnswerContentResults().get(0)
				.getContent());

	}

	@Test
	public void shouldSearchComments() {
		Question question1 = new Question(true, "question1", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);

		Comment commentQuestion = new Comment(admin, question1,
				"comment To Question 1");
		Comment commentAnswer = new Comment(admin, answer1,
				"comment To Answer 1");

		parser = new SearchQueryParser("\"comment To Question 1\"");
		Search search = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("comment To Question 1", search.getCommentResults().get(0)
				.getContent());

		parser = new SearchQueryParser("\"comment To Answer 1\"");
		Search search1 = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());
		assertEquals("comment To Answer 1", search1.getCommentResults().get(0)
				.getContent());
	}

	@Test
	public void shouldAddNotingWhenNoMatches() {
		Question question1 = new Question(true, "question1", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);
		question1.addTags("test");
		Comment commentQuestion = new Comment(admin, question1,
				"commentToQuestion1");
		Comment commentAnswer = new Comment(admin, answer1, "commentToAnswer1");

		parser = new SearchQueryParser("nothing");
		Search search = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());

		assertEquals(0, search.getAnswerContentResults().size());
		assertEquals(0, search.getCommentResults().size());
		assertEquals(0, search.getQuestions().size());
	}

	@Test
	public void shouldAddQuestionOnlyOnceWhenMoreThanOneTagMatches() {
		Question question1 = new Question(true, "question1", admin);
		question1.addTags("testA");
		question1.addTags("test");

		Question question2 = new Question(true, "question2", admin);
		question2.addTags("testA");
		question2.addTags("test");

		parser = new SearchQueryParser("test");
		Search search = new Search(parser.getQueryWordsSoundex(),
				parser.getQuerySentences());

		assertEquals(2, question1.getTags().size());
		assertEquals(2, question2.getTags().size());
		assertEquals(2, search.getQuestions().size());

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
