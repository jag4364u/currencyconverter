package com.dj.challenge.currenyconverter.main.mvvm

import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import com.dj.challenge.currenyconverter.api.CountryNamesResponse
import com.dj.challenge.currenyconverter.main.adapters.models.ConversionRateInfo
import com.mercari.remotedata.RemoteData

interface MainContract {

    interface ViewProxy {
        fun initView(view: View)
    }

    interface ViewModel : LifecycleObserver {
        val liveConversionRates: LiveData<List<ConversionRateInfo>>
        val liveCountryNames: LiveData<RemoteData<CountryNamesResponse, Exception>>

        fun setNewCurrency(currencySource: String)
    }

//    interface Navigator {
//        // do we need it?
//    }
}