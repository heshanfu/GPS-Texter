/*
 * Copyright (C) 2017 Wiktor Nizio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.org.seva.texter.view.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen

import pl.org.seva.texter.R
import pl.org.seva.texter.view.activity.SettingsActivity

class SettingsFragment : PreferenceFragment() {

    var homeLocationClickedListener : (() -> Unit)? = null
    var smsEnabledClickedListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            preferenceScreen.removePreference(findPreference(SettingsActivity.CATEGORY_SMS))
        }
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        when (preference.key) {
            activity.getString(R.string.home_location_key) -> homeLocationClickedListener?.invoke()
            activity.getString(R.string.sms_enabled_key) -> smsEnabledClickedListener?.invoke()
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}