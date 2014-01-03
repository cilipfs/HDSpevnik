package sk.suchac.hds.objects;

import java.io.Serializable;

public class PickedSongInfo implements Serializable {
	
	private static final long serialVersionUID = -7066276067582111945L;
	
	public PickedSongInfo() {}
	
	public PickedSongInfo(int bookId) {
		this.song = bookId;
	}
	
	private int song;

	public int getSong() {
		return song;
	}

	public void setSong(int song) {
		this.song = song;
	}
	
}
