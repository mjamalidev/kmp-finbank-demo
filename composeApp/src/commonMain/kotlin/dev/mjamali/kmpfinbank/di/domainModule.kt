package dev.mjamali.kmpfinbank.di

import dev.mjamali.kmpfinbank.domain.usecase.GetAccountDetailsUseCase
import dev.mjamali.kmpfinbank.domain.usecase.GetAccountsUseCase
import dev.mjamali.kmpfinbank.domain.usecase.GetCardsUseCase
import dev.mjamali.kmpfinbank.domain.usecase.GetLastReceiptUseCase
import dev.mjamali.kmpfinbank.domain.usecase.GetTransactionsUseCase
import dev.mjamali.kmpfinbank.domain.usecase.LoadDashboardUseCase
import dev.mjamali.kmpfinbank.domain.usecase.LoginUseCase
import dev.mjamali.kmpfinbank.domain.usecase.LogoutUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ObserveAccessTokenUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ObserveBalanceVisibilityUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ObserveHasLocalSessionUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ObserveSessionTimeoutUseCase
import dev.mjamali.kmpfinbank.domain.usecase.RefreshLoginUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ToggleBalanceVisibilityUseCase
import dev.mjamali.kmpfinbank.domain.usecase.TransferMoneyUseCase
import dev.mjamali.kmpfinbank.domain.usecase.UpdateLastActiveAtUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ValidateTransferUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }

    factory { LoadDashboardUseCase(get()) }
    factory { GetAccountDetailsUseCase(get()) }
    factory { GetCardsUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }
    factory { GetLastReceiptUseCase(get()) }

    factory { ValidateTransferUseCase() }
    factory { TransferMoneyUseCase(get(), get()) }

    factory { ObserveBalanceVisibilityUseCase(get()) }
    factory { ToggleBalanceVisibilityUseCase(get()) }

    factory { ObserveSessionTimeoutUseCase(get()) }
    factory { UpdateLastActiveAtUseCase(get()) }
    factory {
        RefreshLoginUseCase(
            authRepository = get()
        )
    }

    factory {
        ObserveAccessTokenUseCase(
            authRepository = get()
        )
    }

    factory {
        ObserveHasLocalSessionUseCase(
            authRepository = get()
        )
    }
    factory {
        GetAccountsUseCase(
            repository = get()
        )
    }

}