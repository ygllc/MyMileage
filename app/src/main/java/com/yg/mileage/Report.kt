package com.yg.mileage

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun Report(
    carViewModel: CarViewModel = viewModel()
) {
    Text(
        text = "Report Screen",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold

    )
}