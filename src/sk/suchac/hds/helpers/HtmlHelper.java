package sk.suchac.hds.helpers;

public class HtmlHelper {
	
	public static final String LINE_BRAKE = "<br />";
	public static final String SLIDE_BRAKE_TAG = "<sldbrk />";
	public static final String SLIDE_BRAKE_ALTERNATIVE = "<br /><br />";
	
	public static String bold(String text) {
		return "<b>" + text + "</b>";
	}
	
	public static String getBoldStart() {
		return "<b>";
	}
	
	public static String getBoldEnd() {
		return "</b>";
	}
	
	public static String bold(int text) {
		return "<b>" + text + "</b>";
	}
	
	public static String getNewLine() {
		return "<br />";
	}
	
	public static String getSkipLine() {
		return "<br /><br />";
	}
	
	public static String chord(String chord) {
		return "<font color=\"#B10006\"><i><sup>" + chord + "</sup></i></font>";
	}
}
