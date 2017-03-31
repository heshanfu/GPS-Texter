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

package pl.org.seva.texter.dagger;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;
import pl.org.seva.texter.manager.HistoryManager;
import pl.org.seva.texter.manager.SmsManager;
import pl.org.seva.texter.mockmanager.MockGpsManager;
import pl.org.seva.texter.manager.ActivityRecognitionManager;
import pl.org.seva.texter.manager.GpsManager;
import pl.org.seva.texter.manager.TimerManager;
import pl.org.seva.texter.mockmanager.MockSmsManager;

@Module
class MockTexterModule {

    @Provides
    @Singleton
    GpsManager provideGpsManager(TimerManager timerManager) {
        return new MockGpsManager(timerManager);
    }

    @Provides
    @Singleton
    SmsManager provideSmsManager(GpsManager gpsManager, HistoryManager historyManager) {
        return new MockSmsManager(gpsManager, historyManager);
    }

    @Provides
    @Singleton
    ActivityRecognitionManager provideActivityRecognitionManager() {
        ActivityRecognitionManager result = Mockito.mock(ActivityRecognitionManager.class);
        mockReturnValues(result);
        return result;
    }

    private void mockReturnValues(ActivityRecognitionManager activityRecognitionManager) {
        Mockito.when(activityRecognitionManager.stationaryListener()).thenReturn(PublishSubject.create().hide());
        Mockito.when(activityRecognitionManager.movingListener()).thenReturn(PublishSubject.create().hide());
    }
}
