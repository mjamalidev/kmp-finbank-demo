package dev.mjamali.kmpfinbank.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.usecase.LoadDashboardUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ObserveBalanceVisibilityUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ObserveSessionTimeoutUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ToggleBalanceVisibilityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val loadDashboardUseCase: LoadDashboardUseCase,
    private val observeBalanceVisibilityUseCase: ObserveBalanceVisibilityUseCase,
    private val toggleBalanceVisibilityUseCase: ToggleBalanceVisibilityUseCase,
    private val observeSessionTimeoutUseCase: ObserveSessionTimeoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeBalanceVisibility()
        observeSessionTimeout()
        loadDashboard()
    }

    fun onRetryClicked() {
        loadDashboard()
    }

    fun onToggleBalanceVisibilityClicked() {
        viewModelScope.launch {
            toggleBalanceVisibilityUseCase(
                currentValue = _uiState.value.isBalanceVisible
            )
        }
    }

    fun onSessionExpiredHandled() {
        _uiState.update {
            it.copy(isSessionExpired = false)
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            loadDashboardUseCase().collect { resource ->
                when (resource) {
                    Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        val data = resource.data

                        if (data != null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    accounts = data.accounts,
                                    recentTransactions = data.recentTransactions
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Empty dashboard response."
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                                    ?: "Failed to load dashboard."
                            )
                        }
                    }

                    is Resource.Exception -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Failed to load dashboard."
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeBalanceVisibility() {
        viewModelScope.launch {
            observeBalanceVisibilityUseCase().collect { visible ->
                _uiState.update {
                    it.copy(isBalanceVisible = visible)
                }
            }
        }
    }

    private fun observeSessionTimeout() {
        viewModelScope.launch {
            observeSessionTimeoutUseCase(
                timeoutMillis = 100_000L
            ).collect { expired ->
                if (expired) {
                    _uiState.update {
                        it.copy(isSessionExpired = true)
                    }
                }
            }
        }
    }
}