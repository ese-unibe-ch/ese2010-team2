package models;

import java.util.ArrayList;

import models.algorithms.SearchQueryParser;
import models.algorithms.SearchResultAssembler;
import models.algorithms.SearchResultSorter;

/**
 * This class maintains all the steps of the search in the correct order. The
 * main intention for the creation of this class was that in the controller only
 * 1 class will need to be invoked. If you need to proceed a search use this
 * class, do not invoke every class.
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

		// Get parsed query and search fulltext and tag in Questions, Answers
		// and Comments.
		search = new Search(parser.getSoundexCodes(), parser.getSentences());

		// Take the results from the search and assemble them together in
		// searchResults, they will be used for counting in Search Result
		// sorter.
		assembler = new SearchResultAssembler(search.getAnswerResults(),
				search.getCommentResults(), search.getQuestionResults());

		// Count the occurence of the Words or Sentences of the search query in
		// all SearchResults and count the total Score of a searchResult, those
		// data will be used for sorting the searchResults in a smart way.
		sorter = new SearchResultSorter(assembler.getSearchResults(),
				parser.getSoundexCodes(), parser.getSentences());
	}

	/** Getter */
	public ArrayList<SearchResult> getSearchResults() {
		return sorter.getSearchResults();
	}
}
