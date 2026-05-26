package dev.mjamali.kmpfinbank.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mjamali.kmpfinbank.domain.usecase.LogoutUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ObserveBalanceVisibilityUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ToggleBalanceVisibilityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val observeBalanceVisibilityUseCase: ObserveBalanceVisibilityUseCase,
    private val toggleBalanceVisibilityUseCase: ToggleBalanceVisibilityUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeBalanceVisibility()
    }

    fun onToggleBalanceVisibilityClicked() {
        viewModelScope.launch {
            toggleBalanceVisibilityUseCase(
                currentValue = _uiState.value.isBalanceVisible
            )
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                logoutUseCase()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedOut = true
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Logout failed."
                    )
                }
            }
        }
    }

    fun onLogoutHandled() {
        _uiState.update {
            it.copy(isLoggedOut = false)
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
}