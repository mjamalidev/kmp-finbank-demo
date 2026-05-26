package dev.mjamali.kmpfinbank.domain.mapper

import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.result.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ToResourceTest {

    @Test
    fun mapsSuccessResultToSuccessResource() {
        val resource = Result.Success("data").toResource()

        assertTrue(resource is Resource.Success)
        assertEquals("data", resource.data)
    }

    @Test
    fun mapsErrorResultToErrorResource() {
        val resource = Result.Error(
            ApiErrorModel(
                code = 400,
                message = "Bad request"
            )
        ).toResource()

        assertTrue(resource is Resource.Error)
        assertNull(resource.throwable)
        assertEquals("Bad request", resource.message)
    }
}
