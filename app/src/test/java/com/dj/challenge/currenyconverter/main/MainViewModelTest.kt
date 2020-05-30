package com.dj.challenge.currenyconverter.main

import androidx.lifecycle.MutableLiveData
import com.dj.challenge.currenyconverter.api.*
import com.dj.challenge.currenyconverter.main.adapters.models.ConversionRateInfo
import com.dj.challenge.currenyconverter.main.logic.ConversionLogic
import com.dj.challenge.currenyconverter.main.mvvm.CountryNamesRemoteData
import com.dj.challenge.currenyconverter.main.mvvm.MainViewModel
import com.dj.challenge.currenyconverter.util.WorkerSchedulers
import com.mercari.remotedata.RemoteData
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class MainViewModelTest {

    private val apiService = mock<ApiService>()
    private val disposables = mock<CompositeDisposable>()
    private val conversionLogic = mock<ConversionLogic>()
    private val workerSchedulers = mock<WorkerSchedulers>()

    private val subject =
        spy(MainViewModel(apiService, conversionLogic, disposables, workerSchedulers))

    @Before
    fun setup() {
        whenever(workerSchedulers.io()).thenReturn(Schedulers.trampoline())
        whenever(workerSchedulers.main()).thenReturn(Schedulers.trampoline())
    }

    @Test
    fun `onCreate should load exchange rates if it is for the first time`() {
        doNothing().whenever(subject).loadExchangeRates()
        subject.onCreate()
        verify(subject).loadExchangeRates()
    }

    @Test
    fun `onCreate should not load exchange rates if it is already loaded`() {
        subject.liveCountryNamesMutableData =
            MutableLiveData(RemoteData.Success(defaultCountryNamesResponse()))
        doNothing().whenever(subject).loadExchangeRates()
        subject.onCreate()
        verify(subject, never()).loadExchangeRates()
    }

    @Test
    fun `setNewCurrency should fetch new conversion rates and broadcast`() {
        val mocked = mock<MutableLiveData<List<ConversionRateInfo>>>()
        subject.liveConversionRatesMutableData = mocked

        val newRates = hashMapOf("ABC" to 1.0, "DEF" to 2.0, "GHI" to 4.0)
        val conversionRates = defaultConversionRates()
        doReturn(conversionRates).whenever(subject).conversionInfoMapper(newRates)
        whenever(conversionLogic.getRatesForCountry("ABC")).thenReturn(newRates)

        subject.setNewCurrency("ABC")
        verify(conversionLogic).getRatesForCountry("ABC")
        verify(mocked).postValue(conversionRates)
    }

    @Test
    fun `loadExchangeRates should fetch exchange rates from api`() {
        val response = defaultExchangeRateResponse()
        whenever(apiService.getExchangeRates()).thenReturn(Single.just(response))
        doNothing().whenever(subject).loadSupportedCountries()
        subject.loadExchangeRates()
        verify(conversionLogic).setRates(response.quotes.rates)
        verify(subject).loadSupportedCountries()
    }

    @Test
    fun `loadExchangeRates should not call #setRates on error`() {
        whenever(apiService.getExchangeRates()).thenReturn(Single.error(Exception("Error")))
        subject.loadExchangeRates()
        verify(conversionLogic, never()).setRates(any())
        verify(subject, never()).loadSupportedCountries()
    }

    @Test
    fun `loadSupportedCountries should fetch country names from api`() {
        val mocked = mock<MutableLiveData<CountryNamesRemoteData>>()
        subject.liveCountryNamesMutableData = mocked

        val response = defaultCountryNamesResponse()
        whenever(apiService.getCountryNames()).thenReturn(Single.just(response))
        subject.loadSupportedCountries()
        verify(mocked, times(2)).postValue(any())
    }

    @Test
    fun `loadSupportedCountries should throw error if api has error`() {
        val mocked = mock<MutableLiveData<CountryNamesRemoteData>>()
        subject.liveCountryNamesMutableData = mocked

        whenever(apiService.getCountryNames()).thenReturn(Single.error(Exception("Error")))
        subject.loadSupportedCountries()
        verify(mocked, times(2)).postValue(any())
    }

    @Test
    fun `conversionInfoMapper should convert exchange rates to models`() {
        val mocked = mock<MutableLiveData<CountryNamesRemoteData>>()
        whenever(mocked.value).thenReturn(RemoteData.Success(defaultCountryNamesResponse()))
        subject.liveCountryNamesMutableData = mocked

        val rates = linkedMapOf("ABC" to 1.0, "DEF" to 2.0, "GHI" to 4.0)
        val list = subject.conversionInfoMapper(rates)
        assert(list.size == 3) { "there should 3 currencies" }
        assert(list[0].code == "ABC") { "code should be correct" }
        assert(list[0].name == "abc") { "name should be correct" }
        assert(list[0].value == 1.0) { "rate should be correct" }
        assert(list[1].code == "DEF") { "code should be correct" }
        assert(list[1].name == "def") { "name should be correct" }
        assert(list[1].value == 2.0) { "rate should be correct" }
    }

    private fun defaultExchangeRateResponse(): ExchangeRatesResponse {
        return ExchangeRatesResponse(
            success = true,
            source = "USD",
            timestamp = 123456,
            quotes = Quotes(hashMapOf("USDABC" to 500.0, "USDDEF" to 1000.0, "USDGHI" to 2000.0))
        )
    }

    private fun defaultCountryNamesResponse(): CountryNamesResponse {
        return CountryNamesResponse(
            success = true,
            currencies = Currencies(hashMapOf("ABC" to "abc", "DEF" to "def", "GHI" to "ghi"))
        )
    }

    private fun defaultConversionRates(): List<ConversionRateInfo> {
        val list = ArrayList<ConversionRateInfo>()
        list.add(ConversionRateInfo("ABC", "Apple Bat Car", 1.0))
        list.add(ConversionRateInfo("DEF", "Doll Ear Fan", 2.0))
        list.add(ConversionRateInfo("GHI", "Goat Horse Iron", 4.0))
        return list
    }
}