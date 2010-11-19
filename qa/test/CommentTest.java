import models.Answer;
import models.Comment;
import models.DbManager;
import models.Like;
import models.Question;
import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class CommentTest extends UnitTest {
	private static DbManager manager;
	private User admin;
	private Question question;
	private Answer answer;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		admin = new User("admin", "admin@ese.ch", "admin");
		question = new Question(true, "just another stupid question", admin);
		answer = new Answer(true, "and a jet more stupid answer", admin,
				question);
	}

	@Test
	public void shouldhaveOwner() {
		Comment comment = new Comment(admin, question, "my first comment");
		assertEquals(admin, comment.getOwner());
	}

	@Test
	public void shouldCommentQuestion() {
		Comment comment = new Comment(admin, question, "another comment");
		assertEquals(question, comment.getCommentedPost());
		assertEquals(comment, manager.getAllCommentsByQuestionIdSortedByDate(
				question.getId()).get(0));
	}

	@Test
	public void shouldCommentAnswer() {
		Comment comment = new Comment(admin, answer, "one more comment");
		assertEquals(answer, comment.getCommentedPost());
		assertEquals(comment, manager.getAllCommentsByAnswerIdSortedByDate(
				answer.getId()).get(0));
	}

	@Test
	public void shouldLikeComment() {
		User user = new User("Test", "test@test.com", "test");
		Comment comment = new Comment(admin, question, "new comment");
		Like like = new Like(user, comment);
		assertNotNull(like);
		assertEquals(user, like.getUser());
		assertEquals(comment, like.getComment());

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
