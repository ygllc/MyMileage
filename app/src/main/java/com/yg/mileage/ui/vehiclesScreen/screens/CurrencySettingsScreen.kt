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

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage.ui.vehiclesScreen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yg.mileage.CarViewModel
import com.yg.mileage.ui.vehiclesScreen.components.currencySettingsScreen.CurrencySettingsScreenContent
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelPrice
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelType
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

@Composable
fun CurrencySettingsScreen(
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = viewModel()
) {
    val currencies by carViewModel.currencies.collectAsState()
    val fuelPrices by carViewModel.fuelPrices.collectAsState()
    val defaultCurrency by carViewModel.defaultCurrency.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    CurrencySettingsScreenContent(
        modifier = modifier,
        currencies = currencies,
        fuelPrices = fuelPrices,
        defaultCurrency = defaultCurrency,
        onAddCurrency = { currency -> coroutineScope.launch { carViewModel.addCurrency(currency) } },
        onUpdateCurrency = { currency -> coroutineScope.launch { carViewModel.updateCurrency(currency) } },
        onDeleteCurrency = { currency -> coroutineScope.launch { carViewModel.deleteCurrency(currency) } },
        onSetDefaultCurrency = { currencyId -> coroutineScope.launch { carViewModel.setDefaultCurrency(currencyId) } },
        onAddFuelPrice = { fuelPrice -> coroutineScope.launch { carViewModel.addFuelPrice(fuelPrice) } },
        onUpdateFuelPrice = { fuelPrice -> coroutineScope.launch { carViewModel.updateFuelPrice(fuelPrice) } },
        onDeleteFuelPrice = { fuelPrice -> coroutineScope.launch { carViewModel.deleteFuelPrice(fuelPrice) } }
    )
}

@Preview(showBackground = true)
@Composable
fun CurrencySettingsScreenPreview() {
    val sampleCurrencies = listOf(
        Currency(id = "1", code = "USD", name = "US Dollar", symbol = "$", isDefault = true),
        Currency(id = "2", code = "EUR", name = "Euro", symbol = "€"),
        Currency(id = "3", code = "INR", name = "Indian Rupee", symbol = "₹")
    )
    val sampleFuelPrices = listOf(
        FuelPrice(
            id = "1",
            fuelType = FuelType.PETROL,
            pricePerUnit = 1.50,
            currencyId = "1",
            lastUpdated = Date(),
            isActive = true
        ),
        FuelPrice(
            id = "2",
            fuelType = FuelType.DIESEL,
            pricePerUnit = 1.30,
            currencyId = "1",
            lastUpdated = Date(),
            isActive = true
        ),
        FuelPrice(
            id = "3",
            fuelType = FuelType.CNG,
            pricePerUnit = 0.90,
            currencyId = "1",
            lastUpdated = Date(),
            isActive = true
        )
    )
    CurrencySettingsScreenContent(
        currencies = sampleCurrencies,
        fuelPrices = sampleFuelPrices,
        defaultCurrency = sampleCurrencies.first { it.isDefault },
        onAddCurrency = {},
        onUpdateCurrency = {},
        onDeleteCurrency = {},
        onSetDefaultCurrency = {},
        onAddFuelPrice = {},
        onUpdateFuelPrice = {},
        onDeleteFuelPrice = {}
    )
}

