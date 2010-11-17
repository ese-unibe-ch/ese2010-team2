import models.Answer;
import models.DbManager;
import models.Post;
import models.Question;
import models.User;
import models.Vote;

import org.junit.AfterClass;
import org.junit.Before;
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
		question = new Question(true, "what", u);
		a1 = new Answer(true, "hah", u, question);
		a2 = new Answer(true, "hah", u, question);
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void shouldNotAllowToSetArbitrayAnswerAsBestAnswer() {
		Answer a3 = new Answer(true, "haha", u, new Question(true, "", u));
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
		assertEquals(0, DbManager.getTagList().size());

		question.addTags("hello world planet earth");
		assertEquals(4, question.getTags().size());

		// Size after adding 4 tags should now incremented by 4
		assertEquals(4, DbManager.getTagList().size());

		assertTrue(question.getTags().contains("hello"));
		assertTrue(question.getTags().contains("world"));
		assertTrue(question.getTags().contains("planet"));
		assertTrue(question.getTags().contains("earth"));

	}

	@Test
	public void shouldCheckUserAlreadyVotedQuestion() {
		Post question = new Question(true, "content of question", admin);
		assertFalse(question.checkUserVotedForPost(admin));
		question.userVotedForPost(admin);
		assertTrue(question.checkUserVotedForPost(admin));
	}

	@Test
	public void shouldVoteQuestion() {
		Post question = new Question(true, "content of question", admin);
		assertEquals(0, question.getScore());
		// Vote Up
		Vote vote1 = new Vote(question, 1, admin);
		question.vote(vote1, admin);
		assertEquals(1, question.getScore());
		// Vote down
		Vote vote2 = new Vote(question, -1, admin);
		question.vote(vote2, admin);
		assertEquals(0, question.getScore());
	}

	@Test
	public void shouldAddVersionAndRestore(){
		Question q = new Question(true, "question", admin);
		q.addVersion("hallo velo", "hallo velo", "admin");
		assertEquals(1, q.getOldVersions().size());
		q.restoreOldVersion("question", "hallo velo", "admin");
		assertEquals(2, q.getOldVersions().size());
		assertEquals("question", q.getContent());
		assertTrue(q.getTags().contains("hallo"));
		assertTrue(q.getTags().contains("velo"));
		assertEquals(2,q.getTags().size());
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
