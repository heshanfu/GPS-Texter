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

package pl.org.seva.texter.view.preference

import android.content.Context
import android.content.res.TypedArray
import android.preference.Preference
import android.util.AttributeSet
import android.util.Log

import pl.org.seva.texter.presenter.utils.Constants
import java.util.*

class HomeLocationPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return Constants.DEFAULT_HOME_LOCATION
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        val value: String
        if (restorePersistedValue) {
            value = getPersistedString(HOME_LOCATION)
            lat = parseLatitude(value)
            lon = parseLongitude(value)
        } else {
            value = Constants.DEFAULT_HOME_LOCATION
            persistString(value)
        }
        lat = parseLatitude(value)
        lon = parseLongitude(value)
    }

    override fun toString(): String {
        return toString(lat, lon)
    }

    companion object {

        private val TAG = HomeLocationPreference::class.java.simpleName

        private val HOME_LOCATION = "HOME_LOCATION"

        fun toString(lat: Double, lon: Double): String {
            val result = String.format(Locale.US, "geo:%.6f,%.6f", lat, lon)
            Log.d(TAG, result)
            return result
        }

        fun parseLatitude(geoUri: String): Double {
            Log.d(TAG, "Parse latitude: $geoUri")
            val str = geoUri.substring(geoUri.indexOf(":") + 1, geoUri.indexOf(","))
            return java.lang.Double.valueOf(str)!!
        }

        fun parseLongitude(geoUri: String): Double {
            Log.d(TAG, "Parse longitude: $geoUri")
            val str = geoUri.substring(geoUri.indexOf(",") + 1)
            return java.lang.Double.valueOf(str)!!
        }
    }
}