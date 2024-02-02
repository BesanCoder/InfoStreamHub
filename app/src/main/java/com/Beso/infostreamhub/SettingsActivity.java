package com.Beso.infostreamhub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check if the dark theme is enabled in preferences
        boolean isDarkThemeEnabled = new NewsPreferenceFragment().isDarkThemeEnabled(this);

        // Set the theme based on user preference
        if (isDarkThemeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    // The user can edit the app settings in the PreferenceFragment.
    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // Check if the dark theme is enabled in preferences
            boolean isDarkThemeEnabled = isDarkThemeEnabled((AppCompatActivity) getActivity());

            // Set the theme based on user preference
            if (isDarkThemeEnabled) {
                getActivity().setTheme(R.style.AppTheme_Dark);
            }

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // The first editable preference is the search keyword, to narrow down the articles.
            Preference searchContent = findPreference(getString(R.string.settings_edit_text_key));
            bindPreferenceSummaryToValue(searchContent);

            // The second editable preference is the news article order.
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            // The third editable preference is the news article number displayed.
            Preference articleNumber = findPreference(getString(R.string.settings_article_number_key));
            bindPreferenceSummaryToValue(articleNumber);

            // The dark theme preference
            Preference darkTheme = findPreference(getString(R.string.settings_dark_theme_key));
            bindPreferenceSummaryToValue(darkTheme);
        }

        // Helper method to check if dark theme is enabled
        public static boolean isDarkThemeEnabled(AppCompatActivity activity) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            return preferences.getBoolean(activity.getString(R.string.settings_dark_theme_key), false);
        }

        @Override
        /* This method modifies the news article displayed according to the changes made by the
         * user.
         */
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals(getString(R.string.settings_article_number_key))) {
                    if (Integer.parseInt(stringValue) > 0 && Integer.parseInt(stringValue) <= 50)
                        preference.setSummary(stringValue);
                    else if (Integer.parseInt((stringValue)) <= 0) preference.setSummary("1");
                    else preference.setSummary("50");
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            if (preference instanceof EditTextPreference) {
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            } else if (preference instanceof ListPreference) {
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            } else if (preference instanceof CheckBoxPreference) {
                boolean preferenceBoolean = preferences.getBoolean(preference.getKey(), false);
                onPreferenceChange(preference, preferenceBoolean);
            }
        }

    }
}
