import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import models.*;

public class NotificationTest extends UnitTest {
	User notifiedUser;
	Question changingQuestion;
	
	@Before
	public void setUp() {
		notifiedUser = new User("notifiedUser", "note.user@ese.ch", "1234");
		changingQuestion = new Question(true, "question", notifiedUser);
	}
	
	@Test
	public void shouldNotifiyChange() {
		Answer answer = new Answer(true, "answer", notifiedUser, changingQuestion);
		assertTrue(!notifiedUser.getAllNotifications().isEmpty());
	}
}
