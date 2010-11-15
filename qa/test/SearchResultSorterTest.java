import models.Answer;
import models.Comment;
import models.DbManager;
import models.Question;
import models.SearchManager;
import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class SearchResultSorterTest extends UnitTest {
	private User admin;
	private static DbManager manager;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void shouldCountTagAndContentMatchesBasedOnSoundexCodes() {
		Question question1 = new Question(true, "question1", admin);
		question1.addTags("question1");
		Answer answer1 = new Answer(true, "question1", admin, question1);
		Comment commentQuestion = new Comment(admin, question1, "question1");
		Comment commentAnswer = new Comment(admin, answer1, "question1");

		SearchManager searchManager = new SearchManager("question1");

		// 4 content count + 1 tag counts double
		assertEquals(6, searchManager.getSearchResults().get(0).getTotalCount());
	}

	@Test
	public void shouldCountContentMatchesBasedOnSentences() {
		Question question2 = new Question(true, "question1 is the best", admin);
		Answer answer2 = new Answer(true, "question1 is the best", admin,
				question2);
		Comment commentQuestion = new Comment(admin, question2,
				"question1 is the best");
		Comment commentAnswer = new Comment(admin, answer2,
				"question1 is the best");

		SearchManager searchManager = new SearchManager(
				"\"question1 is the best\"");

		assertEquals(4, searchManager.getSearchResults().get(0).getTotalCount());

	}

	@Test
	public void shouldCountScores() {
		Question question3 = new Question(true, "question1 is the best", admin);
		question3.vote(4);
		Answer answer3 = new Answer(true, "question1 is the best", admin,
				question3);
		answer3.vote(5);
		Comment commentQuestion = new Comment(admin, question3,
				"question1 is the best");
		Comment commentAnswer = new Comment(admin, answer3,
				"question1 is the best");

		SearchManager searchManager = new SearchManager(
				"\"question1 is the best\"");

		assertEquals(9, searchManager.getSearchResults().get(0).getTotalScore());
	}

	@Test
	public void shouldCheckIfHasABestAnswerAndIncreaseCountAndScoreFive() {
		Question question4 = new Question(true, "question1 is the best", admin);
		Answer answer4 = new Answer(true, "question1 is the best", admin,
				question4);
		question4.setBestAnswer(answer4);
		Comment commentQuestion = new Comment(admin, question4,
				"question1 is the best");
		Comment commentAnswer = new Comment(admin, answer4,
				"question1 is the best");

		SearchManager searchManager = new SearchManager(
				"\"question1 is the best\"");

		// 3 (content counts, content count in best answer will not be counted)
		// + 5 (has a best answer) = 9
		assertEquals(8, searchManager.getSearchResults().get(0).getTotalCount());

		assertEquals(5, searchManager.getSearchResults().get(0).getTotalScore());
	}

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
		manager.getComments().clear();
		manager.getTagList().clear();
	}
}
