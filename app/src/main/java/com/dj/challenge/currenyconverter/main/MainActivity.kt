package com.dj.challenge.currenyconverter.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dj.challenge.currenyconverter.CurrencyConverterApp
import com.dj.challenge.currenyconverter.R
import com.dj.challenge.currenyconverter.main.mvvm.MainContract
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewProxy: MainContract.ViewProxy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as CurrencyConverterApp).activityInjector.inject(this)
        viewProxy.initView(root_view)
    }
}
