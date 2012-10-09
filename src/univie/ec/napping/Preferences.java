package univie.ec.napping;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	private EditTextPreference mTiming;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            
            mTiming = (EditTextPreference)getPreferenceScreen().findPreference("timingPref");
            setSummary();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("timingPref")) {
            mTiming.setSummary(sharedPreferences.getString("timingPref", "3000")+" ms");
            }
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        setSummary();
	}

	@Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }
	
	 private void setSummary()
	 {
	     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	     String timing = prefs.getString("timingPref",  "3000" )+" ms";
	     mTiming.setSummary(timing);
	 }
}
