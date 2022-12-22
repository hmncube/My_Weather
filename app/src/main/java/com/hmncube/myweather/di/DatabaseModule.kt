package com.hmncube.myweather.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.hmncube.myweather.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    /*@Provides
    fun provideWeatherDao(appDatabase: AppDatabase) : WeatherDao {
        return appDatabase.weatherDao()
    }

    @Provides
    fun provideDailyDao(appDatabase: AppDatabase) : DailyDao {
        return appDatabase.dailyDao()
    }

    @Provides
    fun provideDailyUnitsDao(appDatabase: AppDatabase) : DailyUnitsDao {
        return appDatabase.dailyUnitsDao()
    }

    @Provides
    fun provideHourlyDao(appDatabase: AppDatabase) : HourlyDao {
        return appDatabase.hourlyDao()
    }

    @Provides
    fun provideHourlyUnitsDao(appDatabase: AppDatabase) : HourlyUnitsDao {
        return appDatabase.hourlyUnitsDao()
    }
*/
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase(context)
    }
}
