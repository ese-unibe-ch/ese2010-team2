package controllers;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import models.Answer;
import models.DbManager;
import models.Post;
import models.Question;
import models.SearchManager;
import models.SearchResult;
import models.User;

import org.apache.commons.io.IOUtils;

import play.Play;
import play.cache.Cache;
import play.mvc.Controller;
import annotations.Unused;

/**
 * This Controller manages general actions about posts
 * like questions answers and comments. 
 */
public class PostController extends Controller {

	private static Calendar calendar = Calendar.getInstance();
	private static DbManager manager = DbManager.getInstance();
	
	public static void showVersionHistory(String type, String id) {
		Post post;
		int intId = Integer.parseInt(id);
		if (type.equals("questions") || type.equals("question")) {
			post = manager.getQuestionById(intId);
			type = "question";
			ArrayList<Post> history = post.getOldVersions();
			render(type, post, history);
		} else {
			post = manager.getAnswerById(intId);
			Post question = manager.getAnswerById(intId).getQuestion();
			ArrayList<Post> history = post.getOldVersions();
			render(type, post, history, question);
		}

	}

}
