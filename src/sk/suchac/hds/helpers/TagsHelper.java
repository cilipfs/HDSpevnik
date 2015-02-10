package sk.suchac.hds.helpers;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sk.suchac.hds.objects.Song;

public class TagsHelper {

	public static String getFormattedText(Song song) {
		Map<Integer, String> tagsMap = toTagsMap(song.getTags());
		StringBuilder text = new StringBuilder(song.getText());
		
		List<Integer> tagsPositions = getTagsPositions(tagsMap);
		
		for (Integer i : tagsPositions) {
			String strOnPosition = tagsMap.get(i);
			String[] tags = strOnPosition.split(",");
			
			for (int ii = tags.length -1; ii >= 0; ii--) {
				String tag = tags[ii];
				if (tag != null) {
					if (tag.indexOf("<") == 0) {
						text.replace(i, i + 1, tag);
					}
				}
			}
		}
		
		return text.toString();
	}
	
	public static String getFormattedTextWithChords(Song song) {
		Map<Integer, String> tagsMap = toTagsMap(song.getTags());
		StringBuilder text = new StringBuilder(song.getText());
		
		List<Integer> tagsPositions = getTagsPositions(tagsMap);
		
		for (Integer i : tagsPositions) {
			String strOnPosition = tagsMap.get(i);
			String[] tags = strOnPosition.split(",");
			
			for (int ii = tags.length -1; ii >= 0; ii--) {
				String tag = tags[ii];
				if (tag != null) {
					if (tag.indexOf("<") == 0) {
						text.replace(i, i + 1, tag);
					} else {
						text.insert(i, HtmlHelper.chord(tag));
					}
				}
			}
		}
		
		return text.toString();
	}
	
	private static List<Integer> getTagsPositions(Map<Integer, String> tagsMap) {
		List<Integer> tagsPositions = new ArrayList<Integer>(tagsMap.keySet());
		Collections.sort(tagsPositions);
		Collections.reverse(tagsPositions);
		return tagsPositions;
	}
	
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, String> toTagsMap(String tags) {
		Map<Integer, String> tagsMap = new HashMap<Integer, String>();
		if (tags == null) {
			return tagsMap;
		}
		
		String[] tagsArray = tags.split(";");
		
		for (String tag : tagsArray) {
			String[] tagParts = tag.split(":");
			tagsMap.put(Integer.parseInt(tagParts[0]), tagParts[1]);
		}
		
		return tagsMap;
	}
	
}
