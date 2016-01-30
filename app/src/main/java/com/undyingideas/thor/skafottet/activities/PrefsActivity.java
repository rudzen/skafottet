/*
 * Copyright 2016 Rudy Alex Kohn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.undyingideas.thor.skafottet.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

public class PrefsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.skafottet_preferences);
        final EditTextPreference playerNameEdit = (EditTextPreference) findPreference(Constant.KEY_PREFS_PLAYER_NAME);
        playerNameEdit.setSummary(GameUtility.s_preferences.getString(Constant.KEY_PREFS_PLAYER_NAME, "Mig"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        GameUtility.settings.prefsMusic = GameUtility.s_preferences.getBoolean(Constant.KEY_PREFS_MUSIC);
        GameUtility.settings.prefsSfx = GameUtility.s_preferences.getBoolean(Constant.KEY_PREFS_SFX);
        GameUtility.settings.prefsBlood = GameUtility.s_preferences.getBoolean(Constant.KEY_PREFS_BLOOD);
        GameUtility.settings.prefsColour = GameUtility.s_preferences.getInt(Constant.KEY_PREFS_COLOUR, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? ContextCompat.getColor(getApplicationContext(), R.color.colorAccent) : getResources().getColor(R.color.colorAccent));
        GameUtility.settings.prefsHeptic = GameUtility.s_preferences.getBoolean(Constant.KEY_PREFS_HEPTIC);
        GameUtility.settings.keepLogin = GameUtility.s_preferences.getBoolean(Constant.KEY_PREFS_KEEP_LOGIN);
        if (!GameUtility.settings.keepLogin) {
            GameUtility.settings.lastPw = null;
        }

        final String newName = GameUtility.s_preferences.getString(Constant.KEY_PREFS_PLAYER_NAME);
        if (!newName.isEmpty() && !newName.equalsIgnoreCase(GameUtility.me.getName())) {
            GameUtility.me.setName(newName);
        }
        GameUtility.settings.setContrastColor();
        startActivity(new Intent(this, GameActivity.class));
        super.finish();
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Preference preference = findPreference(key);
        if (preference instanceof EditTextPreference) {
            final EditTextPreference editTextPreference = (EditTextPreference) preference;
            preference.setSummary(editTextPreference.getText());
        }
    }
}
