package com.dj.challenge.currenyconverter.api

import com.dj.challenge.currenyconverter.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {

    @GET("live?access_key=${BuildConfig.API_KEY}&format=1&source=USD")
    fun getExchangeRates(): Single<ExchangeRatesResponse>

    @GET("list?access_key=${BuildConfig.API_KEY}&format=1")
    fun getCountryNames(): Single<CountryNamesResponse>
}