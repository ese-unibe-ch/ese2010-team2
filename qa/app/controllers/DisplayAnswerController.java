package controllers;

import models.Answer;
import models.DbManager;
import models.Question;
import play.mvc.Controller;

/**
 * This Controller manages the view of answers
 */
public class DisplayAnswerController extends Controller {

	private static DbManager manager = DbManager.getInstance();

	/**
	 * 
	 * @param qid
	 * @param aid
	 */
	public static void setBestAnswer(int qid, int aid) {
		Question q = manager.getQuestionById(qid);
		Answer a = manager.getAnswerById(aid);

		q.setBestAnswer(a);

		DisplayQuestionController.showAnswers(Integer.toString(qid), "");
	}

}
