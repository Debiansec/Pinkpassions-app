package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.ui.viewmodel.MainViewModel

@Composable
fun BusinessDirectoryScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    MainDirectoryScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}
