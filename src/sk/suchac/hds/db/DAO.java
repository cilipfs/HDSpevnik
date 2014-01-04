package sk.suchac.hds.db;

import java.text.Collator;
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

public class DAO {
	// Database fields
	  private SQLiteDatabase database;
	  private DBHelper dbHelper;
	  private String[] allSongColumns = { "_id",
	      "NUMBER", "TITLE", "TEXT" };

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
	  
	  public List<SearchResult> getSearchResults(List<Integer> bookIds, String searchString) {
		  List<SearchResult> results = new ArrayList<SearchResult>();
		  
		  StringBuilder query = new StringBuilder();
		  query.append("SELECT book._id, chapter.NUMBER, verse.NUMBER, verse.TEXT FROM (SELECT * FROM BOOK WHERE ");
		  for (int i = 0; i < bookIds.size(); i++) {
			  Integer bookId = bookIds.get(i);
			  if (i == 0) {
				  query.append("_id='" + (bookId + 1) + "'");
			  } else {
				  query.append(" OR _id='" + (bookId + 1) + "'");
			  }
		  }
		  query.append(") as book JOIN CHAPTER as chapter ON book._id=chapter.BOOK_ID JOIN VERSE as verse ON chapter._id=verse.CHAPTER_ID WHERE TEXT LIKE ?");
		  
		  Cursor cursor = database.rawQuery(query.toString(), new String[] {"%" + searchString + "%"});
	      cursor.moveToFirst();
	      while (!cursor.isAfterLast()) {
			  SearchResult result = cursorToSearcHResult(cursor);
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
		  return song;
	  }
	  
	  private SearchResult cursorToSearcHResult(Cursor cursor) {
		  SearchResult sr = new SearchResult();
		  sr.setBookId(cursor.getInt(0) - 1);
		  sr.setChapterId(cursor.getInt(1) - 1);
		  sr.setVerseNumber(cursor.getInt(2));
		  sr.setSample("<b>" + cursor.getString(2) + "</b>" + " " + cursor.getString(3));	// TODO spravit nejaky formater tychto veci
		  return sr;
	  }
}

