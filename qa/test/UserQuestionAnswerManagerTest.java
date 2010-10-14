import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import models.Answer;
import models.Question;
import models.User;
import models.UserQuestionAnswerManager;
import play.test.UnitTest;

public class UserQuestionAnswerManagerTest extends UnitTest {
	private static UserQuestionAnswerManager manager;
	private User admin;

	@Before
	public void setUp() {
		manager = UserQuestionAnswerManager.getInstance();
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
	public void QuestionsShouldBeSortedByScore(){
		Question question1= new Question("content1", admin);
		Question question2= new Question("content2", admin);
		Question question3= new Question("content3", admin);
		Question question4= new Question("content4", admin);
		
		question1.vote("2");
		question2.vote("3");
		question3.vote("4");
		question4.vote("5");
		
		assertEquals(question4, manager.getQuestionsSortedByScore().get(0));
		assertEquals(question3, manager.getQuestionsSortedByScore().get(1));
		assertEquals(question2, manager.getQuestionsSortedByScore().get(2));
		assertEquals(question1, manager.getQuestionsSortedByScore().get(3));
		
		
	}
	
	@Test 
	public void answersShouldBeSortedByScore(){
		Question question1= new Question("content1", admin);
		
		Answer answer1= new Answer("content1", admin, question1);
		Answer answer2= new Answer("content2", admin, question1);
		Answer answer3= new Answer("content3", admin, question1);
		Answer answer4= new Answer("content4", admin, question1);
		
		answer1.vote("2");
		answer2.vote("3");
		answer3.vote("4");
		answer4.vote("5");
		
		assertEquals(answer4, manager.getAnswersSortedByScore(question1.getId()).get(0));
		assertEquals(answer3, manager.getAnswersSortedByScore(question1.getId()).get(1));
		assertEquals(answer2, manager.getAnswersSortedByScore(question1.getId()).get(2));
		assertEquals(answer1, manager.getAnswersSortedByScore(question1.getId()).get(3));
	}
	
	@Test
	public void shouldGetVotablesByUser(){
		Question question1= new Question("question1", admin);
		Question question2= new Question("question2",admin);
		Answer answer1=new Answer("answer1", admin, question1);
		Answer answer2=new Answer("answer2",admin,question2);
		
		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(question1));
		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(question2));
		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(answer1));
		assertTrue(manager.getVotablesByUserId(admin.getId()).contains(answer2));
	}
	
	@Test
	public void shouldAddSingleTag(){
		Question question1= new Question("question1",admin);
		question1.addTags("Tag1 Tag2 Tag3");
		assertTrue(manager.getTagList().contains("Tag1"));
		assertTrue(manager.getTagList().contains("Tag2"));
		assertTrue(manager.getTagList().contains("Tag3"));
	}
	
	@AfterClass
	public static void tearDown(){
		//Cleaning up so next tests will not fail.
		manager.getUsers().clear();
		System.out.println(manager.getUsers().size());
		manager.getQuestions().clear();
		manager.getAnswers().clear();
	}
}
