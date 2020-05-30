package com.dj.challenge.currenyconverter.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface WorkerSchedulers {

    fun io(): Scheduler

    fun main(): Scheduler
}

class WorkSchedulersImpl: WorkerSchedulers {

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun main(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}