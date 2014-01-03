package sk.suchac.hds.objects;

import java.util.Comparator;

public class HistoryRecordComparator implements Comparator<HistoryRecord> {
	
	@Override
	public int compare(HistoryRecord b1, HistoryRecord b2) {
		long ts1 = b1.getTimestamp();
		long ts2 = b2.getTimestamp();

		if(ts1 < ts2) return 1;
		else if(ts1 > ts2) return -1;
		else return 0;
	}
}