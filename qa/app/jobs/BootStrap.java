package jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import models.Answer;
import models.Comment;
import models.DbManager;
import models.Post;
import models.Question;
import models.User;
import models.UserGroups;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class BootStrap extends Job {

	private static final Log log = LogFactory.getLog(BootStrap.class);
	private static Random random = new Random();
	private static DbManager manager = DbManager.getInstance();

	// boolean up= (random.nextInt(2) == 0 ? true : false);
	//
	// if(up)
	// post.rateUp(user);
	// else
	// post.rateDown(user);

	public void doJob() {
		Calendar cal = Calendar.getInstance();
		
		log.info("fill Model with test-data");

		User user = new User("admin", "admin@admin.ch", "admin");
		user.setGroup(UserGroups.admin);

		int users = 5;
		int questionsPerUser = 3;

		// create some users
		for (int i = 1; i <= users; i++) {
			User u = new User("user-" + i, "user-" + i + "@ese.ch", "user-" + i);
			// 5 questions per user
			for (int j = 1; j <= questionsPerUser; j++) {
				Post q = new Question(true, "question " + j, u);
				cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(380));
				q.setDate(cal.getTime());
				cal.setTime(new Date());
			}
		}

		ArrayList<Question> questions = manager.getQuestions();

		// add questions with tags
		Question q1 = new Question(true, "How small is the fish?", manager
				.getUserByName("user-1"));
		q1.addTags("fish size");
		q1.vote(1);
		Answer a11= new Answer(true, "see www.smallfish.com", manager.getUserByName("user-2"), q1);

		Question q2 = new Question(
				true, "How many roads must a man walk down before you can call him a man?",
				manager.getUserByName("user-5"));
		q2.addTags("man road");
		new Answer(true, "The answer my friend is blowin in the wind", manager
				.getUserByName("user-3"), q2);
		q1.vote(1);
		q1.vote(1);
		
		// add avatars
		File avatar1= new File("qa/public/images/avatars/casper.jpg");
		manager.getUserByName("user-1").setAvatar(avatar1);
		File avatar2= new File("qa/public/images/avatars/office.jpg");
		manager.getUserByName("user-2").setAvatar(avatar2);
		File avatar3= new File("qa/public/images/avatars/policeman.jpg");
		manager.getUserByName("user-3").setAvatar(avatar3);
		File avatar4= new File("qa/public/images/avatars/robinHood.jpg");
		manager.getUserByName("user-4").setAvatar(avatar4);
		File avatar5= new File("qa/public/images/avatars/soldier.jpg");
		manager.getUserByName("user-5").setAvatar(avatar5);
		
		// add comment
		new Comment(manager.getUserByName("user-4"), q2, "ask bob dylan, dude!");
		new Comment(manager.getUserByName("user-1"), a11, "thank you");
		
		//add Reputation for admin over 30 days
		int[] reputations = {13,23,28,33,37,40,43,46,49,52,54,57,59,61,64,66,68,70,72,73,75,77,79,80,82,84,85,87,88,90};
		
		for(int i = 0; i < 30; i++) {
			manager.addReputation(user, reputations[i]);
		}
		
		// an answer for every second question from the first user
		int i = 0;
		for (Question q : questions) {

			if (i++ % 2 == 0) {
				User u = manager.getUsers().get(0);
				Answer a = new Answer(true, "answer " + i, u, q);
				cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(380));
				a.setDate(cal.getTime());
				cal.setTime(new Date());
			} else {
				q.vote(-1);
			}
		}
		
		//a question just to test best answer
		Question goodQuestion = new Question(true, "This is actually a good question", manager.getUserByName("user-1"));
		Date aWeekAgo = new Date(goodQuestion.getDate().getTime()-604800000);
		goodQuestion.setDate(aWeekAgo);
		goodQuestion.vote(2);
		Answer stupidAnswer = new Answer(true, "This is a stupid answer", manager.getUserByName("user-2"), goodQuestion);
		Answer anotherstupidAnswer = new Answer(true, "This is just another stupid answer", manager.getUserByName("user-3"), goodQuestion);
		Answer goodAnswer = new Answer(true, "Finally a good answer", manager.getUserByName("user-4"), goodQuestion);
		goodAnswer.vote(3);
		Answer bestAnswer = new Answer(true, "This is the best answer ever", manager.getUserByName("user-5"), goodQuestion);
		bestAnswer.vote(7);
		Comment aComment = new Comment(manager.getUserByName("user-4"), bestAnswer, "Wow! Best answer, even better than mine");
		
		// set user-1 be moderator
		manager.getUserByName("user-1").setGroup(UserGroups.moderator);

	}

}
