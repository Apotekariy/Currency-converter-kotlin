package com.example.currencyconverter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.dataSource.remote.RatesService
import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import com.example.currencyconverter.data.dataSource.room.account.dao.AccountDao
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo
import com.example.currencyconverter.ui.CurrencyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val ratesService: RatesService,
    private val accountDao: AccountDao
) : ViewModel() {


    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    private var updateJob: Job? = null

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                var accounts = accountDao.getAll()

                //Если счетов нет - создаем начальный рублёвый счёт
                if (accounts.isEmpty()) {
                    val initialAccount = AccountDbo(code = "RUB", amount = 75000.0)
                    accountDao.insertAll(initialAccount)
                    accounts = listOf(initialAccount)
                }

                _uiState.update { it.copy(accounts = accounts, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to load accounts",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getRates() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val baseCurrency = _uiState.value.selectedCurrency ?: return@launch

            try {
                val rates = ratesService.getRates(
                    baseCurrencyCode = baseCurrency.currency,
                    amount = 1.0
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        selectedCurrency = baseCurrency.copy(value = 1.0),
                        otherCurrencies = rates.filter { it.currency != baseCurrency.currency },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to fetch rates: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun selectCurrency(currency: RateDto) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCurrency = currency.copy(value = 1.0),
                otherCurrencies = (listOf(currentState.selectedCurrency) +
                        currentState.otherCurrencies)
                    .filterNotNull()
                    .filter { it.currency != currency.currency },
                isLoading = true
            )
        }
        getRates()
    }
}