package com.yg.mileage

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(showSystemUi = true)
fun Report() {
    Text(
        text = "Statistics",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 100.dp, bottom = 100.dp, start = 100.dp, end = 100.dp)
    )
}