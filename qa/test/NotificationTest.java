import models.Answer;
import models.DbManager;
import models.Question;
import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class NotificationTest extends UnitTest {
	private User notifiedUser;
	private Question changingQuestion;
	private static DbManager manager;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		notifiedUser = new User("notifiedUser", "note.user@ese.ch", "1234");
		changingQuestion = new Question(true, "question", "title", notifiedUser);
	}

	@Test
	public void shouldNotifiyChange() {
		new Answer(true, "answer", notifiedUser, changingQuestion);
		assertTrue(!notifiedUser.getAllNotifications().isEmpty());
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
