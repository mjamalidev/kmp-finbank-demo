package dev.mjamali.kmpfinbank.domain.usecase

import kotlinx.coroutines.flow.Flow
import dev.mjamali.kmpfinbank.common.time.SessionTimer

class ObserveSessionTimeoutUseCase(
    private val sessionTimer: SessionTimer
) {
    operator fun invoke(timeoutMillis: Long = 60_000L): Flow<Boolean> {
        return sessionTimer.observeTimeout(timeoutMillis)
    }
}