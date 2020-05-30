package com.dj.challenge.currenyconverter.di

import com.dj.challenge.currenyconverter.main.MainActivity
import com.dj.challenge.currenyconverter.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun mainActivity(): MainActivity
}