package com.example.currencyconverter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo

@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel = hiltViewModel(),
    onExchangeSelected: (RateDto) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn {
        // Выбранная валюта (всегда наверху)
        item {
            CurrencyItem(
                rate = state.rates.find { it.currencyCode == state.selectedCurrency }
                    ?: RateDto(state.selectedCurrency, state.amount),
                balance = state.accounts.getBalance(state.selectedCurrency),
                isSelected = true,
                isInputMode = state.isInputMode,
                onAmountChange = viewModel::onAmountChanged,
                onToggleInputMode = { viewModel.toggleInputMode() }
            )
        }

        // Остальные валюты
        items(state.filteredRates.filter { it.currencyCode != state.selectedCurrency }) { rate ->
            CurrencyItem(
                rate = rate,
                balance = state.accounts.getBalance(rate.currencyCode),
                isSelected = false,
                isInputMode = false,
                onAmountChange = {},
                onClick = {
                    if (state.isInputMode) {
                        onExchangeSelected(rate)
                    } else {
                        viewModel.onCurrencySelected(rate.currencyCode)
                    }
                }
            )
        }
    }
}

@Composable
fun CurrencyItem(
    rate: RateDto,
    balance: Double,
    isSelected: Boolean,
    isInputMode: Boolean,
    onAmountChange: (Double) -> Unit,
    onToggleInputMode: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Флаг валюты (реализацию см. в доп. материалах)
        CurrencyFlag(rate.currencyCode)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = Currency.getInstance(rate.currencyCode).displayName,
                fontWeight = FontWeight.Bold
            )
            Text(text = rate.currencyCode)
        }

        // Поле ввода/отображения суммы
        if (isSelected && isInputMode) {
            var amountText by remember { mutableStateOf(rate.amount.toString()) }
            Box {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        it.toDoubleOrNull()?.let { amount -> onAmountChange(amount) }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.width(120.dp)
                )
                IconButton(
                    onClick = onToggleInputMode,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.Close, "Reset")
                }
            }
        } else {
            Text(
                text = "%.2f".format(rate.amount),
                modifier = if (isSelected) Modifier.clickable { onToggleInputMode() } else Modifier
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "%.2f".format(balance))
    }
}

// Расширение для получения баланса
fun List<AccountDbo>.getBalance(currency: String): Double {
    return find { it.code == currency }?.amount ?: 0.0
}