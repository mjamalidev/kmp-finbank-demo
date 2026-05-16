package dev.mjamali.kmpfinbank.domain.repository

import dev.mjamali.kmpfinbank.domain.model.Account
import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.model.BankCard
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.model.Transaction
import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.result.Result

interface BankingRepository {

    suspend fun getAccounts(): Result<List<Account>, ApiErrorModel>

    suspend fun getAccountDetails(
        accountId: String
    ): Result<Account, ApiErrorModel>

    suspend fun getCards(): Result<List<BankCard>, ApiErrorModel>

    suspend fun getTransactions(): Result<List<Transaction>, ApiErrorModel>

    suspend fun transferMoney(
        transfer: Transfer
    ): Result<Receipt, ApiErrorModel>

    suspend fun getLastReceipt(): Receipt?
}