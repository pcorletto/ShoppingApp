package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.example.android.shoppingapp3.R;

/**
 * A {@link android.preference.PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        // TODO: Add preferences from XML
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        // TODO: Add preferences
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_language_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent1 = getIntent();

        // Get the name of the activity from which SettingsActivity was called.

        String calling_activity_name = intent1.getStringExtra(getString(R.string.calling_activity_name));
        String preceding_activity_name = intent1.getStringExtra(getString(R.string.preceding_activity_name));

        if(preceding_activity_name.equals("ui.SearchActivity")){//Return to SearchActivity

            Intent intent4 = new Intent(this, SearchActivity.class);
            intent4.putExtra(getString(R.string.calling_activity_name), calling_activity_name);
            startActivity(intent4);

        }

        else// If preceding activity name is not the Search Activity

        {

            if (calling_activity_name.equals("ui.DisplayListActivity")) {//Return to DisplayListActivity

                Intent intent2 = new Intent(this, DisplayListActivity.class);
                startActivity(intent2);

            } else if (calling_activity_name.equals("ui.DisplayCartActivity")) {//Return to DisplayCartActivity

                Intent intent3 = new Intent(this, DisplayCartActivity.class);
                startActivity(intent3);

            } else if (calling_activity_name.equals("ui.MainActivity")) {//Return to MainActivity

                Intent intent4 = new Intent(this, MainActivity.class);
                startActivity(intent4);

            } else if (calling_activity_name.equals("ui.PayActivity")){//Return to PayActivity

                Intent intent5 = new Intent(this, PayActivity.class);
                startActivity(intent5);
            }
        }
    }
}
