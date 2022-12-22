package com.hmncube.myweather.di

import com.hmncube.myweather.data.WeatherRepository
import com.hmncube.myweather.data.remote.OpenMateoService
import com.hmncube.myweather.data.remote.OpenWeatherService
import com.hmncube.myweather.data.remote.WeatherRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    //private const val BASE_URL = "https://api.open-meteo.com/v1/"
    private const val BASE_URL = "https://api.openweathermap.org/"
    //private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideOpenWeatherService(retrofit: Retrofit): OpenWeatherService =
        retrofit.create(OpenWeatherService::class.java)


    @Singleton
    @Provides
    fun provideOpenMateoService(retrofit: Retrofit): OpenMateoService =
        retrofit.create(OpenMateoService::class.java)


    @Singleton
    @Provides
    fun provideWeatherRemotedDataSource(openMateoService: OpenMateoService) = WeatherRemoteDataSource(openMateoService)
/*
    @Singleton
    @Provides
    fun providesWeatherRepository(openMateoService: OpenMateoService) = WeatherRepository()*/
}







