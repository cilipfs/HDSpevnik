package sk.suchac.hds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class SlideActivity extends Activity {
	
	private SlideActivity thisActivity = this;
	private TextView textField;
	private TextView goToNextSlide;
	private TextView goToPreviousSlide;
	private View background;
	
	private DAO datasource;
	
	// SLIDEFLOW napr. pre 4 odstavce: 12341
	private static final String SLIDE_BRAKE_TAG = "<sldbrk />";
	private static final String SLIDE_BRAKE_ALTERNATIVE = "<br /><br />";
	private List<String> slidesInOrder = new ArrayList<String>();
	private int slidesPointer = 0;
	
	public final static String INTENT_FOR_SETTINGS = "sk.suchac.hds.FOR_SETTINGS";
	
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
		goToNextSlide = (TextView) findViewById(R.id.slide_right_icon);
		goToPreviousSlide = (TextView) findViewById(R.id.slide_left_icon);
		
		goToNextSlide.setOnTouchListener(goToNextSlideTouchListener);
		goToPreviousSlide.setOnTouchListener(goToPreviousSlideTouchListener);
		
		Intent intent = getIntent();
		pickedSong = (PickedSongInfo) intent.getSerializableExtra(MainActivity.INTENT_PICKED_SONG);
		
		displayScriptureText();
		HistoryHelper.saveRecord(thisActivity, pickedSong.getSong());
        
		datasource.close();
	}
	
	private OnTouchListener goToNextSlideTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (slidesPointer + 1 != slidesInOrder.size()) {
				slidesPointer++;
				textField.setText(Html.fromHtml(slidesInOrder.get(slidesPointer)));
				setGoToSlideElementsVisibility();
			}
			return false;
		}
	};
	
	private OnTouchListener goToPreviousSlideTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (slidesPointer != 0) {
				slidesPointer--;
				textField.setText(Html.fromHtml(slidesInOrder.get(slidesPointer)));
				setGoToSlideElementsVisibility();
			}
			return false;
		}
	};
	
	private void setGoToSlideElementsVisibility() {
		if (slidesPointer == 0) {
			goToPreviousSlide.setVisibility(View.INVISIBLE);
		} else {
			goToPreviousSlide.setVisibility(View.VISIBLE);
		}
		
		if (slidesPointer + 1 == slidesInOrder.size()) {
			goToNextSlide.setVisibility(View.INVISIBLE);
		} else {
			goToNextSlide.setVisibility(View.VISIBLE);
		}
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
		textField.setTextSize(settings.getInt("presentationFontSize", 20));
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
	    		intent3.putExtra(SettingsActivity.INTENT_FOR_SETTINGS, true);
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
		String songText = song.getText();
		String[] slides = null;
		String slidesOrder = song.getSlideFlow();
		
		if (songText.indexOf(SLIDE_BRAKE_TAG) != -1) {
			slides = songText.split(SLIDE_BRAKE_TAG);
			trimSlides(slides);
		} else {
			slides = songText.split(SLIDE_BRAKE_ALTERNATIVE);
		}
		
		if (slidesOrder != null) {
			//Log.i(SlideActivity.class.getName(), "slidesOrder: " + slidesOrder);
			for (int i = 0; i < slidesOrder.length(); i++) {
				int orderNumber = Character.getNumericValue(slidesOrder.charAt(i));
				slidesInOrder.add(slides[orderNumber - 1]);
			}
		} else {
			//Log.i(SlideActivity.class.getName(), "slidesOrder: null");
			slidesInOrder = Arrays.asList(slides);
		}
		
		textField.setText(Html.fromHtml(slidesInOrder.get(slidesPointer)));
		setGoToSlideElementsVisibility();
		
		this.setTitle(song.getNumber() + " " + song.getTitle());
	}
	
	private void trimSlides(String[] slides) {
		for (int i = 0; i < slides.length; i++) {
			if (slides[i].indexOf(SLIDE_BRAKE_ALTERNATIVE) == 0) {
				slides[i] = slides[i].substring(0 + SLIDE_BRAKE_ALTERNATIVE.length());
			}
			if (slides[i].lastIndexOf(SLIDE_BRAKE_ALTERNATIVE) == slides[i].length() - SLIDE_BRAKE_ALTERNATIVE.length()) {
				slides[i] = slides[i].substring(0, slides[i].length() - SLIDE_BRAKE_ALTERNATIVE.length());
			}
		}
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
		background.setBackgroundColor(getResources().getColor(R.color.presentation_back));
    	textField.setTextColor(getResources().getColor(R.color.presentation_text));
    	goToNextSlide.setTextColor(getResources().getColor(R.color.night_text));
    	goToPreviousSlide.setTextColor(getResources().getColor(R.color.night_text));
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(getResources().getColor(R.color.day_back));
    	textField.setTextColor(getResources().getColor(R.color.day_text));
    	goToNextSlide.setTextColor(getResources().getColor(R.color.day_text));
    	goToPreviousSlide.setTextColor(getResources().getColor(R.color.day_text));
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}

}
