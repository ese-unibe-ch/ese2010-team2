import java.util.ArrayList;

import models.Answer;
import models.DbManager;
import models.Post;
import models.Question;
import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class BasicTest extends UnitTest {
	private static DbManager manager;
	private User admin;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void shouldCreateIds() {

		User a = new User("a", "a@a.ch", "a");
		User b = new User("b", "b@b.ch", "b");
		User c = new User("c", "c@c.ch", "c");

		// admin has Id 0
		assertEquals(1, a.getId());
		assertEquals(2, b.getId());
		assertEquals(3, c.getId());

		Question question = new Question("content of question", admin);
		Question question1 = new Question("content of question1", admin);
		Question question2 = new Question("content of question2", admin);

		assertEquals(0, question.getId());
		assertEquals(1, question1.getId());
		assertEquals(2, question2.getId());

		Answer answer = new Answer("content of answer", admin, manager
				.getQuestions().get(0));
		Answer answer1 = new Answer("content of answer1", admin, manager
				.getQuestions().get(1));
		Answer answer2 = new Answer("content of answer2", admin, manager
				.getQuestions().get(2));

		assertEquals(0, answer.getId());
		assertEquals(1, answer1.getId());
		assertEquals(2, answer2.getId());

	}

	@Test
	public void aVeryImportantThingToTest() {
		assertEquals(2, 1 + 1);
	}

	@Test
	public void shouldCreateUser() {
		assertEquals("admin", manager.getUsers().get(0).getName());
		assertEquals("admin@admin.ch", manager.getUsers().get(0).getEmail());
		assertEquals("admin", manager.getUsers().get(0).getPassword());
	}

	@Test
	public void shouldCreateQuestion() {
		@SuppressWarnings("unused")
		Question question = new Question("content of question", admin);

		assertEquals("content of question", manager.getQuestions().get(0)
				.getContent());
		assertEquals(admin.getName(), manager.getQuestions().get(0).getOwner()
				.getName());
	}

	@Test
	public void shouldCreateAnswer() {
		@SuppressWarnings("unused")
		Answer answer = new Answer("content of answer", admin, manager
				.getQuestions().get(0));

		assertEquals("content of answer", manager.getAnswers().get(0)
				.getContent());
		assertEquals(admin.getName(), manager.getQuestions().get(0).getOwner()
				.getName());
		assertEquals(manager.getQuestions().get(0).getId(),
				(manager.getAnswers()).get(0).getQuestionId());
	}

	@Test
	public void shouldCheckUserAlreadyVotedQuestion() {
		Question question = new Question("content of question", admin);
		assertFalse(question.checkUserVotedForQuestion(admin));
		question.userVotedForQuestion(admin);
		assertTrue(question.checkUserVotedForQuestion(admin));
	}

	@Test
	public void shouldCheckUserAlreadyVotedAnswer() {
		Question question = new Question("content of question", admin);
		Answer answer = new Answer("content of answer", admin, question);
		assertFalse(answer.checkUserVotedForAnswer(admin));
		answer.userVotedForAnswer(admin);
		assertTrue(answer.checkUserVotedForAnswer(admin));
	}

	@Test
	public void shouldVoteQuestion() {
		Question question = new Question("content of question", admin);
		assertEquals(0, question.getScore());
		// Vote Up
		question.vote("1");
		assertEquals(1, question.getScore());
		// Vote down
		question.vote("-1");
		assertEquals(0, question.getScore());
	}

	@Test
	public void shouldVoteAnswer() {
		Question question = new Question("content of question", admin);
		Answer answer = new Answer("content of answer", admin, question);
		assertEquals(0, answer.getScore());
		// Vote Up
		answer.vote("1");
		assertEquals(1, answer.getScore());
		// Vote down
		answer.vote("-1");
		assertEquals(0, answer.getScore());
	}

	@Test
	public void shouldCheckUserDuplication() {
		assertTrue(manager.checkUserNameIsOccupied("admin"));
		assertFalse(manager.checkUserNameIsOccupied("noAlreadyRegistered"));
	}

	@Test
	public void shouldCheckQuestionDuplication() {
		@SuppressWarnings("unused")
		Question question = new Question("content of question", admin);
		assertTrue(manager.checkQuestionDuplication("content of question"));
		assertFalse(manager
				.checkQuestionDuplication("content of not registered question"));
	}

	@Test
	public void shouldGetUserByName() {
		assertEquals("admin", manager.getUserByName("admin").getName());
		assertNull(manager.getUserByName("notRegistered"));
	}

	@Test
	public void shouldGetQuestionById() {
		Question question = new Question("content of question", admin);
		assertEquals(question.getContent(),
				manager.getQuestionById(question.getId()).getContent());
		assertNull(manager.getQuestionById(-1));
	}

	@Test
	public void shouldGetAnswerById() {
		Question question = new Question("content of question", admin);
		Answer answer = new Answer("content of answer", admin, question);
		assertEquals(answer.getContent(), manager.getAnswerById(answer.getId())
				.getContent());
		assertNull(manager.getAnswerById(-1));
	}

	@Test
	public void shouldGetUsersVotablesById() {
		User user1 = new User("user1", "user@1", "password");
		User user2 = new User("user2", "user@2", "password");
		Question question = new Question("something", user1);
		Answer answer1 = new Answer("something", user2, question);
		Answer answer2 = new Answer("something", user1, question);
		ArrayList<Post> list = manager.getVotablesByUserId(user1.getId());
		assertEquals(2, list.size());
		assertTrue(list.contains(question));
		assertTrue(list.contains(answer2));
		assertFalse(list.contains(answer1));
	}

	@Test
	public void shouldGetRightScore() {
		User topScorer = new User("scorer", "user@champion", "password");
		Question question = new Question("Good Question", topScorer);
		Answer answer = new Answer("Good Answer", topScorer, question);
		question.vote("1");
		answer.vote("2");
		assertEquals(3, topScorer.getScore());
	}

	@Test
	public void shouldAddUserLog() {
		User logTester = new User("logTester", "test@log", "pw");
		logTester.addActivity("Activity1");
		logTester.addActivity("Activity2");
		assertEquals(3, logTester.getActivities().size());
		assertEquals("Activity2", logTester.getActivities().get(0));
	}

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
	}
}
