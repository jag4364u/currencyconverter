package com.dj.challenge.currenyconverter.main.logic

import androidx.annotation.VisibleForTesting

interface ConversionLogic {

    fun setRates(data: Map<String, Double>)

    fun getRatesForCountry(countryCode: String): HashMap<String, Double>
}

class ConversionLogicImpl: ConversionLogic {

    @VisibleForTesting
    val usdToOther = LinkedHashMap<String, Double>()

    override fun setRates(data: Map<String, Double>) {
        for ((key, value) in data) {
            usdToOther[key.substring(3)] = value
        }
    }

    override fun getRatesForCountry(countryCode: String): HashMap<String, Double> {
        val usdToCodeRate = usdToOther[countryCode] ?: 1.0
        val rates = LinkedHashMap<String, Double>()
        for ((code, value) in usdToOther) {
            rates[code] = value/usdToCodeRate
        }
        return rates
    }
}