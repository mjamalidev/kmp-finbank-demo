package dev.mjamali.kmpfinbank.common.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SessionTimer {

    fun observeTimeout(timeoutMillis: Long = 60_000L): Flow<Boolean> = flow {
        delay(timeoutMillis)
        emit(true)
    }
}