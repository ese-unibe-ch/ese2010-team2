import models.Answer;
import models.Comment;
import models.DbManager;
import models.Question;
import models.Search;
import models.User;
import models.algorithms.SearchQueryParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class SearchTest extends UnitTest {
	private static DbManager manager;
	private User admin;
	private SearchQueryParser parser;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void sentenceSearchShouldNotBeCaseSensitive() {
		Question question3 = new Question(true, "question1 is the best",
				"title", admin);
		new Answer(true, "answer1 is the best", admin, question3);
		new Comment(admin, question3, "kant is the best");

		parser = new SearchQueryParser("\"question1 is the best\"");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("question1 is the best", search.getQuestionResults()
				.get(0).getContent());

		parser = new SearchQueryParser("\"answer1 is the best\"");
		Search search1 = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("answer1 is the best", search1.getAnswerResults().get(0)
				.getContent());

		parser = new SearchQueryParser("\"kant is the best\"");
		Search search2 = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("kant is the best", search2.getCommentResults().get(0)
				.getContent());
	}

	@Test
	public void shouldSearchQuestionTags() {
		Question question1 = new Question(true, "question1", "title", admin);
		question1.addTags("test");

		// Only soundex based search will be done on tags
		parser = new SearchQueryParser("test");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("test", search.getQuestionResults().get(0).getTags()
				.get(0));
	}

	@Test
	public void shouldSearchQuestionContentSoundexBased() {
		new Question(true, "question1", "title", admin);

		parser = new SearchQueryParser("question1");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("question1", search.getQuestionResults().get(0)
				.getContent());
	}

	@Test
	public void shouldSearchQuestionContentSentenceBased() {
		new Question(true, "Should find only this phrase question1", "title",
				admin);

		parser = new SearchQueryParser(
				"\"Should find only this phrase question1\"");
		Search search1 = new Search(parser.getSoundexCodes(),
				parser.getSentences());

		assertEquals("Should find only this phrase question1", search1
				.getQuestionResults().get(0).getContent());
	}

	@Test
	public void shouldSearchAnswerContentSoundexBased() {
		Question question1 = new Question(true, "question1", "title", admin);
		new Answer(true, "answer1", admin, question1);

		parser = new SearchQueryParser("answer1");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());

		assertEquals("answer1", search.getAnswerResults().get(0).getContent());
	}

	@Test
	public void shouldSearchAnswerContentSentenceBased() {
		Question question1 = new Question(true, "question1", "title", admin);
		new Answer(true, "Search this text answer1", admin, question1);

		parser = new SearchQueryParser("\"Search this text answer1\"");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());

		assertEquals("Search this text answer1",
				search.getAnswerResults().get(0).getContent());
	}

	@Test
	public void shouldSearchCommentsSoundexBased() {
		Question question1 = new Question(true, "question1", "title", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);

		new Comment(admin, question1, "comment1");
		new Comment(admin, answer1, "blabla");

		parser = new SearchQueryParser("comment1");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("comment1", search.getCommentResults().get(0).getContent());

		parser = new SearchQueryParser("blabla");
		Search search1 = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("blabla", search1.getCommentResults().get(0).getContent());
	}

	@Test
	public void shouldSearchCommentsSentenceBased() {
		Question question1 = new Question(true, "question1", "title", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);

		new Comment(admin, question1, "comment To Question 1");
		new Comment(admin, answer1, "comment To Answer 1");

		parser = new SearchQueryParser("\"comment To Question 1\"");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("comment To Question 1", search.getCommentResults().get(0)
				.getContent());

		parser = new SearchQueryParser("\"comment To Answer 1\"");
		Search search1 = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		assertEquals("comment To Answer 1", search1.getCommentResults().get(0)
				.getContent());
	}

	@Test
	public void shouldAddNotingWhenNoMatches() {
		Question question1 = new Question(true, "question1", "title", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);
		question1.addTags("test");
		new Comment(admin, question1, "commentToQuestion1");
		new Comment(admin, answer1, "commentToAnswer1");

		parser = new SearchQueryParser("nothing");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());

		assertEquals(0, search.getAnswerResults().size());
		assertEquals(0, search.getCommentResults().size());
		assertEquals(0, search.getQuestionResults().size());
	}

	@Test
	public void shouldAddQuestionOnlyOnceWhenMoreThanOneTagMatches() {
		Question question1 = new Question(true, "question1", "title", admin);
		question1.addTags("testA");
		question1.addTags("test");

		Question question2 = new Question(true, "question2", "title", admin);
		question2.addTags("testA");
		question2.addTags("test");

		parser = new SearchQueryParser("test");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());

		assertEquals(2, question1.getTags().size());
		assertEquals(2, question2.getTags().size());
		assertEquals(2, search.getQuestionResults().size());

	}

	@After
	public void tearDown() {
		manager.getUsers().clear();
		manager.clearQuestionsMap();
		manager.clearAnswerMap();
		manager.resetAllIdCounts();
		manager.getComments().clear();
		manager.getTagList().clear();
	}
}
