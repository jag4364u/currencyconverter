package com.dj.challenge.currenyconverter.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.dj.challenge.currenyconverter.api.ApiService
import com.dj.challenge.currenyconverter.di.ActivityScope
import com.dj.challenge.currenyconverter.main.adapters.SpinnerCurrencyAdapter
import com.dj.challenge.currenyconverter.main.logic.ConversionLogic
import com.dj.challenge.currenyconverter.main.logic.ConversionLogicImpl
import com.dj.challenge.currenyconverter.main.mvvm.MainContract
import com.dj.challenge.currenyconverter.main.mvvm.MainViewModel
import com.dj.challenge.currenyconverter.main.mvvm.MainViewProxy
import com.dj.challenge.currenyconverter.util.AlertActions
import com.dj.challenge.currenyconverter.util.AlertActionsImpl
import com.dj.challenge.currenyconverter.util.WorkerSchedulers
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class MainModule {

    @ActivityScope
    @Provides
    fun provideView(
        viewModel: MainContract.ViewModel,
        alertActions: AlertActions,
        activity: MainActivity
    ): MainContract.ViewProxy {
        activity.lifecycle.addObserver(viewModel)
        return MainViewProxy(
            activity,
            viewModel,
            alertActions,
            SpinnerCurrencyAdapter(activity)
        )
    }

    @ActivityScope
    @Provides
    fun provideViewModel(
        activity: MainActivity,
        apiService: ApiService,
        conversionLogic: ConversionLogic,
        workerSchedulers: WorkerSchedulers
    ): MainContract.ViewModel {
        return ViewModelProviders.of(activity, MainViewModelFactory(apiService, conversionLogic, workerSchedulers))[MainViewModel::class.java]
    }

    @ActivityScope
    @Provides
    fun provideAlertActions(activity: MainActivity): AlertActions {
        return AlertActionsImpl(activity)
    }

    @ActivityScope
    @Provides
    fun provideConversionLogic(): ConversionLogic {
        return ConversionLogicImpl()
    }

    class MainViewModelFactory(
        private val apiService: ApiService,
        private val conversionLogic: ConversionLogic,
        private val workerSchedulers: WorkerSchedulers
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            try {
                return MainViewModel(apiService, conversionLogic, CompositeDisposable(), workerSchedulers) as T
            } catch (e: Exception) {
            }
            return super.create(modelClass)
        }
    }
}