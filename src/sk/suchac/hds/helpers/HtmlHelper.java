package sk.suchac.hds.helpers;

public class HtmlHelper {
	
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
}
