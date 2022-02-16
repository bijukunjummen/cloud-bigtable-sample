package org.bk.notification

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink

fun <V> ApiFuture<V>.toMono(): Mono<V> {
    return Mono.create { sink: MonoSink<V> ->
        val callback = object : ApiFutureCallback<V> {
            override fun onFailure(t: Throwable) {
                sink.error(t)
            }

            override fun onSuccess(result: V) {
                sink.success(result)
            }
        }
        ApiFutures.addCallback(this, callback, Runnable::run)
    }
}