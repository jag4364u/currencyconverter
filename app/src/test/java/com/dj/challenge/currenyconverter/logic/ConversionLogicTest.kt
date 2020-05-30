package com.dj.challenge.currenyconverter.logic

import com.dj.challenge.currenyconverter.main.logic.ConversionLogicImpl
import org.junit.Test

class ConversionLogicTest {

    private val subject = ConversionLogicImpl()

    @Test
    fun testSetRates() {
        val data = hashMapOf("ABCDEF" to 1234.0, "ABCGHI" to 1000.0, "ABCIOS" to 2000.0)
        subject.setRates(data)
        assert(subject.usdToOther.isNotEmpty()) { "setRates should build array with values" }
        assert(subject.usdToOther.size == 3) { "Rates array should have all items in it" }
        assert(subject.usdToOther["DEF"] == 1234.0) { "Values should be saved with target key" }
        assert(subject.usdToOther["IOS"] == 2000.0) { "Values should be saved with target key" }
    }

    @Test
    fun testGetRatesForCountry() {
        val data = hashMapOf("ABCDEF" to 500.0, "ABCGHI" to 1000.0, "ABCIOS" to 2000.0)
        subject.setRates(data)
        val list = subject.getRatesForCountry("DEF")
        assert(list.isNotEmpty()) { "array should not be empty" }
        assert(list.size == 3) { "Rates array should have all items in it" }
        assert(list["DEF"] == 1.0) { "Values should have correct fractions" }
        assert(list["IOS"] == 4.0) { "Values should have correct fractions" }
    }
}