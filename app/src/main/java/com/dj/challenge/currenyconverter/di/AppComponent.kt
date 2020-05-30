package com.dj.challenge.currenyconverter.di

import com.dj.challenge.currenyconverter.CurrencyConverterApp
import com.dj.challenge.currenyconverter.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        ActivityBindingModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<CurrencyConverterApp> {

    @Component.Builder
    interface Builder {

        fun appModule(appModule: AppModule): Builder

        fun build(): AppComponent
    }
}