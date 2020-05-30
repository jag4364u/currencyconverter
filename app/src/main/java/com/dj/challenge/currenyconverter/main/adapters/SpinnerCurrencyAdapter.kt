package com.dj.challenge.currenyconverter.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import com.dj.challenge.currenyconverter.R
import com.dj.challenge.currenyconverter.main.adapters.models.CurrencyInfo
import kotlinx.android.synthetic.main.currency_spinner_view_holder.view.*

class SpinnerCurrencyAdapter(
    context: Context
) : BaseAdapter() {

    @VisibleForTesting
    var items: List<CurrencyInfo> = arrayListOf()

    private val layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: CurrencyViewHolder
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.currency_spinner_view_holder, parent, false)
            viewHolder = CurrencyViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as CurrencyViewHolder
        }
        viewHolder.bindCurrency(getItem(position))
        return view
    }

    override fun getItem(position: Int): CurrencyInfo {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    fun setSpinnerItems(currencies: List<CurrencyInfo>) {
        synchronized(this) {
            items = currencies
        }
        notifyDataSetChanged()
    }
}

class CurrencyViewHolder(view: View) {

    private val currencyTextView: TextView by lazy { view.currencyName }

    fun bindCurrency(data: CurrencyInfo) {
        currencyTextView.apply {
            text = context.getString(R.string.currency_name_format, data.name, data.code)
        }
    }
}