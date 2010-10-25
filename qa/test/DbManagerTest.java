import java.util.ArrayList;
import java.util.NoSuchElementException;

import models.Answer;
import models.Comment;
import models.DbManager;
import models.Post;
import models.Question;
import models.User;

import org.hibernate.hql.ast.tree.ExpectedTypeAwareNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class DbManagerTest extends UnitTest {
	private static DbManager manager;
	private User admin;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@admin.ch", "admin");
	}

	@Test
	public void souldGetRecentQuestions() {
		Question newQuestion1 = new Question("question1", admin);
		Question newQuestion2 = new Question("question2", admin);

		assertEquals(1, manager.getRecentQuestionsByNumber(1).size());
		assertEquals(2, manager.getRecentQuestionsByNumber(2).size());
		assertTrue(manager.getRecentQuestionsByNumber(1).contains(newQuestion2));
		assertTrue(manager.getRecentQuestionsByNumber(2).contains(newQuestion1)
				&& manager.getRecentQuestionsByNumber(2).contains(newQuestion2));
	}

	@Test
	public void shouldGetUserLog() {
		User newUser = new User("user", "user@ese", "user");
		assertEquals(1, manager.getUserLog("user").size());

		Question newQuestion = new Question("question1", newUser);
		assertEquals(2, manager.getUserLog("user").size());
		assertEquals("Asked question <question1>", manager.getUserLog("user")
				.get(0));

		Answer newAnswer = new Answer("answer1", newUser, newQuestion);
		assertEquals(3, manager.getUserLog("user").size());
		assertEquals("Answered question <question1> by writing: <answer1>",
				manager.getUserLog("user").get(0));
	}

	@Test
	public void userNameShouldBeOccupied() {
		User user1 = new User("user1user1user1user1user1", "user@ese", "user1");
		assertTrue(manager.checkUserNameIsOccupied("user1user1user1user1user1"));
	}

	@Test
	public void shouldRemarkQuestionDuplication() {
		Question question1 = new Question("question1<question1<question1",
				admin);
		assertTrue(manager
				.checkQuestionDuplication("question1<question1<question1"));
	}

	@Test
	public void shouldGetAllAnswersToQuestion() {
		Question question1 = new Question("content", admin);
		Answer answer1 = new Answer("answer1", admin, question1);
		Answer answer2 = new Answer("answer2", admin, question1);
		Answer answer3 = new Answer("answer3", admin, question1);

		assertEquals(3, manager.getAllAnswersByQuestionId(question1.getId())
				.size());
		assertTrue(manager.getAllAnswersByQuestionId(question1.getId())
				.contains(answer3)
				&& manager.getAllAnswersByQuestionId(question1.getId())
						.contains(answer2)
				&& manager.getAllAnswersByQuestionId(question1.getId())
						.contains(answer1));
	}

	@Test
	public void shouldFindQuestionById() {
		Question question1 = new Question("question1", admin);
		assertEquals(question1, manager.getQuestionById(question1.getId()));
	}

	@Test
	public void shouldFindAnswerById() {
		Question q = new Question("content", admin);
		Answer answer1 = new Answer("answer1", admin, q);
		assertEquals(answer1, manager.getAnswerById(answer1.getId()));
	}

	@Test
	public void QuestionsShouldBeSortedByScore() {
		Question question1 = new Question("content1", admin);
		Question question2 = new Question("content2", admin);
		Question question3 = new Question("content3", admin);
		Question question4 = new Question("content4", admin);

		question1.vote(2);
		question2.vote(3);
		question3.vote(4);
		question4.vote(5);

		assertEquals(question4, manager.getQuestionsSortedByScore().get(0));
		assertEquals(question3, manager.getQuestionsSortedByScore().get(1));
		assertEquals(question2, manager.getQuestionsSortedByScore().get(2));
		assertEquals(question1, manager.getQuestionsSortedByScore().get(3));

	}

	@Test
	public void answersShouldBeSortedByScore() {
		Question question1 = new Question("content1", admin);

		Answer answer1 = new Answer("content1", admin, question1);
		Answer answer2 = new Answer("content2", admin, question1);
		Answer answer3 = new Answer("content3", admin, question1);
		Answer answer4 = new Answer("content4", admin, question1);

		answer1.vote(2);
		answer2.vote(3);
		answer3.vote(4);
		answer4.vote(5);

		assertEquals(answer4, manager
				.getAnswersSortedByScore(question1.getId()).get(0));
		assertEquals(answer3, manager
				.getAnswersSortedByScore(question1.getId()).get(1));
		assertEquals(answer2, manager
				.getAnswersSortedByScore(question1.getId()).get(2));
		assertEquals(answer1, manager
				.getAnswersSortedByScore(question1.getId()).get(3));
	}

	@Test
	public void shouldGetVotablesByUser() {
		Question question1 = new Question("question1", admin);
		Question question2 = new Question("question2", admin);
		Answer answer1 = new Answer("answer1", admin, question1);
		Answer answer2 = new Answer("answer2", admin, question2);

		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(
				question1));
		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(
				question2));
		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(answer1));
		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(answer2));
	}

	@Test
	public void shouldAddSingleTag() {
		// clear Tag-list in order to be able to count the total amount of tags
		// stored.
		manager.getTagList().clear();
		assertEquals(0, manager.getTagList().size());

		Question question1 = new Question("question1", admin);
		question1.addTags("Hello hello world Earth earth World");
		assertTrue(manager.getTagList().contains("hello"));
		assertTrue(manager.getTagList().contains("world"));
		assertTrue(manager.getTagList().contains("earth"));
		assertEquals(3, manager.getTagList().size());
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
		Question question = new Question("content of question", admin);
		Answer answer = new Answer("content of answer", admin, question);

		assertEquals("content of answer", manager.getAnswers().get(0)
				.getContent());
		assertEquals(admin.getName(), manager.getAnswers().get(0).getOwner()
				.getName());
		assertEquals(manager.getQuestions().get(0).getId(),
				(manager.getAnswers()).get(0).getQuestionId());
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
	public void shouldRemoveUser(){
		User user1= new User("user1", "user@1", "password");
		Question question1= new Question("Question to be deleted", user1);
		Question question2= new Question("question not to be deleted", admin);
		Answer answer1= new Answer("answer to be deleted", user1, question1);
		Comment comm1= new Comment(user1, answer1, "comment to be deleted");
		assertTrue(manager.getQuestions().contains(question1));
		assertTrue(manager.getAnswers().contains(answer1));
		assertTrue(manager.getComments().contains(comm1));
		manager.deleteUser("user1");
		assertFalse(manager.getQuestions().contains(question1));
		assertFalse(manager.getAnswers().contains(answer1));
		assertFalse(manager.getComments().contains(comm1));
		assertTrue(manager.getQuestions().contains(question2));
	}
	
	@Test
	public void shouldSortCommentsByDate() {
		Question question = new Question("some content", admin);
		Comment firstComment = new Comment(admin, question, "first comment");
		Comment secondComment = new Comment(admin, question, "second comment");
		Comment thirdComment = new Comment(admin, question, "third comment");
		ArrayList<Comment> comments = manager.getAllCommentsByQuestionIdSortedByDate(question.getId());
		assertEquals(firstComment, comments.get(0));
		assertEquals(secondComment, comments.get(1));
		assertEquals(thirdComment, comments.get(2));
	}
	
    @Test(expected=NoSuchElementException.class)
    public void shouldntFindUserToDelete() {
    	manager.deleteUser("userDoesNotExist");
    }


	@After
	public void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.getComments().clear();
		manager.getTagList().clear();
		manager.resetAllIdCounts();
	}
}
