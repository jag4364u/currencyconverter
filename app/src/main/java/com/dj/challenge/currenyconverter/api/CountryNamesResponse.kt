package com.dj.challenge.currenyconverter.api

import com.google.gson.annotations.SerializedName

data class CountryNamesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("currencies")
    val currencies: Currencies
)

data class Currencies(
    val names: HashMap<String, String>
)