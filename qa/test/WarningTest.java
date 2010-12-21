import models.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class WarningTest extends UnitTest {
	private static DbManager manager;
	private User problematicUser;
	private Question problematicPost, anotherProblematicPost;

	@Before
	public void setUp() {
		manager = DbManager.getInstance();
		problematicUser = new User("ass", "mail", "1234");
		problematicPost = new Question("some awful content", "offensiv",
				problematicUser);
		anotherProblematicPost = new Question("also awful content",
				"offensiv2", problematicUser);
	}

	@Test
	public void shouldBeEmptyAtBeginning() {
		assertTrue(manager.getWarnings().isEmpty());
	}

	@Test
	public void shouldGetWarning() {
		Warning warning = new Warning(problematicPost);
		assertEquals(1, manager.getWarnings().size());
		assertEquals(warning, manager.getWarnings().get(0));
	}

	@Test
	public void shouldBeCounterOnOne() {
		Warning warning = new Warning(anotherProblematicPost);
		assertEquals(1, warning.getWarningCounter());
	}

	@Test
	public void shouldClearWarnings() {
		new Warning(problematicPost);
		assertTrue(!manager.getWarnings().isEmpty());
		manager.clearWarnings();
		assertTrue(manager.getWarnings().isEmpty());
	}

	@Test
	public void shouldGetTwoWarnings() {
		manager.clearWarnings();
		new Warning(problematicPost);
		new Warning(anotherProblematicPost);
		assertEquals(2, manager.getWarnings().size());

	}

	@Test
	public void shouldNotDuplicateWarning() {
		manager.clearWarnings();
		new Warning(problematicPost);
		new Warning(problematicPost);
		assertEquals(1, manager.getWarnings().size());
	}

	@Test
	public void shouldIncrementWarningCounter() {
		manager.clearWarnings();
		Warning warning = new Warning(problematicPost);
		new Warning(problematicPost);
		assertEquals(2, warning.getWarningCounter());
	}

	@AfterClass
	public static void tearDown() {
		manager.clearWarnings();
	}
}
