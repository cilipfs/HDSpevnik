package sk.suchac.hds;

import sk.suchac.hds.db.DAO;
import sk.suchac.hds.helpers.TagsHelper;
import sk.suchac.hds.helpers.ExportHelper;
import sk.suchac.hds.helpers.HistoryHelper;
import sk.suchac.hds.helpers.IntentHelper;
import sk.suchac.hds.helpers.PreferencesHelper;
import sk.suchac.hds.objects.PickedSongInfo;
import sk.suchac.hds.objects.Song;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ScriptureActivity extends Activity {
	
	private ScriptureActivity thisActivity = this;
	private TextView textField;
	private View background;
	
	private DAO datasource;
	private Song song;
	
	private static boolean nightMode;
	
	PickedSongInfo pickedSong = new PickedSongInfo();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripture);
		
		datasource = new DAO(this);
		datasource.open();
		
		background = findViewById(R.id.scripture_layout);
		textField = (TextView) findViewById(R.id.textView);
		textField.setText("");
		
		Intent intent = getIntent();
		pickedSong = (PickedSongInfo) intent.getSerializableExtra(IntentHelper.INTENT_PICKED_SONG);
		
		song = datasource.getSongById(pickedSong.getSong());
		displayScriptureText();
		HistoryHelper.saveRecord(thisActivity, pickedSong.getSong());
        
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_scripture, menu);
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
        displayScriptureText();
    }

	private void applyFontSize() {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.SETTINGS_PREFS, 0);
		textField.setTextSize(settings.getInt("fontSize", 18));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.night_day_mode:
	    		switchNightDayMode();
	    		return true;
			case R.id.normal_presentation_mode:
				switchNormalPresentationMode();
				return true;
			case R.id.chords_mode:
				switchChordsMode();
				return true;
			case R.id.export_TXT:
				new ExportTask().execute();
	    		return true;
			case R.id.show_pick_activity:
				Intent intent = new Intent(this, MainActivity.class);
			    startActivity(intent);
	            return true;
			case R.id.show_settings:
	    		Intent intent3 = new Intent(this, SettingsActivity.class);
	    		intent3.putExtra(IntentHelper.INTENT_FOR_SETTINGS, thisActivity.getLocalClassName());
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
	
	private void switchNormalPresentationMode() {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.SETTINGS_PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("presentationMode", true);
	    editor.commit();
	    
	    Intent intent = new Intent(thisActivity, SlideActivity.class);
		intent.putExtra(IntentHelper.INTENT_PICKED_SONG, pickedSong);
	    startActivity(intent);
	}
	
	private void switchChordsMode() {
		boolean chordsMode = isChordsMode();
		
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.SETTINGS_PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("chordsMode", !chordsMode);
	    editor.commit();
	    
	    displayScriptureText();
	}

	private void displayScriptureText() {
		String text = "";
		if (isChordsMode()) {
			text = TagsHelper.getFormattedTextWithChords(song);
		} else {
			text = TagsHelper.getFormattedText(song);
		}
		
		textField.setText(Html.fromHtml(text));
		this.setTitle(song.getNumber() + " " + song.getTitle());
	}
	
	// onClick for buttonAbout, buttonSeb
	public void displayAbout(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
	    startActivity(intent);
	}
	
	private class ExportTask extends AsyncTask<Void, Void, Void> {
		private boolean success = false;
		
        @Override
        protected Void doInBackground(Void... params) {
        	success = ExportHelper.export(thisActivity, song.getText());
        	return null;
        }
        
        protected void onPostExecute(Void result) {
        	if (success) {
        		Toast toast = Toast.makeText(getApplicationContext(), 
    				getResources().getString(R.string.export_success) + ExportHelper.EXPORT_DIR + "/",
    				Toast.LENGTH_SHORT);
    		    toast.show();
        	} else {
        		Toast toast = Toast.makeText(getApplicationContext(), 
					getResources().getString(R.string.export_fail),
					Toast.LENGTH_SHORT);
			    toast.show();
        	}
        }
    }
	
	private boolean isKeepScreenOn() {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.SETTINGS_PREFS, 0);
        return settings.getBoolean("keepScreenOn", false);
	}
	
	private boolean isChordsMode() {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.SETTINGS_PREFS, 0);
		return settings.getBoolean("chordsMode", false);
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.PREFS, 0);
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
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}

}
