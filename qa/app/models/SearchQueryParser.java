package models;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.codec.language.Soundex;

/**
 * In this class will the search query splitted in word or sentences if the
 * words are embraced with double quotes. For example: Word are: these are
 * words, and a sentence would be: "this would be a sentence". Then some
 * stopwords will be filtered out of the words, and soundex codes will be made
 * of them.
 */
public class SearchQueryParser {
	/**
	 * The list of the stopwords found on:
	 * http://armandbrahaj.blog.al/2009/04/14/list-of-english-stop-words/
	 */
	private String[] englishStopwordsDatabase = { "a", "a's", "able", "about",
			"above",
			"according", "accordingly", "across", "actually", "after",
			"afterwards", "again", "against", "ain't", "all", "allow",
			"allows", "almost", "alone", "along", "already", "also",
			"although", "always", "am", "among", "amongst", "an", "and",
			"another", "any", "anybody", "anyhow", "anyone", "anything",
			"anyway", "anyways", "anywhere", "apart", "appear", "appreciate",
			"appropriate", "are", "aren't", "around", "as", "aside", "ask",
			"asking", "associated", "at", "available", "away", "awfully", "b",
			"be", "became", "because", "become", "becomes", "becoming", "been",
			"before", "beforehand", "behind", "being", "believe", "below",
			"beside", "besides", "best", "better", "between", "beyond", "both",
			"brief", "but", "by", "c", "c'mon", "c's", "came", "can", "can't",
			"cannot", "cant", "cause", "causes", "certain", "certainly",
			"changes", "clearly", "co", "com", "come", "comes", "concerning",
			"consequently", "consider", "considering", "contain", "containing",
			"contains", "corresponding", "could", "couldn't", "course",
			"currently", "d", "definitely", "described", "despite", "did",
			"didn't", "different", "do", "does", "doesn't", "doing", "don't",
			"done", "down", "downwards", "during", "e", "each", "edu", "eg",
			"eight", "either", "else", "elsewhere", "enough", "entirely",
			"especially", "et", "etc", "even", "ever", "every", "everybody",
			"everyone", "everything", "everywhere", "ex", "exactly", "example",
			"except", "f", "far", "few", "fifth", "first", "five", "followed",
			"following", "follows", "for", "former", "formerly", "forth",
			"four", "from", "further", "furthermore", "g", "get", "gets",
			"getting", "given", "gives", "go", "goes", "going", "gone", "got",
			"gotten", "greetings", "h", "had", "hadn't", "happens", "hardly",
			"has", "hasn't", "have", "haven't", "having", "he", "he's",
			"hello", "help", "hence", "her", "here", "here's", "hereafter",
			"hereby", "herein", "hereupon", "hers", "herself", "hi", "him",
			"himself", "his", "hither", "hopefully", "how", "howbeit",
			"however", "i", "i'd", "i'll", "i'm", "i've", "ie", "if",
			"ignored", "immediate", "in", "inasmuch", "inc", "indeed",
			"indicate", "indicated", "indicates", "inner", "insofar",
			"instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll",
			"it's", "its", "itself", "j", "just", "k", "keep", "keeps", "kept",
			"know", "knows", "known", "l", "last", "lately", "later", "latter",
			"latterly", "least", "less", "lest", "let", "let's", "like",
			"liked", "likely", "little", "look", "looking", "looks", "ltd",
			"m", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile",
			"merely", "might", "more", "moreover", "most", "mostly", "much",
			"must", "my", "myself", "n", "name", "namely", "nd", "near",
			"nearly", "necessary", "need", "needs", "neither", "never",
			"nevertheless", "new", "next", "nine", "no", "nobody", "non",
			"none", "noone", "nor", "normally", "not", "nothing", "novel",
			"now", "nowhere", "o", "obviously", "of", "off", "often", "oh",
			"ok", "okay", "old", "on", "once", "one", "ones", "only", "onto",
			"or", "other", "others", "otherwise", "ought", "our", "ours",
			"ourselves", "out", "outside", "over", "overall", "own", "p",
			"particular", "particularly", "per", "perhaps", "placed", "please",
			"plus", "possible", "presumably", "probably", "provides", "q",
			"que", "quite", "qv", "r", "rather", "rd", "re", "really",
			"reasonably", "regarding", "regardless", "regards", "relatively",
			"respectively", "right", "s", "said", "same", "saw", "say",
			"saying", "says", "second", "secondly", "see", "seeing", "seem",
			"seemed", "seeming", "seems", "seen", "self", "selves", "sensible",
			"sent", "serious", "seriously", "seven", "several", "shall", "she",
			"should", "shouldn't", "since", "six", "so", "some", "somebody",
			"somehow", "someone", "something", "sometime", "sometimes",
			"somewhat", "somewhere", "soon", "sorry", "specified", "specify",
			"specifying", "still", "sub", "such", "sup", "sure", "t", "t's",
			"take", "taken", "tell", "tends", "th", "than", "thank", "thanks",
			"thanx", "that", "that's", "thats", "the", "their", "theirs",
			"them", "themselves", "then", "thence", "there", "there's",
			"thereafter", "thereby", "therefore", "therein", "theres",
			"thereupon", "these", "they", "they'd", "they'll", "they're",
			"they've", "think", "third", "this", "thorough", "thoroughly",
			"those", "though", "three", "through", "throughout", "thru",
			"thus", "to", "together", "too", "took", "toward", "towards",
			"tried", "tries", "truly", "try", "trying", "twice", "two", "u",
			"un", "under", "unfortunately", "unless", "unlikely", "until",
			"unto", "up", "upon", "us", "use", "used", "useful", "uses",
			"using", "usually", "uucp", "v", "value", "various", "very", "via",
			"viz", "vs", "w", "want", "wants", "was", "wasn't", "way", "we",
			"we'd", "we'll", "we're", "we've", "welcome", "well", "went",
			"were", "weren't", "what", "what's", "whatever", "when", "whence",
			"whenever", "where", "where's", "whereafter", "whereas", "whereby",
			"wherein", "whereupon", "wherever", "whether", "which", "while",
			"whither", "who", "who's", "whoever", "whole", "whom", "whose",
			"why", "will", "willing", "wish", "with", "within", "without",
			"won't", "wonder", "would", "would", "wouldn't", "x", "y", "yes",
			"yet", "you", "you'd", "you'll", "you're", "you've", "your",
			"yours", "yourself", "yourselves", "z", "zero" };

