package models;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.codec.language.Soundex;

/**
 * This class implents the feature of fulltext and tag search for Question,
 * Answer, Comments
 */
public class Search {
	private ArrayList<String> queryWordsSoundex;
	private ArrayList<String> querySentences;
	private DbManager manager;
	private boolean doASoundexSearch;
	private Soundex soundex;

	/**
	 * Determines the number of questions, answer and comments in DbMangers
	 * ArrayLists
	 */
	private int numberOfQuestions;
	private int numberOfAnswers;
	private int numberOfComments;

	/** Store the results of the search */
	private ArrayList<Answer> answerContentResults;
	private ArrayList<Comment> commentResults;
	private ArrayList<Question> questions;

	public Search(ArrayList<String> queryWordsSoundex, ArrayList<String> querySentences) {
		this.queryWordsSoundex = queryWordsSoundex;
		this.querySentences = querySentences;
		this.manager = DbManager.getInstance();
		doASoundexSearch = false;
		soundex = new Soundex();

		answerContentResults = new ArrayList<Answer>();
		commentResults = new ArrayList<Comment>();
		questions = new ArrayList<Question>();

		numberOfQuestions = manager.getQuestions().size();
		numberOfAnswers = manager.getAnswers().size();
		numberOfComments = manager.getComments().size();

		doTheSearchForEveryQuery();
	}

	private void doTheSearchForEveryQuery() {
		doASoundexSearch = true;
		for (int i = 0; i < queryWordsSoundex.size(); i++) {
			String query = queryWordsSoundex.get(i);
			searchQuestionTags(query);
			searchQuestionContent(query);
			searchAnswerContent(query);
			searchComments(query);
		}

		doASoundexSearch = false;
		for (int i = 0; i < querySentences.size(); i++) {
			String query = querySentences.get(i);
			searchQuestionContent(query);
			searchAnswerContent(query);
			searchComments(query);
		}
		removeDuplicatedQuestions();

	}

	/** Search through all question tags for search query matches */
	public void searchQuestionTags(String query) {
		// Prevents that if a search query matches two different tags the
		// question will be added twice.
		boolean addQuestionOnlyOnce = true;

		for (int i = 0; i < numberOfQuestions; i++) {

			Question curQuestion = manager.getQuestions().get(i);
			int numberOfTags = curQuestion.getTags().size();

			for (int j = 0; j < numberOfTags; j++) {
				String curTag = curQuestion.getTagByIndex(j);
				curTag = curTag.toLowerCase();
				curTag = soundex.encode(curTag);

				if (curTag.contains(query)) {
					if (addQuestionOnlyOnce) {
						questions.add(curQuestion);
						addQuestionOnlyOnce = false;
					}
				}
			}
			addQuestionOnlyOnce = true;
		}
	}

	/** Search through all question contents for search query matches */
	public void searchQuestionContent(String query) {
		for (int i = 0; i < numberOfQuestions; i++) {
			Question curQuestion = manager.getQuestions().get(i);
			String curContent = curQuestion.getContent();
			if(doASoundexSearch){
				String[] curContentArray = curQuestion.getContent()
						.split("\\s");
				for (int x = 0; x < curContentArray.length; x++) {
					if (soundex.encode(curContentArray[x]).contains(query)) {
						questions.add(curQuestion);
					}
				}
			} else if (curContent.contains(query)) {
				System.out.println("Query: " + query);
				System.out.println("Query: " + curContent);
				questions.add(curQuestion);
			}
		}
	}

	/** Search through all comments for matches */
	public void searchComments(String query) {
		for (int i = 0; i < numberOfComments; i++) {

			Comment curComment = manager.getComments().get(i);
			String curContent = curComment.getContent();
			curContent = curContent.toLowerCase();

			if (doASoundexSearch) {
				String[] curContentArray = curComment.getContent().split("\\s");
				for (int x = 0; x < curContentArray.length; x++) {
					if (soundex.encode(curContentArray[x]).contains(query)) {
						commentResults.add(curComment);
					}
				}
			} else if (curContent.contains(query)) {
				commentResults.add(curComment);
			}
		}
	}

	/** Search through all answer contents for matches */
	public void searchAnswerContent(String query) {
		for (int i = 0; i < numberOfAnswers; i++) {

			Answer curAnswer = manager.getAnswers().get(i);
			String curContent = curAnswer.getContent();
			curContent = curContent.toLowerCase();

			if (doASoundexSearch) {
				String[] curContentArray = curAnswer.getContent().split("\\s");
				for (int x = 0; x < curContentArray.length; x++) {
					if (soundex.encode(curContentArray[x]).contains(query)) {
						answerContentResults.add(curAnswer);
					}
				}
			} else if (curContent.contains(query)) {
				answerContentResults.add(curAnswer);
			}
		}
	}

	/**
	 * Merge the questions of the tag search with them of the content search. I
	 * have left both method, because of advanced search.
	 */
	public void removeDuplicatedQuestions() {
		HashSet h = new HashSet(questions);
		questions.clear();
		questions.addAll(h);
	}

	/** Getters */
	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public ArrayList<Answer> getAnswerContentResults() {
		return answerContentResults;
	}

	public ArrayList<Comment> getCommentResults() {
		return commentResults;
	}
}
