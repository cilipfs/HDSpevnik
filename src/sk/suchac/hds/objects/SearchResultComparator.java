package sk.suchac.hds.objects;

import java.util.Comparator;

public class SearchResultComparator implements Comparator<SearchResult> {

	@Override
	public int compare(SearchResult a, SearchResult b) {
		int compare1 = a.getBookId() - b.getBookId();
		int compare2 = compare1 == 0 ? a.getChapterId() - b.getChapterId() : compare1;
		int compare3 = compare2 == 0 ? a.getVerseNumber() - b.getVerseNumber() : compare2;
		return compare3;
	}
	
}
