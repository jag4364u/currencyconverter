package com.dj.challenge.currenyconverter.util

import com.mercari.remotedata.RemoteData
import io.reactivex.Single
import io.reactivex.SingleTransformer

/**
 * transform response to remote data
 */
fun <T : Any> transformToRemoteData(): SingleTransformer<T, RemoteData<T, Exception>> {
    return SingleTransformer { upStreamObservable ->
        upStreamObservable
            .flatMap { response -> Single.just(RemoteData.Success(response) as RemoteData<T, Exception>) }
            .onErrorResumeNext { error ->
                when (error) {
                    is Exception -> RemoteData.Failure(error)
                    else -> {
                        RemoteData.Failure(error = Exception(""))
                    }
                }.let {
                    Single.just(it)
                }
            }
    }
}