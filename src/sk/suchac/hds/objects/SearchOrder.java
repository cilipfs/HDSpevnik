package sk.suchac.hds.objects;

import java.io.Serializable;

public class SearchOrder implements Serializable {

	private static final long serialVersionUID = -1231407228786848236L;
	private String searchString = "";
	
	public SearchOrder() {}
	
	public SearchOrder(String searchString) {
		this.searchString = searchString;
	}
	
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
