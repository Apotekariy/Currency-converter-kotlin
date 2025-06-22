package com.example.currencyconverter.ui

import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo

data class CurrencyUiState(
    val accounts: List<AccountDbo> = emptyList(),
    val selectedCurrency: RateDto? = null,
    val otherCurrencies: List<RateDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