	private ArrayList<String> englishStopwords;

	/** The original search query the user typed in. */
	private String originalQuery;

	/** The list of the word in the query. */
	private ArrayList<String> words;

	/** the soundex codes of the word in the query. */
	private ArrayList<String> soundexCodes;

	/** the sentences in the query. */
	private ArrayList<String> sentences;

	/**
	 * The soundex algorithm from:
	 * 
	 * @package org.apache.commons.codec.language.Soundex
	 */
	private Soundex soundexAlgorithm;

	public SearchQueryParser(String query) {
		// Make query case insensitive.
		originalQuery = query.toLowerCase();
		
		// Get original query an split in words and store in ArrayList.
		words = new ArrayList<String>(java.util.Arrays.asList(originalQuery
				.split(" ")));

		englishStopwords = new ArrayList<String>(
				java.util.Arrays.asList(englishStopwordsDatabase));

		sentences = new ArrayList<String>();
		soundexCodes = new ArrayList<String>();
		soundexAlgorithm = new Soundex();

		divideQueryInWordsAndSentences();
	}

	private void divideQueryInWordsAndSentences() {
		int doubleQuoteCounter = 0;
		String sentence = "";
		ArrayList<Integer> indexesToRemove = new ArrayList<Integer>();

		// Filter out Strings marked with "" e.g.
		// "this is to filter out as a sentence"
		for (int i = 0; i < words.size(); i++) {

			// Will get the first word after the double quote and add to
			// sentence string.
			if (words.get(i).contains("\"")) {
				sentence = sentence + " " + words.get(i);
				indexesToRemove.add(i);
				doubleQuoteCounter++;
			}

			// Will add all word from the second to the second last word to
			// sentence string.
			else if (doubleQuoteCounter == 1) {
				sentence = sentence + " " + words.get(i);
				indexesToRemove.add(i);
			}

			// Ads the last word (the word directly before the double quote) to
			// sentence String.
			if (doubleQuoteCounter == 2) {
				sentence = sentence.replace("\"", "");
				sentence = sentence.substring(1, sentence.length());
				sentences.add(sentence);
				doubleQuoteCounter = 0;
				sentence = "";
			}
		}

		// Removes the word previously added to the sentence from the words
		// List.
		for (int j = indexesToRemove.size() - 1; j >= 0; j--) {
			int index = indexesToRemove.get(j);
			words.remove(index);
		}

		// Removes duplicates in words list, filter out stopwords and creates
		// soundex codes.
		removeDuplicate();
		filterOutStopwords();
		createSoundexOfWords();
	}

	/** Removes duplicated words */
	private void removeDuplicate() {
		HashSet h = new HashSet(words);
		words.clear();
		words.addAll(h);
	}

	/** Will filter out all english word which are in the stopwords list. */
	private void filterOutStopwords() {
		for (int j = englishStopwords.size() - 1; j >= 0; j--) {
			for (int i = words.size() - 1; i >= 0; i--) {
				if (englishStopwords.get(j).equals(words.get(i))) {
					words.remove(i);
				}
			}
		}
	}

	/** Creates the soundex code of all words in the query. */
	private void createSoundexOfWords() {
		for (int i = 0; i < words.size(); i++) {
			try {
				String soundexCode = soundexAlgorithm.encode(words.get(i));
				soundexCodes.add(soundexCode);
			} catch (IllegalArgumentException e) {
			}
		}

		// Remove duplicated soundex codes
		HashSet h = new HashSet(soundexCodes);
		soundexCodes.clear();
		soundexCodes.addAll(h);
	}

	/** Getters */
	public ArrayList<String> getSoundexCodes() {
		return soundexCodes;
	}
	public ArrayList<String> getWords() {
		return words;
	}
	public ArrayList<String> getSentences() {
		return sentences;
	}
}
