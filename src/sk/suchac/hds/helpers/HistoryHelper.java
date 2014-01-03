package sk.suchac.hds.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import sk.suchac.hds.objects.HistoryRecord;
import sk.suchac.hds.objects.HistoryRecordComparator;
import android.app.Activity;
import android.content.SharedPreferences;

public class HistoryHelper {
	
	public static final String PREFS_HISTORY = "HdsHistoryPrefs";
	public static final int MAX_RECORDS = 10;
	
	public static boolean saveRecord(Activity activity, int bookNumber) {
		boolean success = false;
		SharedPreferences settings = activity.getSharedPreferences(PREFS_HISTORY, 0);
		Map<String,?> internal = settings.getAll();
		
		long timestamp = new Date().getTime();
		HistoryRecord histRecord = new HistoryRecord(timestamp, bookNumber);
		
		if (internal.size() != MAX_RECORDS) {
			SharedPreferences.Editor editor = settings.edit();
		    editor.putString(String.valueOf(timestamp), histRecord.toString());
		    success = editor.commit();
		} else {
			Object[] values = internal.values().toArray();
			List<HistoryRecord> hrList = castHrObjectsToList(values);
			Collections.sort(hrList, new HistoryRecordComparator());
			
			HistoryRecord oldestRecord = hrList.get(MAX_RECORDS - 1);
			
			SharedPreferences.Editor editor = settings.edit();
			editor.remove(String.valueOf(oldestRecord.getTimestamp()));
		    editor.putString(String.valueOf(timestamp), histRecord.toString());
		    success = editor.commit();
		}
		
		return success;
	}
	
	private static ArrayList<HistoryRecord> castHrObjectsToList(Object[] values) {
		ArrayList<HistoryRecord> hrList = new ArrayList<HistoryRecord>();
		for (int i = 0; i < values.length; i++) {
			hrList.add(new HistoryRecord((String) values[i]));
		}
		return hrList;
	}
	
	public static List<HistoryRecord> getRecords(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_HISTORY, 0);
		Map<String,?> internal = settings.getAll();
		Object[] values = internal.values().toArray();
		
		List<HistoryRecord> hrList = castHrObjectsToList(values);
		Collections.sort(hrList, new HistoryRecordComparator());
		
		return hrList;
	}
}
