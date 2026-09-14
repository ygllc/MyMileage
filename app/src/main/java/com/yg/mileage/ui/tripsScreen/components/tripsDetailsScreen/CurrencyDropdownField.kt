/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdownField(
    selectedCurrency: Currency?,
    currencies: List<Currency>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCurrencySelected: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCurrency?.let { "${it.symbol} ${it.name} (${it.code})" } ?: "Select Currency",
            onValueChange = {},
            readOnly = true,
            label = { Text("Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            if (currencies.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No currencies available") },
                    onClick = { onExpandedChange(false) }
                )
            } else {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text("${currency.symbol} ${currency.name} (${currency.code})") },
                        onClick = {
                            onCurrencySelected(currency)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}
