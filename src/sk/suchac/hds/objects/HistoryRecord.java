package sk.suchac.hds.objects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryRecord {
	
	private long timestamp;
	private int songNumber;
	
	public HistoryRecord() {}
	
	public HistoryRecord(long timestamp, int bookNumber) {
		this.timestamp = timestamp;
		this.songNumber = bookNumber;
	}
	
	public HistoryRecord(String historyRecordStr) {
		String[] bm = historyRecordStr.split("~");
		for (int i = 0; i < bm.length; i++) {
			if (i == 0) {
				this.timestamp = Long.parseLong(bm[i]);
			} else if (i == 1) {
				this.songNumber = Integer.parseInt(bm[i]);
			}
		}
	}
	
	public String getDateString() {
		Date date = new Date(timestamp);
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return df.format(date);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getSongNumber() {
		return songNumber;
	}

	public void setSongNumber(int songNumber) {
		this.songNumber = songNumber;
	}

	public String toString() {
		return String.valueOf(timestamp) + "~" + String.valueOf(songNumber);
	}
	
}
