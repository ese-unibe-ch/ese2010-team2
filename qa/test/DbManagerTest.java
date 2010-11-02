import java.util.ArrayList;
import java.util.Date;
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
import org.junit.Ignore;
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
	public void shoulfindCommentbyId(){
		Question Question1 = new Question(true, "question1", admin);
		Answer answer1 = new Answer(true, "answer1", admin, Question1);
		Comment comment1 = new Comment(admin, Question1, "hallo1");
		Comment comment2 = new Comment(admin, Question1, "hallo2");
		Comment comment3 = new Comment(admin, answer1, "hallo3");
		assertTrue(manager.getComments().size()==3);
		assertEquals(comment1, manager.getCommentById(comment1.getId()));
		assertEquals(comment3, manager.getCommentById(comment3.getId()));
		assert(manager.getCommentById(comment2.getId()).getContent()=="hallo2");
		assert(manager.getCommentById(comment3.getId()).getContent()=="hallo3");
	}
	
	@Test
	public void shouldDeleteComment(){
		Question Question1 = new Question(true, "question1", admin);
		Comment comment = new Comment(admin, Question1, "hallo");
		
		assert(manager.getComments().size()>0&&manager.getCommentById(0).equals(comment));
		assertEquals(comment, Question1.getCommentbyId(comment.getId()));
		manager.deleteComment(comment);
		assert(manager.getComments().size()==0);
	}	
	
	@Test
	public void shouldDeleteAnswerandhisComments(){
		Question question1 = new Question(true, "question1", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);
		Comment comment = new Comment(admin, answer1, "hallo");
		Comment comment2 = new Comment(admin, question1, "hallo2");
		
		assertEquals(comment, answer1.getCommentbyId(comment.getId()));
		assertFalse(comment2==answer1.getCommentbyId(comment2.getId()));
		assertEquals(answer1, manager.getAnswerById(answer1.getId()));
		assertTrue(manager.getAnswers().size()==1);
		assertTrue(manager.getComments().size()==2);
		
		manager.deleteAnswer(answer1);
		
		assertTrue(manager.getAnswers().size()==0);
		assertTrue(manager.getComments().size()==1);
	}
	
	@Ignore
	public void shoulDeleteQuestionandallhisPost(){
		Question question1 = new Question(true, "question1", admin);
		Question question2 = new Question(true, "question2", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);
		Answer answer2 = new Answer(true, "answer1", admin, question2);
		Comment comment = new Comment(admin, answer1, "hallo");
		Comment comment2 = new Comment(admin, question1, "hallo2");
		Comment comment3 = new Comment(admin, question1, "hallo3");
		
		assertEquals(question1, manager.getQuestionById(question1.getId()));
		assertTrue(manager.getQuestions().size()==2);
		assertTrue(manager.getComments().size()==3);
		assertTrue(manager.getAnswers().size()==2);
		assertTrue(question1.getComments().size()==2);
		assertEquals(comment3, question1.getCommentbyId(comment3.getId()));
		assertEquals(comment2.getCommentedPost(), question1);
		assertEquals(comment3.getCommentedPost(), question1);
		assertFalse(question1.getCommentbyId(comment.getId())==comment);
		assertFalse(question1.getComments().contains(comment));
		
		manager.deleteQuestion(question1);
		
		assertTrue(manager.getQuestions().size()==1);
		assertTrue(manager.getComments().isEmpty());
		assertTrue(manager.getAnswers().size()==1);
		assertFalse(manager.getComments().contains(comment3));
		assertFalse(manager.getComments().contains(comment2));
		assertFalse(manager.getComments().contains(comment));
	}

	@Test
	public void souldGetRecentQuestions() {
		Post newQuestion1 = new Question(true, "question1", admin);
		Post newQuestion2 = new Question(true, "question2", admin);

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

		Question newQuestion = new Question(true, "question1", newUser);
		assertEquals(2, manager.getUserLog("user").size());
		assertEquals("Asked question <question1>", manager.getUserLog("user")
				.get(0));

		Answer newAnswer = new Answer(true, "answer1", newUser, newQuestion);
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
		Post question1 = new Question(true, "question1<question1<question1",
				admin);
		assertTrue(manager
				.checkQuestionDuplication("question1<question1<question1"));
	}

	@Test
	public void shouldGetAllAnswersToQuestion() {
		Question question1 = new Question(true, "content", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);
		Answer answer2 = new Answer(true, "answer2", admin, question1);
		Answer answer3 = new Answer(true, "answer3", admin, question1);

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
		Post question1 = new Question(true, "question1", admin);
		assertEquals(question1, manager.getQuestionById(question1.getId()));
	}

	@Test
	public void shouldFindAnswerById() {
		Question q = new Question(true, "content", admin);
		Answer answer1 = new Answer(true, "answer1", admin, q);
		assertEquals(answer1, manager.getAnswerById(answer1.getId()));
	}

	@Test
	public void QuestionsShouldBeSortedByScore() {
		Post question1 = new Question(true, "content1", admin);
		Post question2 = new Question(true, "content2", admin);
		Post question3 = new Question(true, "content3", admin);
		Post question4 = new Question(true, "content4", admin);

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
		Question question1 = new Question(true, "content1", admin);

		Answer answer1 = new Answer(true, "content1", admin, question1);
		Answer answer2 = new Answer(true, "content2", admin, question1);
		Answer answer3 = new Answer(true, "content3", admin, question1);
		Answer answer4 = new Answer(true, "content4", admin, question1);

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
	public void shouldGetQuestionsSortedByDate() {
		Question firstQuestion = new Question(true, "", admin);
		Question secondQuestion = new Question(true, "", admin);
		ArrayList<Question> list = manager.getQuestionsSortedByDate();
		assertTrue(list.indexOf(firstQuestion) < list.indexOf(secondQuestion));
	}
	
	@Test
	public void shouldGetAnswersSortedByDate() {
		Question question = new Question(true,"",admin);
		Answer firstAnswer = new Answer(true, "", admin, question);
		Answer secondAnswer = new Answer(true, "", admin, question);
		ArrayList<Answer> list = manager.getAnswersSortedByDate();
		assertTrue(list.indexOf(firstAnswer) < list.indexOf(secondAnswer));
	}
	
	@Test
	public void shouldGetQuestionsByUserSortedByDate() {
		User user = new User("", "", "");
		Question firstQuestion = new Question(true, "", user);
		Question secondQuestion = new Question(true, "", user);
		ArrayList<Question> list = manager.getQuestionsByUserIdSortedByDate(user.getId());
		assertEquals(2,list.size());
		assertEquals(firstQuestion,list.get(0));
		assertEquals(secondQuestion,list.get(1));
	}
	
	@Test
	public void shouldGetAnswersByUserSortedByDate() {
		User user = new User("", "", "");
		Question question = new Question(true,"",admin);
		Answer firstAnswer = new Answer(true, "", user, question);
		Answer secondAnswer = new Answer(true, "", user, question);
		ArrayList<Answer> list = manager.getAnswersByUserIdSortedByDate(user.getId());
		assertEquals(2,list.size());
		assertEquals(firstAnswer,list.get(0));
		assertEquals(secondAnswer,list.get(1));
	}
	
	@Test
	public void shouldGetVotablesByUser() {
		Question question1 = new Question(true, "question1", admin);
		Question question2 = new Question(true, "question2", admin);
		Answer answer1 = new Answer(true, "answer1", admin, question1);
		Answer answer2 = new Answer(true, "answer2", admin, question2);

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

		Question question1 = new Question(true, "question1", admin);
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

		Post question = new Question(true, "content of question", admin);
		Post question1 = new Question(true, "content of question1", admin);
		Post question2 = new Question(true, "content of question2", admin);

		assertEquals(0, question.getId());
		assertEquals(1, question1.getId());
		assertEquals(2, question2.getId());

		Answer answer = new Answer(true, "content of answer", admin, manager
				.getQuestions().get(0));
		Answer answer1 = new Answer(true, "content of answer1", admin, manager
				.getQuestions().get(1));
		Answer answer2 = new Answer(true, "content of answer2", admin, manager
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
		Post question = new Question(true, "content of question", admin);

		assertEquals("content of question", manager.getQuestions().get(0)
				.getContent());
		assertEquals(admin.getName(), manager.getQuestions().get(0).getOwner()
				.getName());
	}

	@Test
	public void shouldCreateAnswer() {
		@SuppressWarnings("unused")
		Question question = new Question(true, "content of question", admin);
		Answer answer = new Answer(true, "content of answer", admin, question);

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
		Post question = new Question(true, "content of question", admin);
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
		Post question = new Question(true, "content of question", admin);
		assertEquals(question.getContent(),
				manager.getQuestionById(question.getId()).getContent());
		assertNull(manager.getQuestionById(-1));
	}

	@Test
	public void shouldGetAnswerById() {
		Question question = new Question(true, "content of question", admin);
		Answer answer = new Answer(true, "content of answer", admin, question);
		assertEquals(answer.getContent(), manager.getAnswerById(answer.getId())
				.getContent());
		assertNull(manager.getAnswerById(-1));
	}

	@Test
	public void shouldGetUsersVotablesById() {
		User user1 = new User("user1", "user@1", "password");
		User user2 = new User("user2", "user@2", "password");
		Question question = new Question(true, "something", user1);
		Answer answer1 = new Answer(true, "something", user2, question);
		Answer answer2 = new Answer(true, "something", user1, question);
		ArrayList<Post> list = manager.getVotablesByUserId(user1.getId());
		assertEquals(2, list.size());
		assertTrue(list.contains(question));
		assertTrue(list.contains(answer2));
		assertFalse(list.contains(answer1));
	}
	
	@Test
	public void shouldRemoveUser(){
		User user1= new User("user1", "user@1", "password");
		Question question1= new Question(true, "Question to be deleted", user1);
		Post question2= new Question(true, "question not to be deleted", admin);
		Answer answer1= new Answer(true, "answer to be deleted", user1, question1);
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
		Post question = new Question(true, "some content", admin);
		Comment firstComment = new Comment(admin, question, "first comment");
		Comment secondComment = new Comment(admin, question, "second comment");
		Comment thirdComment = new Comment(admin, question, "third comment");
		ArrayList<Comment> comments = manager.getAllCommentsByQuestionIdSortedByDate(question.getId());
		assertEquals(firstComment, comments.get(0));
		assertEquals(secondComment, comments.get(1));
		assertEquals(thirdComment, comments.get(2));
	}
	
	@Test
	public void shouldCorrectlyAddAndAccessReputation() {
		User reputatedUser = new User("user", "user@ese.ch", "user");
		manager.addReputation(reputatedUser, 50);
		assertEquals(50, manager.getReputationByUserAndDate(reputatedUser, new Date()));
	}
	
	@Test
	public void shouldCorrectlyAccessReputationsOfTheLast5Days() {
		User reputatedUser = new User("user", "user@ese.ch", "user");
		int[] reputations = {10,20,30,40,50};
		for(int i = 0; i < 5; i++) {
			manager.addReputation(reputatedUser, reputations[i]);
		}
		ArrayList<Integer> reps = manager.getReputations(reputatedUser, 5);
		assertEquals((Integer)50, reps.get(0));
		assertEquals((Integer)40, reps.get(1));
		assertEquals((Integer)30, reps.get(2));
		assertEquals((Integer)20, reps.get(3));
		assertEquals((Integer)10, reps.get(4));
	}
	
    @Test(expected=NoSuchElementException.class)
    public void shouldntFindUserToDelete() {
    	manager.deleteUser("userDoesNotExist");
    }
    
    @Test
    public void shouldAnonymizeEditedByList(){
    	User u1= new User("u1","u@u","u");
    	User u2= new User("u2","u@u","u");
    	
    	Question question= new Question("question", admin);
    	Question q=manager.getQuestionById(question.getId());
    	q.addVersion("question edited", "edited", "u1");
    	q.addVersion("ques", "edited ques", "u2");
    	q.addVersion("quess", "", "admin");
    	
    	Answer answer= new Answer("answer", u1, q);
    	Answer a=manager.getAnswerById(answer.getId());
    	a.addVersion("djs", "u1");
    	a.addVersion("version2","u2");
    	
    	manager.deleteUser("u2");
    	
    	assertTrue(q.getEditors().contains(manager.getUserByName("u1")));
    	assertTrue(q.getEditors().contains(admin));
    	assertFalse(q.getEditors().contains(manager.getUserByName("u2")));
    	assertTrue(q.getEditors().contains(manager.getUserByName("anonymous")));
    	
    	assertTrue(a.getEditors().contains(manager.getUserByName("u1")));
    	assertFalse(a.getEditors().contains(manager.getUserByName("u2")));
    	assertTrue(a.getEditors().contains(manager.getUserByName("anonymous")));
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
