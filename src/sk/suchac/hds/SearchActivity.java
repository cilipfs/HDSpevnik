package sk.suchac.hds;

import sk.suchac.hds.db.DAO;
import sk.suchac.hds.helpers.IntentHelper;
import sk.suchac.hds.helpers.PreferencesHelper;
import sk.suchac.hds.objects.SearchOrder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SearchActivity extends Activity {
	
	SearchActivity thisActivity = this;
	
	LinearLayout background;
	EditText textInput;
	Button buttonSearch;
	
	private DAO datasource;
	
	private static Resources resources;
	
	private static boolean nightMode;
	
	private static final int SEARCH_STRING_MAX_LETTERS = 40;
	private static final int SEARCH_STRING_MIN_LETTERS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		resources = getResources();
		
		datasource = new DAO(this);
		datasource.open();
		
		initializeElements();
		
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
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
		}
		return super.onOptionsItemSelected(item);
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
		background = (LinearLayout) findViewById(R.id.search_background);
		
		textInput = (EditText) findViewById(R.id.search_text_input);
		textInput.setOnEditorActionListener(textInputOnEditorActionListener);
		
		buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(buttonSearchOnClickListener);
	}
	
	private OnClickListener buttonSearchOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String searchString = textInput.getText().toString().trim();
			if (validateSearchString(searchString)) {
				SearchOrder order = new SearchOrder(searchString);
				Intent intent = new Intent(thisActivity, SearchResultsActivity.class);
			    intent.putExtra(IntentHelper.INTENT_SEARCH_ORDER, order);
			    startActivity(intent);
			}
		}
	};
	
	private OnEditorActionListener textInputOnEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_NULL  
				      && event.getAction() == KeyEvent.ACTION_DOWN) { 
				buttonSearch.performClick();
			}
			return true;
		}
	};
	
	private boolean validateSearchString(String searchString) {
		if (searchString.compareTo("") == 0) {
			return false;
		}
		if (searchString.indexOf("*") != -1) {
			Toast toast = Toast.makeText(getApplicationContext(), 
					resources.getString(R.string.search_string_invalid_characters),
					Toast.LENGTH_SHORT);
		    	toast.show();
			return false;
		}
		
		int letters = searchString.length();
		if (letters < SEARCH_STRING_MIN_LETTERS || letters > SEARCH_STRING_MAX_LETTERS) {
			Toast toast = Toast.makeText(getApplicationContext(), 
				resources.getString(R.string.search_string_out_of_range) +
					" " + SEARCH_STRING_MIN_LETTERS + "-" +
					SEARCH_STRING_MAX_LETTERS, 
				Toast.LENGTH_SHORT);
	    	toast.show();
			return false;
		}
		return true;
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(resources.getColor(R.color.night_back));
//		textInput.setTextColor(resources.getColor(R.color.night_text));
		textInput.setBackgroundColor(resources.getColor(R.color.night_text));
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(resources.getColor(R.color.day_back));
//		textInput.setTextColor(resources.getColor(R.color.day_text));
		textInput.setBackgroundColor(resources.getColor(R.color.night_text));
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}
}
