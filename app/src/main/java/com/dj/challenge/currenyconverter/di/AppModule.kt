package com.dj.challenge.currenyconverter.di

import android.app.Application
import android.content.Context
import com.dj.challenge.currenyconverter.api.ApiService
import com.dj.challenge.currenyconverter.util.WorkSchedulersImpl
import com.dj.challenge.currenyconverter.util.WorkerSchedulers
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun bindContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSchedulars(): WorkerSchedulers {
        return WorkSchedulersImpl()
    }
}