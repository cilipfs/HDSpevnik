package sk.suchac.hds.objects;

public class SearchResult {

	int bookId;
	int chapterId;
	int verseNumber;
	String sample;
	
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public int getChapterId() {
		return chapterId;
	}
	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}
	public int getVerseNumber() {
		return verseNumber;
	}
	public void setVerseNumber(int verseNumber) {
		this.verseNumber = verseNumber;
	}
	public String getSample() {
		return sample;
	}
	public void setSample(String sample) {
		this.sample = sample;
	}
	
}
