package sk.suchac.hds.objects;

import java.util.Comparator;

public class SearchResultComparator implements Comparator<SearchResult> {

	@Override
	public int compare(SearchResult a, SearchResult b) {
		int compare1 = a.getSongId() - b.getSongId();
		return compare1;
	}
	
}
