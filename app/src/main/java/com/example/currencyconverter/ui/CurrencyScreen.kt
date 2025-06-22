package com.example.currencyconverter.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import kotlinx.coroutines.delay

@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel = hiltViewModel(),
    onExchangeSelected: (RateDto) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Автообновление курсов каждую секунду
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.getRates()
            delay(1000)
        }
    }

    LazyColumn {
        items(state.accounts) {
            CurrencyItem(
                currencyCode = it.code,
                balance = it.amount,
                isSelected = state.selectedCurrency?.currency == it.code,
                rate = state.otherCurrencies.find { otherRate -> otherRate.currency == it.code }
                    ?: RateDto(currency = it.code, value = 0.0)
            )
        }
    }
}

@Composable
private fun CurrencyItem (
    currencyCode: String,
    balance: Double,
    rate: RateDto,
    isSelected: Boolean
) {
    Card(modifier = Modifier
        .fillMaxWidth()
    ) {
        Column {
            Text(text = currencyCode)
            Text(text = "%.2f".format(balance))
        }
    }
}