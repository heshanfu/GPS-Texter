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

package pl.org.seva.texter

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.conf.global
import pl.org.seva.texter.presenter.*

import pl.org.seva.texter.source.ActivityRecognitionSource
import pl.org.seva.texter.source.LocationSource

open class TexterApplication : Application(), KodeinGlobalAware {

    val texterModule = Kodein.Module {
        bind<LocationSource>() with singleton { LocationSource() }
        bind<SmsSender>() with singleton { SmsSender() }
        bind<Timer>() with singleton { Timer() }
        bind<PermissionsHelper>() with singleton { PermissionsHelper() }
        bind<SmsHistory>() with singleton { SmsHistory() }
        bind<ActivityRecognitionSource>() with singleton { ActivityRecognitionSource() }
        bind<ZoneCalculator>() with singleton { ZoneCalculator() }
    }

    init {
        Kodein.global.addImport(texterModule)
    }

    private val locationSource: LocationSource = instance()
    private val activityRecognitionSource: ActivityRecognitionSource = instance()

    private var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()
        locationSource.initPreferences(this)
        activityRecognitionSource.initWithContext(this)
    }

    open fun hardwareCanSendSms() = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

    fun startService() {
        if (isServiceRunning) {
            return
        }
        startService(Intent(baseContext, TexterService::class.java))
        isServiceRunning = true
    }

    open fun stopService() {
        if (!isServiceRunning) {
            return
        }
        stopService(Intent(baseContext, TexterService::class.java))
        isServiceRunning = false
    }
}
