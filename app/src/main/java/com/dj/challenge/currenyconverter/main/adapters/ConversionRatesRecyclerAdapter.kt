package com.dj.challenge.currenyconverter.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dj.challenge.currenyconverter.R
import com.dj.challenge.currenyconverter.databinding.ConversionRateViewHolderBinding
import com.dj.challenge.currenyconverter.main.adapters.models.ConversionRateInfo
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ConversionRatesRecyclerAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var items: List<ConversionRateInfo> = ArrayList()

    private var amount: Double = 1.0

    private val lock = Any()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.conversion_rate_view_holder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val binding = ConversionRateViewHolderBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(amount, items[position])
    }

    fun setItems(newItems: List<ConversionRateInfo>) {
        synchronized(lock) {
            items = newItems
            notifyDataSetChanged()
        }
    }

    fun setAmount(newAmount: Double) {
        synchronized(lock) {
            amount = if (newAmount == 0.0) 1.0 else newAmount
        }
        notifyDataSetChanged()
    }
}

class ViewHolder(private val binding: ConversionRateViewHolderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(amount: Double, conversionRateInfo: ConversionRateInfo) {
        binding.conversionRate = conversionRateInfo
        val value = kotlin.math.floor((amount * conversionRateInfo.value) * 100) / 100
        val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(value)
        binding.finalAmount.text = itemView.context.getString(
            R.string.conversion_amount_format,
            conversionRateInfo.code,
            formatted
        )
    }
}