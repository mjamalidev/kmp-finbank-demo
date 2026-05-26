package dev.mjamali.kmpfinbank.common.time

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SessionTimerTest {

    @Test
    fun emitsTimeoutAfterConfiguredDelay() = runTest {
        val result = SessionTimer()
            .observeTimeout(timeoutMillis = 1_000L)
            .first()

        assertTrue(result)
    }
}
