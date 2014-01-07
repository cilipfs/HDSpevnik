package sk.suchac.hds;

import sk.suchac.hds.db.DAO;
import sk.suchac.hds.helpers.HistoryHelper;
import sk.suchac.hds.objects.PickedSongInfo;
import sk.suchac.hds.objects.Song;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class SlideActivity extends Activity {
	
	private SlideActivity thisActivity = this;
	private TextView textField;
	private View background;
	
	private DAO datasource;
	
	public static final String PREFS = "HdsPrefsFile";
	private static boolean nightMode;
	public static final String SETTINGS_PREFS = "HdsSettingsPrefs";
	
	public final static String INTENT_PICKED_SONG = "sk.suchac.hds.PICKED_SONG";
	PickedSongInfo pickedSong = new PickedSongInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide);
		
		datasource = new DAO(this);
		datasource.open();
		
		background = findViewById(R.id.slide_layout);
		textField = (TextView) findViewById(R.id.textView_slide);
		textField.setText("");
		
		Intent intent = getIntent();
		pickedSong = (PickedSongInfo) intent.getSerializableExtra(MainActivity.INTENT_PICKED_SONG);
		
		displayScriptureText();
		HistoryHelper.saveRecord(thisActivity, pickedSong.getSong());
        
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.slide, menu);
		return true;
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
        if (isNightMode()) {
        	applyNightMode();
        } else {
        	applyDayMode();
        }
        
        if (isKeepScreenOn()) {
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        
        applyFontSize();
    }

	private void applyFontSize() {
		SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
		textField.setTextSize(settings.getInt("fontSize", 18));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.night_day_mode:
	    		switchNightDayMode();
	    		return true;
			case R.id.show_pick_activity:
				Intent intent = new Intent(this, MainActivity.class);
			    startActivity(intent);
	            return true;
			case R.id.show_settings:
	    		Intent intent3 = new Intent(this, SettingsActivity.class);
			    startActivity(intent3);
	            return true;
			case R.id.show_about:
	    		Intent intent4 = new Intent(this, AboutActivity.class);
	    		startActivity(intent4);
	    		return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void switchNightDayMode() {
		if (nightMode) {
			saveNightModeState(false);
		    applyDayMode();
        } else {
        	saveNightModeState(true);
		    applyNightMode();
        }
	}

	private void displayScriptureText() {
		Song song = datasource.getSongById(pickedSong.getSong());
		// TODO podmienka na abesenciu <sldbrk />
		String[] slides = song.getText().split("<sldbrk />");
		textField.append(Html.fromHtml(slides[0]));	// TODO slide style
		this.setTitle(song.getNumber() + " " + song.getTitle());
	}
	
	// onClick for buttonAbout, buttonSeb
	public void displayAbout(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
	    startActivity(intent);
	}
	
	private boolean isKeepScreenOn() {
		SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
        return settings.getBoolean("keepScreenOn", false);
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(getResources().getColor(R.color.night_back));
    	textField.setTextColor(getResources().getColor(R.color.night_text));
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(getResources().getColor(R.color.day_back));
    	textField.setTextColor(getResources().getColor(R.color.day_text));
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}

}
