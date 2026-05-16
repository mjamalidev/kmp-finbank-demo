package dev.mjamali.kmpfinbank.data.mock

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

fun BankingMockEngine() = MockEngine { request ->
    val path = request.url.encodedPath

    val jsonHeaders = headersOf(
        HttpHeaders.ContentType,
        "application/json"
    )

    when {
        path.endsWith("/login") -> respond(
            content = """
        {
          "accessToken": "mock-access-token-123",
          "refreshToken": "mock-refresh-token-valid-for-one-month",
          "userName": "KMP Demo User"
        }
    """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = jsonHeaders
        )

        path.endsWith("/refresh") -> respond(
            content = """
        {
          "accessToken": "mock-access-token-from-biometric-login",
          "userName": "KMP Demo User"
        }
    """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = jsonHeaders
        )

        path.endsWith("/accounts") -> respond(
            content = """
                [
                  {
                    "id": "acc_1",
                    "name": "Main Account",
                    "iban": "GB29NWBK60161331926819",
                    "balanceMinor": 245075,
                    "currency": "USD"
                  },
                  {
                    "id": "acc_2",
                    "name": "Savings Account",
                    "iban": "GB29NWBK60161331926817",
                    "balanceMinor": 980050,
                    "currency": "USD"
                  }
                ]
            """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = jsonHeaders
        )

        path.endsWith("/cards") -> respond(
            content = """
                [
                  {
                    "id": "card_1",
                    "holderName": "KMP Demo User",
                    "cardNumber": "4111111111111111",
                    "expiry": "12/28",
                    "type": "Debit",
                    "isFrozen": false
                  },
                  {
                    "id": "card_2",
                    "holderName": "KMP Demo User",
                    "cardNumber": "5555555555554444",
                    "expiry": "09/27",
                    "type": "Virtual",
                    "isFrozen": true
                  }
                ]
            """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = jsonHeaders
        )

        path.endsWith("/transactions") -> respond(
            content = """
                [
                  {
                    "id": "tx_1",
                    "accountId": "acc_1",
                    "title": "Online Shopping",
                    "amountMinor": 12500,
                    "category": "Shopping",
                    "date": "2026-05-01",
                    "type": "Expense"
                  },
                  {
                    "id": "tx_2",
                    "accountId": "acc_1",
                    "title": "Salary",
                    "amountMinor": 45100,
                    "category": "Salary",
                    "date": "2026-05-02",
                    "type": "Income"
                  },
                  {
                    "id": "tx_3",
                    "accountId": "acc_2",
                    "title": "Electricity Bill",
                    "amountMinor": 7600,
                    "category": "Bills",
                    "date": "2026-05-03",
                    "type": "Expense"
                  }
                ]
            """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = jsonHeaders
        )

        path.endsWith("/transfer") -> respond(
            content = """
                {
                  "id": "receipt_1",
                  "trackingCode": "TRX-20260505-001",
                  "fromAccountId": "acc_1",
                  "toAccountNumber": "**** 4821",
                  "amountMinor": 50000,
                  "date": "2026-05-05",
                  "status": "Success"
                }
            """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = jsonHeaders
        )

        else -> respond(
            content = """{"code":404,"message":"Not found"}""",
            status = HttpStatusCode.NotFound,
            headers = jsonHeaders
        )
    }
}