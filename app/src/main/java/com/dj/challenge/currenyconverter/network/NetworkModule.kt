package com.dj.challenge.currenyconverter.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.dj.challenge.currenyconverter.api.Currencies
import com.dj.challenge.currenyconverter.api.Quotes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideNetworkHttpClient(
        context: Context
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.cache(Cache(context.cacheDir, 50 * 1024 * 1024))
        client.addInterceptor { chain ->
            var request = chain.request()
            request = if (hasNetwork(context)!!)
            /*
            *  If there is Internet, get the cache that was stored 5 seconds ago.
            *  If the cache is older than 5 seconds, then discard it,
            *  and indicate an error in fetching the response.
            *  The 'max-age' attribute is responsible for this behavior.
            */
                request.newBuilder().header("Cache-Control", "public, max-age=" + 60 * 30).build()
            else
            /*
            *  If there is no Internet, get the cache that was stored 7 days ago.
            *  If the cache is older than 7 days, then discard it,
            *  and indicate an error in fetching the response.
            *  The 'max-stale' attribute is responsible for this behavior.
            *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
            */
                request.newBuilder().header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=" + 60 * 30
                ).build()
            // End of if-else statement

            // Add the modified request to the chain.
            chain.proceed(request)
        }
        return client.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(httpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Currencies::class.java, CurrencyNamesDeserializer())
            .registerTypeAdapter(Quotes::class.java, CurrencyRateDeserializer())
            .setLenient()
            .create()
    }

    private fun hasNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    companion object {
        private const val BASE_URL = "http://api.currencylayer.com/"
    }
}