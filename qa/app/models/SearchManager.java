package models;

import java.util.ArrayList;

import models.algorithms.SearchResultAssembler;
import models.algorithms.SearchResultSorter;

/**
 * This class maintains all the steps of the search in the correct order. The
 * main intention for the creation of this class, was that in the controller
 * only 1 class will need to be invoked
 */
public class SearchManager {
	private String query;
	private SearchQueryParser parser;
	private Search search;
	private SearchResultAssembler assembler;
	private SearchResultSorter sorter;

	public SearchManager(String query) {
		this.query = query;

		initSearch();
	}

	private void initSearch() {
		// Init the parser
		parser = new SearchQueryParser(query);
		// Get parsed query
		search = new Search(parser.getQueryWordsSoundex(), parser.getQuerySentences());
		// Init the assembler
		assembler = new SearchResultAssembler(search.getAnswerContentResults(),
				search.getCommentResults(), search.getQuestions(), query);
		// Do the sorting of the results
		sorter = new SearchResultSorter(assembler.getSearchResults(),
				parser.getQueryWordsSoundex(), parser.getQuerySentences());

	}

	public ArrayList<SearchResult> getResults() {
		return sorter.getSearchResults();
	}
}
