import models.Answer;
import models.Comment;
import models.DbManager;
import models.Question;
import models.Search;
import models.SearchResult;
import models.User;
import models.algorithms.SearchQueryParser;
import models.algorithms.SearchResultAssembler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class SearchResultAssemblerTest extends UnitTest {
	private User admin;
	private static DbManager manager;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void shouldAssembleBasedOnAAnswer() {
		Question question1 = new Question(true, "question1", admin);
		Answer answer1 = new Answer(true, "Test1", admin, question1);
		new Answer(true, "blabla", admin, question1);
		new Comment(admin, question1, "comment1");
		new Comment(admin, answer1, "comment2");

		// Init Parser and Search
		SearchQueryParser parser = new SearchQueryParser("Test1");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());

		// Be sure only a answer was found by search
		assertEquals(0, search.getQuestionResults().size());
		assertEquals(0, search.getCommentResults().size());
		assertEquals(1, search.getAnswerResults().size());

		// Check that the answer was found
		assertEquals("Test1", search.getAnswerResults().get(0).getContent());

		SearchResultAssembler assembler = new SearchResultAssembler(
				search.getAnswerResults(), search.getCommentResults(),
				search.getQuestionResults());

		// Check if assembling is correct
		assertEquals(1, assembler.getSearchResults().size());
		SearchResult result = assembler.getSearchResults().get(0);
		assertEquals("question1", result.getQuestion().getContent());
		assertEquals("Test1", result.getAnswers().get(0).getContent());
		assertEquals("blabla", result.getAnswers().get(1).getContent());
		assertEquals("comment2", result.getComments().get(0).getContent());
		assertEquals("comment1", result.getComments().get(1).getContent());

		// Check everything was assembled
		assertEquals(0, assembler.getAnswerResults().size());
		assertEquals(0, assembler.getCommentResults().size());
	}

	@Test
	public void shouldAssembleBasedOnAQuestion() {
		Question question1 = new Question(true, "question1Test", admin);
		Answer answer1 = new Answer(true, "answer1Test", admin, question1);
		new Answer(true, "answer2Test", admin, question1);
		new Comment(admin, question1,
				"commentQuestion1");
		new Comment(admin, answer1, "commentAnswer1");

		// Init Parser Search and Assembler
		SearchQueryParser parser = new SearchQueryParser("question1Test");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());
		SearchResultAssembler assembler = new SearchResultAssembler(
				search.getAnswerResults(), search.getCommentResults(),
				search.getQuestionResults());

		// Be sure only a question was found by search
		assertEquals(1, search.getQuestionResults().size());
		assertEquals(0, search.getCommentResults().size());
		assertEquals(0, search.getAnswerResults().size());

		// Check that the right question was found
		assertEquals("question1Test", search.getQuestionResults().get(0)
				.getContent());

		// Check if assembling is correct
		assertEquals(1, assembler.getSearchResults().size());
		SearchResult result = assembler.getSearchResults().get(0);
		assertEquals("question1Test", result.getQuestion().getContent());
		assertEquals("answer1Test", result.getAnswers().get(0).getContent());
		assertEquals("answer2Test", result.getAnswers().get(1).getContent());
		assertEquals("commentAnswer1", result.getComments().get(0).getContent());
		assertEquals("commentQuestion1", result.getComments().get(1)
				.getContent());

		// Check everthing was assembled
		assertEquals(0, assembler.getAnswerResults().size());
		assertEquals(0, assembler.getCommentResults().size());

	}

	@Test
	public void shouldAssembleBasedOnAComment() {
		Question question1 = new Question(true, "question1", admin);
		Answer answer1 = new Answer(true, "Test1", admin, question1);
		new Answer(true, "Test2", admin, question1);
		new Comment(admin, question1, "schopenhauer");
		new Comment(admin, answer1, "blabla");

		// Init parser and search
		SearchQueryParser parser = new SearchQueryParser("schopenhauer");
		Search search = new Search(parser.getSoundexCodes(),
				parser.getSentences());

		// Be sure only a comment was found by search
		assertEquals(0, search.getQuestionResults().size());
		assertEquals(1, search.getCommentResults().size());
		assertEquals(0, search.getAnswerResults().size());

		// Check that the right comment was found
		assertEquals("schopenhauer", search.getCommentResults().get(0)
				.getContent());

		SearchResultAssembler assembler = new SearchResultAssembler(
				search.getAnswerResults(), search.getCommentResults(),
				search.getQuestionResults());

		// Check if assembling is correct
		assertEquals(1, assembler.getSearchResults().size());
		SearchResult result = assembler.getSearchResults().get(0);
		assertEquals("question1", result.getQuestion().getContent());
		assertEquals("Test1", result.getAnswers().get(0).getContent());
		assertEquals("Test2", result.getAnswers().get(1).getContent());
		assertEquals("blabla", result.getComments().get(0).getContent());
		assertEquals("schopenhauer", result.getComments().get(1).getContent());

		// Check everthing was assembled
		assertEquals(0, assembler.getAnswerResults().size());
		assertEquals(0, assembler.getCommentResults().size());
	}

	@After
	public void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
		manager.getComments().clear();
		manager.getTagList().clear();
	}
}
