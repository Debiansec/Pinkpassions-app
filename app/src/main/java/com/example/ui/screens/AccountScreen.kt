package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.viewmodel.MainViewModel

/**
 * Account Screen delegates directly to the comprehensive ProfileScreen.
 */
@Composable
fun AccountScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    ProfileScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
