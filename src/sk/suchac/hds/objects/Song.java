package sk.suchac.hds.objects;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Locale;


public class Song implements Comparable<Song> {
	
	private static final Collator collator = Collator.getInstance(Locale.GERMAN);
	private CollationKey key;
	
	private int _id;
	private String number;
	private String title;
	private String text;
	private String slideFlow;
	private String tags;
	
	public String getSlideFlow() {
		return slideFlow;
	}
	public void setSlideFlow(String slideFlow) {
		this.slideFlow = slideFlow;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
		this.key = collator.getCollationKey(title);
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public int compareTo(Song song) {
		return key.compareTo(song.key);
	}
	
}