@Composable
fun CurrencyDialog(
    currency: Currency?,
    onDismiss: () -> Unit,
    onSave: (Currency) -> Unit
) {
    val availableCurrencies = remember {
        java.util.Currency.getAvailableCurrencies()
            .sortedBy { it.displayName }
            .map { it.currencyCode to "${it.currencyCode} - ${it.displayName}" }
    }
    var selectedCurrencyCode by remember { mutableStateOf(currency?.code ?: "") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (currency != null) "Edit Currency" else "Add Currency") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = availableCurrencies.find { it.first == selectedCurrencyCode }?.second ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        availableCurrencies.forEach { (code, displayName) ->
                            DropdownMenuItem(
                                text = { Text(displayName) },
                                onClick = {
                                    selectedCurrencyCode = code
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedCurrencyCode.isNotBlank()) {
                        val javaCurrency = availableCurrencies.find { it.first == selectedCurrencyCode }?.let {
                            java.util.Currency.getInstance(selectedCurrencyCode)
                        }
                        if (javaCurrency != null) {
                            val symbol = javaCurrency.symbol
                            val name = javaCurrency.displayName
                            onSave(
                                Currency(
                                    id = currency?.id ?: UUID.randomUUID().toString(),
                                    code = javaCurrency.currencyCode,
                                    name = name,
                                    symbol = symbol,
                                    isDefault = currency?.isDefault ?: false
                                )
                            )
                        }
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun AddCurrencyDialogPreview() {
    CurrencyDialog(
        currency = null,
        onDismiss = {},
        onSave = {}
    )
}

@Preview
@Composable
fun EditCurrencyDialogPreview() {
    val sampleCurrency = Currency(code = "USD", name = "US Dollar", symbol = "$", isDefault = true)
    CurrencyDialog(
        currency = sampleCurrency,
        onDismiss = {},
        onSave = {}
    )
}

@Composable
fun FuelPriceDialog(
    fuelPrice: FuelPrice?,
    currencies: List<Currency>,
    onDismiss: () -> Unit,
    onSave: (FuelPrice) -> Unit
) {
    var selectedFuelType by remember { mutableStateOf(fuelPrice?.fuelType ?: FuelType.PETROL) }
    var priceText by remember { mutableStateOf(fuelPrice?.pricePerUnit?.toString() ?: "") }
    var selectedCurrencyId by remember { mutableStateOf(fuelPrice?.currencyId ?: currencies.firstOrNull { it.isDefault }?.id ?: currencies.firstOrNull()?.id ?: "") }
    var isActive by remember { mutableStateOf(fuelPrice?.isActive ?: true) } // State for the checkbox
    var isFuelTypeMenuExpanded by remember { mutableStateOf(false) }
    var isCurrencyMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (fuelPrice != null) "Edit Fuel Price" else "Add Fuel Price") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Fuel Type Dropdown (Existing code)
                ExposedDropdownMenuBox(
                    expanded = isFuelTypeMenuExpanded,
                    onExpandedChange = { isFuelTypeMenuExpanded = !isFuelTypeMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFuelType.displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Fuel Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFuelTypeMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isFuelTypeMenuExpanded,
                        onDismissRequest = { isFuelTypeMenuExpanded = false }
                    ) {
                        FuelType.entries.forEach { fuelType ->
                            DropdownMenuItem(
                                text = { Text(fuelType.displayName) },
                                onClick = {
                                    selectedFuelType = fuelType
                                    isFuelTypeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Currency Dropdown (Existing code)
                ExposedDropdownMenuBox(
                    expanded = isCurrencyMenuExpanded,
                    onExpandedChange = { isCurrencyMenuExpanded = !isCurrencyMenuExpanded }
                ) {
                    val selectedCurrency = currencies.find { it.id == selectedCurrencyId }
                    OutlinedTextField(
                        value = selectedCurrency?.let { "${it.symbol} ${it.name} (${it.code})" } ?: "Select Currency",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCurrencyMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isCurrencyMenuExpanded,
                        onDismissRequest = { isCurrencyMenuExpanded = false }
                    ) {
                        currencies.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text("${currency.symbol} ${currency.name} (${currency.code})") },
                                onClick = {
                                    selectedCurrencyId = currency.id
                                    isCurrencyMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            priceText = it
                        }
                    },
                    label = { Text("Price per ${ if (selectedFuelType == FuelType.CNG) "KG" else "Ltr" }") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Added "Active" Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Text(
                        text = "Active Price",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    if (price != null && price > 0 && selectedCurrencyId.isNotBlank()) {
                        onSave(
                            FuelPrice(
                                id = fuelPrice?.id ?: UUID.randomUUID().toString(),
                                fuelType = selectedFuelType,
                                pricePerUnit = price,
                                currencyId = selectedCurrencyId,
                                lastUpdated = Date(),
                                isActive = isActive // Pass the checkbox state to the model
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun AddFuelPriceDialogPreview() {
    val sampleCurrencies = listOf(
        Currency(id = "1", code = "USD", name = "US Dollar", symbol = "$"),
        Currency(id = "2", code = "EUR", name = "Euro", symbol = "€")
    )
    FuelPriceDialog(
        fuelPrice = null,
        currencies = sampleCurrencies,
        onDismiss = {},
        onSave = {}
    )
}

@Preview
@Composable
fun EditFuelPriceDialogPreview() {
    val sampleCurrencies = listOf(
        Currency(id = "1", code = "USD", name = "US Dollar", symbol = "$", isDefault = true),
        Currency(id = "2", code = "EUR", name = "Euro", symbol = "€")
    )
    val sampleFuelPrice = FuelPrice(
        fuelType = FuelType.PETROL,
        pricePerUnit = 1.50,
        currencyId = "1",
        lastUpdated = Date()
    )
    FuelPriceDialog(
        fuelPrice = sampleFuelPrice,
        currencies = sampleCurrencies,
        onDismiss = {},
        onSave = {}
    )
}
