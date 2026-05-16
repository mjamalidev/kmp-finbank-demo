package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.common.time.SessionTimer
import kotlinx.coroutines.flow.Flow

class ObserveSessionTimeoutUseCase(
    private val sessionTimer: SessionTimer
) {
    operator fun invoke(timeoutMillis: Long = 60_000L): Flow<Boolean> {
        return sessionTimer.observeTimeout(timeoutMillis)
    }
}