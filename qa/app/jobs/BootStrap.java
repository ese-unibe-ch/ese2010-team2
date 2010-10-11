package jobs;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import models.Answer;
import models.Question;
import models.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import play.cache.Cache;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class BootStrap extends Job {
    
		private static final Log log = LogFactory.getLog(BootStrap.class);
		private static Random random = new Random();
		
		
//		boolean up= (random.nextInt(2) == 0 ? true : false);
//  		
//		if(up)
//  			post.rateUp(user);
//  		else
//  			post.rateDown(user); 
			
			

	
		
    public void doJob() {
    	
    	log.info("fill Model with test-data");
    	
    	int users = 5;
    	int questionsPerUser = 5;
    	
    	//create some users
    	for(int i=1;  i <= users ; i++){
    		User u = new User("user-"+i, "user-"+i+"@ese.ch", "user-"+i);
    		//5 questions per user
    		for(int j = 1; j <= questionsPerUser; j++){
        		Question q = new Question("question "+j, u);
        	}	
    	}
    	
//    	Collection<Question> questions = Question.findAll();
//    	
//    	//an answer for every second question, from a random user
//    	int i = 0;
//    	for(Question q : questions){    		    		
//    		
//    		if( i++ % 2 == 0){
//    			User u = User.findById(random.nextInt(users)+1);
//    			Answer a = new Answer(u, "Answer to question "+q.getId());
//    			q.addAnswer(a);
//    			randomVote(a, u);
//    			randomVote(q, u);
//    		}
//    	}
        
    }
    
}
