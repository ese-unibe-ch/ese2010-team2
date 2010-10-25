import models.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
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
		question = new Question("just another stupid question", admin);
		answer = new Answer("and a jet more stupid answer", admin, question);
	}

	@Test
	public void shouldhaveOwner() {
		Comment comment = new Comment(admin, question, "my first comment");
		assertEquals(admin, comment.getOwner());
	}
	
	@Test
	public void shouldCommentQuestion() {
		Comment comment = new Comment(admin, question, "another comment");
		assertEquals(question, comment.getCommentedVotable());
		assertEquals(comment, manager.getAllCommentsByQuestionIdSortedByDate(question.getId()).get(0));
	}

	@Test
	public void shouldCommentAnswer() {
		Comment comment = new Comment(admin, answer, "one more comment");
		assertEquals(answer, comment.getCommentedVotable());
		assertEquals(comment, manager.getAllCommentsByAnswerIdSortedByDate(answer.getId()).get(0));
	}

	@AfterClass
	public static void tearDown() {
		manager.getUsers().clear();
		manager.getQuestions().clear();
		manager.getAnswers().clear();
		manager.resetAllIdCounts();
		manager.getComments().clear();
	}

}
