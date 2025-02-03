package ru.yandex.praktikumchatapp.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlin.math.pow

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    private var retryAttempt = 0

    fun getReplyMessage(
        firstRetryMillis: Long = 1000,
        retryDelayFactor: Double = 2.0,
        retries: Int = 3
    ): Flow<String> {
        return api.getReply()
            .onEach { retryAttempt = 0 }
            .retry { cause ->
                val retryDelay = firstRetryMillis * retryDelayFactor.pow(retryAttempt.toDouble())

                delay(retryDelay.toLong())
                retryAttempt++
                val retry = cause is Exception && retryAttempt < retries

                Log.d("mainLog", "attempt: $retryAttempt, delay: $retryDelay, retry: $retry")
                retry
            }
    }
}