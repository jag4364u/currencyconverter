package com.dj.challenge.currenyconverter.main.mvvm

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.dj.challenge.currenyconverter.api.ApiService
import com.dj.challenge.currenyconverter.api.CountryNamesResponse
import com.dj.challenge.currenyconverter.api.ExchangeRatesResponse
import com.dj.challenge.currenyconverter.main.adapters.models.ConversionRateInfo
import com.dj.challenge.currenyconverter.main.logic.ConversionLogic
import com.dj.challenge.currenyconverter.util.WorkerSchedulers
import com.dj.challenge.currenyconverter.util.transformToRemoteData
import com.mercari.remotedata.RemoteData
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

typealias CountryNamesRemoteData = RemoteData<CountryNamesResponse, Exception>

class MainViewModel(
    private val apiService: ApiService,
    private val conversionLogic: ConversionLogic,
    private val compositeDisposable: CompositeDisposable,
    private val workerSchedulers: WorkerSchedulers
) : ViewModel(), MainContract.ViewModel {

    @VisibleForTesting
    var liveConversionRatesMutableData = MutableLiveData<List<ConversionRateInfo>>()
    override val liveConversionRates: LiveData<List<ConversionRateInfo>>
        get() = liveConversionRatesMutableData

    @VisibleForTesting
    var liveCountryNamesMutableData = MutableLiveData<CountryNamesRemoteData>()
    override val liveCountryNames: LiveData<CountryNamesRemoteData>
        get() = liveCountryNamesMutableData

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (liveCountryNamesMutableData.value?.isSuccess != true) {
            loadExchangeRates()
        }
    }

    override fun setNewCurrency(currencySource: String) {
        Single.fromCallable { conversionLogic.getRatesForCountry(currencySource) }
            .map { conversionInfoMapper(it) }
            .subscribeOn(workerSchedulers.io())
            .observeOn(workerSchedulers.main())
            .subscribe { rateList ->
                liveConversionRatesMutableData.postValue(rateList)
            }
            .apply {
                compositeDisposable.add(this)
            }
    }

    @VisibleForTesting
    fun loadExchangeRates() {
        apiService.getExchangeRates()
            .compose(transformToRemoteData<ExchangeRatesResponse>())
            .subscribeOn(workerSchedulers.io())
            .observeOn(workerSchedulers.main())
            .subscribe { remoteData ->
                when (remoteData) {
                    is RemoteData.Success -> {
                        conversionLogic.setRates(remoteData.value.quotes.rates)
                        loadSupportedCountries()
                    }
                }
            }
            .apply {
                compositeDisposable.add(this)
            }
    }

    @VisibleForTesting
    fun loadSupportedCountries() {
        apiService.getCountryNames()
            .doOnSubscribe { liveCountryNamesMutableData.postValue(RemoteData.Loading()) }
            .compose(transformToRemoteData<CountryNamesResponse>())
            .subscribeOn(workerSchedulers.io())
            .observeOn(workerSchedulers.main())
            .subscribe(liveCountryNamesMutableData::postValue)
            .apply {
                compositeDisposable.add(this)
            }
    }

    @VisibleForTesting
    fun conversionInfoMapper(rateList: HashMap<String, Double>): List<ConversionRateInfo> {
        val result = ArrayList<ConversionRateInfo>()
        when (val codesToNamesList = liveCountryNamesMutableData.value) {
            is RemoteData.Success -> {
                val codesToNames = codesToNamesList.value.currencies.names
                rateList.map { (code, rate) ->
                    ConversionRateInfo(
                        code,
                        codesToNames[code] ?: "No Name",
                        rate
                    )
                }.toCollection(result)
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}