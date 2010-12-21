import models.algorithms.SearchQueryParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class SearchQueryParserTest extends UnitTest {
	private SearchQueryParser parser;
	private String query;

	@Before
	public void setUp() {
		query = "";
		parser = new SearchQueryParser(query);
	}

	@Test
	public void shouldSplitWordsAndAddToArrayList() {
		query = "fredi house friend";
		parser = new SearchQueryParser(query);

		assertEquals(3, parser.getWords().size());
		assertEquals("house", parser.getWords().get(0));
		assertEquals("friend", parser.getWords().get(1));
		assertEquals("fredi", parser.getWords().get(2));
	}

	@Test
	public void shouldNotSplitDoubleQuotetQuery() {
		query = "\"fredi house friend\"";
		parser = new SearchQueryParser(query);

		assertEquals(0, parser.getWords().size());
		assertEquals(1, parser.getSentences().size());
		assertEquals("fredi house friend", parser.getSentences().get(0));
	}

	@Test
	public void shouldDifferBetweenQuotetTextAndNotQuotetText() {
		query = "\"fredi house friend\" test garden \"We can\"";
		parser = new SearchQueryParser(query);

		assertEquals(2, parser.getWords().size());
		assertEquals("test", parser.getWords().get(0));
		assertEquals("garden", parser.getWords().get(1));
		assertEquals(2, parser.getSentences().size());
		assertEquals("fredi house friend", parser.getSentences().get(0));
		assertEquals("we can", parser.getSentences().get(1));
	}

	@Test
	public void quotetTextIsNotCaseSensitive() {
		// The case sensitivity of not quotet text needs not to be tested,
		// because when using soundex, case sensitivity doesn't matter.

		query = "\"fredi hOUse friEnd\"";
		parser = new SearchQueryParser(query);

		assertEquals(0, parser.getWords().size());
		assertEquals(1, parser.getSentences().size());
		assertEquals("fredi house friend", parser.getSentences().get(0));
	}

	@Test
	public void shouldCreateSoundexCodes() {
		query = "fredi house friend";
		parser = new SearchQueryParser(query);

		assertEquals(3, parser.getSoundexCodes().size());
	}

	@Test
	public void shouldDeleteDuplicatedSoundexCodes() {
		query = "fredi man men";
		parser = new SearchQueryParser(query);

		assertEquals(2, parser.getSoundexCodes().size());
	}

	@After
	public void tearDown() {
		parser.getSentences().clear();
		parser.getWords().clear();
		parser.getSoundexCodes().clear();
	}
}