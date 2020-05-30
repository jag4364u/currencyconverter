package com.dj.challenge.currenyconverter.main.mvvm

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dj.challenge.currenyconverter.main.adapters.ConversionRatesRecyclerAdapter
import com.dj.challenge.currenyconverter.main.adapters.SpinnerCurrencyAdapter
import com.dj.challenge.currenyconverter.main.adapters.models.CurrencyInfo
import com.dj.challenge.currenyconverter.util.AlertActions
import com.mercari.remotedata.RemoteData
import kotlinx.android.synthetic.main.activity_main.view.*
import java.text.NumberFormat
import java.util.*

class MainViewProxy(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: MainContract.ViewModel,
    private val alertActions: AlertActions,
    private val spinnerAdapter: SpinnerCurrencyAdapter,
    private val recyclerViewAdapter: ConversionRatesRecyclerAdapter = ConversionRatesRecyclerAdapter()
) : MainContract.ViewProxy {

    override fun initView(view: View) {
        setupView(view)
        setupBindings()
    }

    @VisibleForTesting
    fun setupView(view: View) {
        view.sourceSelector.adapter = spinnerAdapter
        view.recycler_view.layoutManager = linearLayoutManager(view.context)
        view.recycler_view.adapter = recyclerViewAdapter
        view.sourceSelector.onItemSelectedListener = spinnerItemClickListener()
        view.amount.addTextChangedListener(textWatcher(view.amount))
    }

    @VisibleForTesting
    fun setupBindings() {
        viewModel.liveConversionRates.observe(lifecycleOwner, Observer {
            recyclerViewAdapter.setItems(it)
        })

        viewModel.liveCountryNames.observe(lifecycleOwner, Observer {
            when (it) {
                is RemoteData.Loading -> {
                    alertActions.showLoading()
                }
                is RemoteData.Success -> {
                    alertActions.hideLoading()
                    spinnerAdapter.setSpinnerItems(
                        it.value.currencies.names.map { nameInfo ->
                            CurrencyInfo(nameInfo.key, nameInfo.value)
                        }.toList()
                    )
                }
                is RemoteData.Failure -> {
                    alertActions.hideLoading()
                    alertActions.showToast(it.error.localizedMessage)
                }
            }
        })
    }

    @VisibleForTesting
    fun linearLayoutManager(context: Context) = LinearLayoutManager(context)

    @VisibleForTesting
    fun spinnerItemClickListener() = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Nothing to do
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            viewModel.setNewCurrency(spinnerAdapter.getItem(position).code)
        }
    }

    @VisibleForTesting
    fun textWatcher(amount: EditText) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            amount.removeTextChangedListener(this)
            var cleanString = s?.toString()
            if (cleanString == null) {
                recyclerViewAdapter.setAmount(1.0)
                return
            }
            cleanString = cleanString.replace("[$,.]".toRegex(), "")
            val value = cleanString.toDoubleOrNull() ?: 1.0
            recyclerViewAdapter.setAmount(value)

            val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(value)
            amount.setText(formatted)
            amount.setSelection(formatted.length);
            amount.addTextChangedListener(this)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Nothing to do
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Nothing to do
        }
    }
}