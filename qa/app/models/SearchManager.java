package models;

import java.util.ArrayList;

import models.algorithms.SearchResultAssembler;
import models.algorithms.SearchResultSorter;

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

		assembler = new SearchResultAssembler(search.getAnswerContentResults(),
				search.getCommentResults(), search.getQuestions(), query);
		
		sorter = new SearchResultSorter(assembler.getSearchResults(),
				parser.getQueryWordsSoundex(), parser.getQuerySentences());

	}

	public ArrayList<SearchResult> getResults() {
		return sorter.getSearchResults();
	}
}
