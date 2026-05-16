package dev.mjamali.kmpfinbank.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.mjamali.kmpfinbank.presentation.accounts.AccountDetailsScreen
import dev.mjamali.kmpfinbank.presentation.accounts.AccountsScreen
import dev.mjamali.kmpfinbank.presentation.cards.CardsScreen
import dev.mjamali.kmpfinbank.presentation.dashboard.DashboardScreen
import dev.mjamali.kmpfinbank.presentation.login.LoginScreen
import dev.mjamali.kmpfinbank.presentation.profile.ProfileScreen
import dev.mjamali.kmpfinbank.presentation.transactions.TransactionsScreen
import dev.mjamali.kmpfinbank.presentation.transfer.PaymentConfirmationScreen
import dev.mjamali.kmpfinbank.presentation.transfer.ReceiptScreen
import dev.mjamali.kmpfinbank.presentation.transfer.TransferScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Login
    ) {
        composable<Route.Login> {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Dashboard) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Dashboard> {
            DashboardScreen(
                onAccountsClick = { navController.navigate(Route.Accounts) },
                onCardsClick = { navController.navigate(Route.Cards) },
                onTransactionsClick = { navController.navigate(Route.Transactions) },
                onTransferClick = { navController.navigate(Route.Transfer) },
                onProfileClick = { navController.navigate(Route.Profile) },
                onSessionExpired = {
                    navController.navigate(Route.Login) {
                        popUpTo<Route.Dashboard> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Route.Accounts> {
            AccountsScreen(
                onBack = { navController.popBackStack() },
                onAccountClick = { accountId ->
                    navController.navigate(Route.AccountDetails(accountId))
                }
            )
        }

        composable<Route.AccountDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.AccountDetails>()

            AccountDetailsScreen(
                route = route,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Cards> {
            CardsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Transactions> {
            TransactionsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Transfer> {
            TransferScreen(
                onBack = { navController.popBackStack() },
                onConfirm = { fromAccountId, toAccountNumber, amountMinor, note ->
                    navController.navigate(
                        Route.PaymentConfirmation(
                            fromAccountId = fromAccountId,
                            toAccountNumber = toAccountNumber,
                            amountMinor = amountMinor,
                            note = note
                        )
                    )
                }
            )
        }

        composable<Route.PaymentConfirmation> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.PaymentConfirmation>()

            PaymentConfirmationScreen(
                route = route,
                onBack = { navController.popBackStack() },
                onPaymentSuccess = { receiptId ->
                    navController.navigate(Route.Receipt(receiptId)) {
                        popUpTo<Route.Transfer> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Route.Receipt> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Receipt>()

            ReceiptScreen(
                route = route,
                onDone = {
                    navController.navigate(Route.Dashboard) {
                        popUpTo<Route.Dashboard> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Route.Profile> {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Route.Login) {
                        popUpTo<Route.Dashboard> {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}