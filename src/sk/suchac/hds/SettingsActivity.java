package sk.suchac.hds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	
	private View background;
	private CheckBox cbKeepScreenOn;
	private TextView summKeepScreenOn;
	private TextView tvFontSize;
	private Button btnFontSize;
	private TextView summFontSize;
	private View layoutPresentationMode;
	private CheckBox cbPresentationMode;
	private TextView textPresentationMode;
	
	private Resources resources;
	
	public static final String PREFS = "HdsPrefsFile";
	private static boolean nightMode;
	public static final String SETTINGS_PREFS = "HdsSettingsPrefs";
	
	public final static String INTENT_FOR_SETTINGS = "sk.suchac.hds.FOR_SETTINGS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initializeElements();
		resources = getResources();
		
		SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
        cbKeepScreenOn.setChecked(settings.getBoolean("keepScreenOn", false));
        cbKeepScreenOn.setOnClickListener(keepScreenOnOnClickListener);
        
        btnFontSize.setText(String.valueOf(settings.getInt("fontSize", 18)));
        btnFontSize.setOnClickListener(btnFontSizeListener);
        
        cbPresentationMode.setChecked(settings.getBoolean("presentationMode", false));
        cbPresentationMode.setOnClickListener(presentationModeOnClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
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
		background = (View) findViewById(R.id.settings_layout);
		cbKeepScreenOn = (CheckBox) findViewById(R.id.setting_cb_keepScreenOn);
		summKeepScreenOn = (TextView) findViewById(R.id.setting_summ_keepScreenOn);
		tvFontSize = (TextView) findViewById(R.id.setting_tv_fontSize);
		btnFontSize = (Button) findViewById(R.id.setting_buttonFontSize);
		summFontSize = (TextView) findViewById(R.id.setting_summ_fontSize);
		layoutPresentationMode = (View) findViewById(R.id.lay_presentationMode);
		cbPresentationMode = (CheckBox) findViewById(R.id.setting_cb_presentationMode);
		textPresentationMode = (TextView) findViewById(R.id.setting_tv_presentationMode);
		
		Intent intent = getIntent();
		Boolean dontAllowManagePresentationMode = intent.getBooleanExtra(INTENT_FOR_SETTINGS, false);
		if (dontAllowManagePresentationMode) {
			layoutPresentationMode.setVisibility(View.GONE);
		}
	}
	
	private OnClickListener keepScreenOnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
		    SharedPreferences.Editor editor = settings.edit();
			if (cbKeepScreenOn.isChecked()) {
			    editor.putBoolean("keepScreenOn", true);
			    editor.commit();
			} else {
				editor.putBoolean("keepScreenOn", false);
			    editor.commit();
			}
		}
	};
	
	private OnClickListener presentationModeOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
		    SharedPreferences.Editor editor = settings.edit();
			if (cbPresentationMode.isChecked()) {
			    editor.putBoolean("presentationMode", true);
			    editor.commit();
			} else {
				editor.putBoolean("presentationMode", false);
			    editor.commit();
			}
		}
	};
	
	private OnClickListener btnFontSizeListener = new OnClickListener() {
	    public void onClick(View v) {
	      createDialogFontSize().show();
	    }
	};
	
	private AlertDialog createDialogFontSize() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.font_size_texts, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		int picked = resources.getIntArray(R.array.font_size)[which];
        		btnFontSize.setText(String.valueOf(picked));
        		SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
    		    SharedPreferences.Editor editor = settings.edit();
    		    editor.putInt("fontSize", picked);
			    editor.commit();
        	}
        });
		return builder.create();
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(resources.getColor(R.color.night_back));
		cbKeepScreenOn.setTextColor(resources.getColor(R.color.night_text));
		summKeepScreenOn.setTextColor(resources.getColor(R.color.night_text));
		tvFontSize.setTextColor(resources.getColor(R.color.night_text));
		summFontSize.setTextColor(resources.getColor(R.color.night_text));
		cbPresentationMode.setTextColor(resources.getColor(R.color.night_text));
		textPresentationMode.setTextColor(resources.getColor(R.color.night_text));
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(resources.getColor(R.color.day_back));
		cbKeepScreenOn.setTextColor(resources.getColor(R.color.day_text));
		summKeepScreenOn.setTextColor(resources.getColor(R.color.day_text));
		tvFontSize.setTextColor(resources.getColor(R.color.day_text));
		summFontSize.setTextColor(resources.getColor(R.color.day_text));
		cbPresentationMode.setTextColor(resources.getColor(R.color.day_text));
		textPresentationMode.setTextColor(resources.getColor(R.color.day_text));
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}

}
