package dev.mjamali.kmpfinbank.di

import dev.mjamali.kmpfinbank.presentation.accounts.AccountsViewModel
import dev.mjamali.kmpfinbank.presentation.cards.CardsViewModel
import dev.mjamali.kmpfinbank.presentation.dashboard.DashboardViewModel
import dev.mjamali.kmpfinbank.presentation.login.LoginViewModel
import dev.mjamali.kmpfinbank.presentation.profile.ProfileViewModel
import dev.mjamali.kmpfinbank.presentation.transactions.TransactionsViewModel
import dev.mjamali.kmpfinbank.presentation.transfer.TransferViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::AccountsViewModel)
    viewModelOf(::CardsViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::TransferViewModel)
    viewModelOf(::ProfileViewModel)
}