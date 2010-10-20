package jobs;

import java.util.ArrayList;
import java.util.Random;

import models.Answer;
import models.DbManager;
import models.Question;
import models.User;

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

		log.info("fill Model with test-data");

		User user = new User("admin", "admin@admin.ch", "admin");

		int users = 5;
		int questionsPerUser = 5;

		// create some users
		for (int i = 1; i <= users; i++) {
			User u = new User("user-" + i, "user-" + i + "@ese.ch", "user-" + i);
			// // 5 questions per user
			// for (int j = 1; j <= questionsPerUser; j++) {
			// Question q = new Question("question " + j, u);
			// }
		}

		ArrayList<Question> questions = manager.getQuestions();

		// add questions with tags
		Question q1 = new Question("How small is the fish?", manager
				.getUserByName("user-1"));
		q1.addTags("fish size");
		q1.vote("1");
		new Answer("as big as the fisherman isn't yet", manager
				.getUserByName("user-2"), q1);

		Question q2 = new Question(
				"How many roads must a man walk down before you can call him a man?",
				manager.getUserByName("user-5"));
		q2.addTags("man road");
		new Answer("The answer my friend is blowin in the wind", manager
				.getUserByName("user-3"), q2);
		q1.vote("1");
		q1.vote("1");

		// an answer for every second question from the first user
		int i = 0;
		for (Question q : questions) {

			if (i++ % 2 == 0) {
				User u = manager.getUsers().get(0);
				Answer a = new Answer("answer " + i, u, q);
			} else {
				q.vote("-1");
			}
		}

	}

}
