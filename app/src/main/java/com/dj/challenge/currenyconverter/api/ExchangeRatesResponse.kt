package com.dj.challenge.currenyconverter.api

import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("source")
    val source: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("quotes")
    internal val quotes: Quotes
)

data class Quotes(
    val rates: HashMap<String, Double>
)
