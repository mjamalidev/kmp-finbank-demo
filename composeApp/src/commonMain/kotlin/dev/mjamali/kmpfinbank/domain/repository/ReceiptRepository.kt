package dev.mjamali.kmpfinbank.domain.repository

import dev.mjamali.kmpfinbank.domain.model.Receipt

interface ReceiptRepository {
    suspend fun saveLastReceipt(receipt: Receipt)
    suspend fun getLastReceipt(): Receipt?
}