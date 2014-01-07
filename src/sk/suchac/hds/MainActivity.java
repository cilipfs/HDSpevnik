package sk.suchac.hds;

import java.util.List;

import sk.suchac.hds.db.DAO;
import sk.suchac.hds.objects.PickedSongInfo;
import sk.suchac.hds.objects.Song;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	MainActivity thisActivity;
	
	private AlertDialog bookDialog;
	private AlertDialog registerDialog;
	private Button buttonSongList;
	private Button buttonSongRegister;
	private View background;
	private TextView title;
	private AutoCompleteTextView mainSearch;
	ArrayAdapter<String> adapter;
	
	private DAO datasource;
	
	private boolean updateDbDone = false;
	
	public static final String PREFS = "HdsPrefsFile";
	private static boolean nightMode;
	
	public final static String INTENT_PICKED_SONG = "sk.suchac.hds.PICKED_SONG";
	PickedSongInfo picked = new PickedSongInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        initializeElements();
        
        disableButtons();
    	title.setText(getResources().getString(R.string.title_updating));
        new UpdateDBTask().execute();

    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
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
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!updateDbDone) {
			Toast toast = Toast.makeText(getApplicationContext(), 
	    			getResources().getString(R.string.updating_so_wait), 
	    			Toast.LENGTH_SHORT);
	    	toast.show();
			return true;
		}
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.night_day_mode:
	    		switchNightDayMode();
	    		return true;
	    	case R.id.show_history:
	    		Intent intent2 = new Intent(this, HistoryActivity.class);
			    startActivity(intent2);
	            return true;
	    	case R.id.show_search:
	    		Intent intent3 = new Intent(this, SearchActivity.class);
			    startActivity(intent3);
	            return true;
	    	case R.id.show_settings:
	    		Intent intent4 = new Intent(this, SettingsActivity.class);
			    startActivity(intent4);
	            return true;
	    	case R.id.show_about:
	    		Intent intent5 = new Intent(this, AboutActivity.class);
	    		startActivity(intent5);
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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

	private void initializeElements() {
		buttonSongList = (Button) findViewById(R.id.button_book);
        buttonSongList.setOnClickListener(buttonBookListener);
        buttonSongRegister = (Button) findViewById(R.id.button_register);
        buttonSongRegister.setOnClickListener(buttonRegisterListener);
        background = findViewById(R.id.main_layout);
    	title = (TextView) findViewById(R.id.textView_title_bible);
    	mainSearch = (AutoCompleteTextView) findViewById(R.id.mainSearch);
	}
	
private class UpdateDBTask extends AsyncTask<Void, Void, Void> {
		
        @Override
        protected Void doInBackground(Void... params) {
        	datasource = new DAO(thisActivity);
        	datasource.initialize();
        	datasource.open();
        	return null;
        }
        
        protected void onPostExecute(Void result) {
        	 // update
        	enableButtons();
        	title.setText(R.string.title_bible);
        	
        	picked.setSong(-1);
        	
        	String[] songTitleArray = datasource.getSongTitleArray();
            
            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
            builder.setItems(songTitleArray, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int which) {
        			datasource.open();
        			Intent intent = new Intent(thisActivity, SlideActivity.class);	// TODO podmienku
				    PickedSongInfo sp = new PickedSongInfo(which + 1);
					intent.putExtra(INTENT_PICKED_SONG, sp);
				    startActivity(intent);
        			datasource.close();
            	}
            });
            bookDialog = builder.create();
            
            final List<Song> songsAlphabetical = datasource.getSongTitleAlphabeticalList();
            String[] songsAlphabeticalArray = createSongsAlphabeticalArray(songsAlphabetical);
  		    
            AlertDialog.Builder registerBuilder = new AlertDialog.Builder(thisActivity);
            registerBuilder.setItems(songsAlphabeticalArray, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int which) {
        			datasource.open();
        			Intent intent = new Intent(thisActivity, ScriptureActivity.class);
				    PickedSongInfo sp = new PickedSongInfo(songsAlphabetical.get(which).get_id());
					intent.putExtra(INTENT_PICKED_SONG, sp);
				    startActivity(intent);
        			datasource.close();
            	}
            });
            registerDialog = registerBuilder.create();
            
            adapter = 
        	        new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, songTitleArray);
        	mainSearch.setAdapter(adapter);
    		mainSearch.setOnItemClickListener(mainSearchListener);
            
            datasource.close();
            updateDbDone = true;
        }          
    }

	private OnItemClickListener mainSearchListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String item = mainSearch.getText().toString();
			int spacePosition = item.indexOf(" ");
			item = item.substring(0, spacePosition);
			mainSearch.setText("");
			
			datasource.open();
			Song song = datasource.getSongByNumber(Integer.valueOf(item));
			
			Intent intent = new Intent(thisActivity, ScriptureActivity.class);
		    PickedSongInfo sp = new PickedSongInfo(song.get_id());
			intent.putExtra(INTENT_PICKED_SONG, sp);
		    startActivity(intent);
		    
		    datasource.close();
		}
	};

	private String[] createSongsAlphabeticalArray(List<Song> songsAlphabetical) {
		String[] array = new String[songsAlphabetical.size()];
		for (int i = 0; i < songsAlphabetical.size(); i++) {
			Song song = songsAlphabetical.get(i);
			array[i] = song.getTitle() + " " + song.getNumber();
		}
		return array;
	}
	
	private void disableButtons() {
		buttonSongList.setEnabled(false);
		buttonSongRegister.setEnabled(false);
		mainSearch.setEnabled(false);
	}
	
	private void enableButtons() {
		buttonSongList.setEnabled(true);
		buttonSongRegister.setEnabled(true);
		mainSearch.setEnabled(true);
	}
	
	private OnClickListener buttonBookListener = new OnClickListener() {
	    public void onClick(View v) {
	      bookDialog.show();
	    }
	};
	
	private OnClickListener buttonRegisterListener = new OnClickListener() {
	    public void onClick(View v) {
	      registerDialog.show();
	    }
	};
	
	// onClick for buttonPick
	public void showPickedScripture(View view) {
		Intent intent = new Intent(this, ScriptureActivity.class);
	    intent.putExtra(INTENT_PICKED_SONG, picked);
	    startActivity(intent);
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(getResources().getColor(R.color.night_back));
    	title.setTextColor(getResources().getColor(R.color.night_text));
    	mainSearch.setBackgroundResource(R.color.night_text);
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(getResources().getColor(R.color.day_back));
    	title.setTextColor(getResources().getColor(R.color.day_text));
    	mainSearch.setBackgroundResource(R.color.night_text);
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}
	
}
