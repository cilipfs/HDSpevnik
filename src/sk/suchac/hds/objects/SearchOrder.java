package sk.suchac.hds.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchOrder implements Serializable {

	private static final long serialVersionUID = -1231407228786848236L;
	private String searchString = "";
	private ArrayList<Integer> bookIds = new ArrayList<Integer>();
	
	public SearchOrder() {}
	
	public SearchOrder(String searchString, ArrayList<Integer> bookIds) {
		this.searchString = searchString;
		this.bookIds = bookIds;
	}
	
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public ArrayList<Integer> getBookIds() {
		return bookIds;
	}
	public void setBookIds(ArrayList<Integer> bookIds) {
		this.bookIds = bookIds;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
