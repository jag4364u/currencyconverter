package com.dj.challenge.currenyconverter.main

import android.content.Context
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dj.challenge.currenyconverter.api.CountryNamesResponse
import com.dj.challenge.currenyconverter.api.Currencies
import com.dj.challenge.currenyconverter.main.adapters.ConversionRatesRecyclerAdapter
import com.dj.challenge.currenyconverter.main.adapters.SpinnerCurrencyAdapter
import com.dj.challenge.currenyconverter.main.adapters.models.ConversionRateInfo
import com.dj.challenge.currenyconverter.main.adapters.models.CurrencyInfo
import com.dj.challenge.currenyconverter.main.mvvm.CountryNamesRemoteData
import com.dj.challenge.currenyconverter.main.mvvm.MainContract
import com.dj.challenge.currenyconverter.main.mvvm.MainViewProxy
import com.dj.challenge.currenyconverter.util.AlertActions
import com.mercari.remotedata.RemoteData
import com.nhaarman.mockitokotlin2.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class MainViewProxyTest {

    private val lifecycleOwner: LifecycleOwner = mock()
    private val viewModel: MainContract.ViewModel = mock()
    private val alertActions: AlertActions = mock()
    private val spinnerAdapter: SpinnerCurrencyAdapter = mock()
    private val recyclerViewAdapter: ConversionRatesRecyclerAdapter = mock()
    private val rootView: View = mock()

    private val recyclerView: RecyclerView = mock()
    private val editText: EditText = mock()
    private val spinner: AppCompatSpinner = mock()
    private val context: Context = mock()

    private val subject =
        spy(
            MainViewProxy(
                lifecycleOwner,
                viewModel,
                alertActions,
                spinnerAdapter,
                recyclerViewAdapter
            )
        )

    @Before
    fun setup() {
        whenever(rootView.recycler_view).thenReturn(recyclerView)
        whenever(rootView.sourceSelector).thenReturn(spinner)
        whenever(rootView.amount).thenReturn(editText)
        whenever(rootView.context).thenReturn(context)
    }

    @Test
    fun testInitView() {
        doNothing().whenever(subject).setupBindings()
        doNothing().whenever(subject).setupView(rootView)
        subject.initView(rootView)
        verify(subject).setupBindings()
        verify(subject).setupView(rootView)
    }

    @Test
    fun testSetupView() {
        val textWatcher = mock<TextWatcher>()
        whenever(subject.textWatcher(editText)).thenReturn(textWatcher)

        val ll = mock<LinearLayoutManager>()
        whenever(subject.linearLayoutManager(context)).thenReturn(ll)

        val spinnerClickListener = mock<AdapterView.OnItemSelectedListener>()
        whenever(subject.spinnerItemClickListener()).thenReturn(spinnerClickListener)

        subject.setupView(rootView)
        verify(spinner).adapter = spinnerAdapter
        verify(spinner).onItemSelectedListener = spinnerClickListener
        verify(recyclerView).adapter = recyclerViewAdapter
        verify(recyclerView).layoutManager = ll
        verify(editText).addTextChangedListener(textWatcher)
    }

    @Test
    fun testSetupBindings() {
        val liveData = mock<LiveData<List<ConversionRateInfo>>>()
        whenever(viewModel.liveConversionRates).thenReturn(liveData)

        val liveNamesMocked = mock<MutableLiveData<CountryNamesRemoteData>>()
        whenever(viewModel.liveCountryNames).thenReturn(liveNamesMocked)

        subject.setupBindings()

        val licycleOwnerCaptor = argumentCaptor<LifecycleOwner>()
        val observerListInfoCaptor = argumentCaptor<Observer<List<ConversionRateInfo>>>()
        val countryNamesObserverCaptor = argumentCaptor<Observer<CountryNamesRemoteData>>()

        verify(viewModel.liveConversionRates).observe(licycleOwnerCaptor.capture(), observerListInfoCaptor.capture())
        val fakeData = defaultConversionRates()
        observerListInfoCaptor.firstValue.onChanged(fakeData)
        verify(recyclerViewAdapter).setItems(fakeData)

        verify(viewModel.liveCountryNames).observe(licycleOwnerCaptor.capture(), countryNamesObserverCaptor.capture())
        countryNamesObserverCaptor.firstValue.onChanged(RemoteData.Loading())
        verify(alertActions).showLoading()

        val failure = RemoteData.Failure(Exception(""))
        countryNamesObserverCaptor.firstValue.onChanged(failure)
        verify(alertActions).hideLoading()
        verify(alertActions).showToast(failure.error.localizedMessage)

        countryNamesObserverCaptor.firstValue.onChanged(RemoteData.Success(defaultCountryNamesResponse()))
        verify(alertActions, times(2)).hideLoading()

        val currencyInfoCaptor = argumentCaptor<List<CurrencyInfo>>()
        verify(spinnerAdapter).setSpinnerItems(currencyInfoCaptor.capture())

        assert(currencyInfoCaptor.firstValue.size == 3) { "it should set all items to spinner" }
        assert(licycleOwnerCaptor.firstValue == lifecycleOwner) { "Owner should be from constructor" }
        assert(licycleOwnerCaptor.secondValue == lifecycleOwner) { "Owner should be from constructor" }
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