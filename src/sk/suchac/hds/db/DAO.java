package sk.suchac.hds.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import sk.suchac.hds.objects.SearchResult;
import sk.suchac.hds.objects.Song;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DAO {
	// Database fields
	  private SQLiteDatabase database;
	  private DBHelper dbHelper;
	  private String[] allSongColumns = { "_id",
	      "NUMBER", "TITLE", "TEXT", "SLIDEFLOW", "TAGS" };

	  public DAO(Context context) {
		  dbHelper = new DBHelper(context);
	  }
	  
	  public void initialize() {
		  dbHelper.initialize();
	  }

	  public void open() throws SQLException {
		  database = dbHelper.openDataBase();
	  }

	  public void close() {
		  dbHelper.close();
	  }
	  
	  public boolean isOpen() {
		  return dbHelper.isOpen();
	  }

	  public Song getSongByNumber(int number) {
		  String[] args = { Integer.toString(number) };
		  Cursor cursor = database.query("SONG",
				allSongColumns, "NUMBER=?", args, null, null, null);
		  cursor.moveToFirst();
		  Song song = cursorToSong(cursor);
		  cursor.close();
		    
		  return song;
	  }
	  
	  public String[] getSongTitleArray() {
		  List<String> songs = new ArrayList<String>();
		  Cursor cursor = database.query("SONG",
				allSongColumns, null, null, null, null, null);
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  Song song = cursorToSong(cursor);
		      songs.add(song.getNumber() + " " + song.getTitle());
		      cursor.moveToNext();
		  }
		  cursor.close();
		  
		  String[] array = new String[songs.size()];
		  songs.toArray(array);
		  
		  return array;
	  }
	  
	  public List<Song> getSongTitleAlphabeticalList() {
		  List<Song> songs = new ArrayList<Song>();
		  Cursor cursor = database.query("SONG",
				allSongColumns, null, null, null, null, null);
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  Song song = cursorToSong(cursor);
		      songs.add(song);
		      cursor.moveToNext();
		  }
		  cursor.close();
		  
		  Collections.sort(songs);
		  
		  return songs;
	  }
	  
	  public Song getSongById(int songId) {
		  String countQuery = "SELECT * FROM SONG WHERE _id=" + songId;
	      Cursor cursor = database.rawQuery(countQuery, null);
	      cursor.moveToFirst();
	      Song song = cursorToSong(cursor);
	      cursor.close();
		  
		  return song;
	  }
	  
	  public List<SearchResult> getSearchResults(String searchString) {
		  List<SearchResult> results = new ArrayList<SearchResult>();
		  
		  StringBuilder query = new StringBuilder();
		  query.append("SELECT * FROM SONG WHERE TEXT LIKE ?");
		  
		  Cursor cursor = database.rawQuery(query.toString(), new String[] {"%" + searchString + "%"});
	      cursor.moveToFirst();
	      while (!cursor.isAfterLast()) {
			  SearchResult result = cursorToSearcHResult(cursor, searchString);
		      results.add(result);
		      cursor.moveToNext();
		  }
		  cursor.close();
		  
		  return results;
	  }
	  
	  private Song cursorToSong(Cursor cursor) {
		  Song song = new Song();
		  song.set_id(cursor.getInt(0));
		  song.setNumber(cursor.getString(1));
		  song.setTitle(cursor.getString(2));
		  song.setText(cursor.getString(3));
		  song.setSlideFlow(cursor.getString(4));
		  song.setTags(cursor.getString(5));
		  return song;
	  }
	  
	  private SearchResult cursorToSearcHResult(Cursor cursor, String searchString) {
		  SearchResult sr = new SearchResult();
		  sr.setSongId(cursor.getInt(0));
		  sr.setNumber(cursor.getString(1));
		  sr.setTitle(cursor.getString(2));
		  sr.setSample(makeSample(cursor.getString(3), searchString));
		  return sr;
	  }

	  private String makeSample(String string, String searchString) {
		  String stringLC = string.toLowerCase(Locale.GERMAN);
		  String searchStringLC = searchString.toLowerCase(Locale.GERMAN);
		  String sample = "";
		  String br = "<br />";
		  int searchStringPosition = stringLC.indexOf(searchStringLC);

		  String part = stringLC.substring(searchStringPosition);
		  int secondBrPosition = part.indexOf(br);
		  
		  if (secondBrPosition == -1) {
			  int firstBrPosition = stringLC.lastIndexOf(br);
			  sample = string.substring(firstBrPosition + br.length());
		  } else {
			  sample = string.substring(0, secondBrPosition + searchStringPosition);
			  int firstBrPosition = sample.lastIndexOf(br);
			  
			  if (firstBrPosition != -1) {
				  sample = sample.substring(firstBrPosition + br.length());
			  }
		  }
		  
		  return sample;
	  }
	  
}

