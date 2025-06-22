package com.example.currencyconverter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.currencyconverter.ui.CurrencyScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "currency_screen"
    ) {
        composable("currency_screen") {
            CurrencyScreen(onExchangeSelected = {})
        }
    }
}