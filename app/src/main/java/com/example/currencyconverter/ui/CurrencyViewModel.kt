package com.example.currencyconverter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.dataSource.remote.RatesService
import com.example.currencyconverter.data.dataSource.room.account.dao.AccountDao
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        viewModelScope.launch {
            val accounts = accountDao.getAll()
            if (accounts.isEmpty()) {
                accountDao.insertAll(AccountDbo(code = "RUB", amount = 75000.0))
            }
        }
    }

    private fun startPeriodicUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                val state = _uiState.value
                val rates = ratesService.getRates(
                    baseCurrencyCode = state.selected
                )
            }
        }
    }
}