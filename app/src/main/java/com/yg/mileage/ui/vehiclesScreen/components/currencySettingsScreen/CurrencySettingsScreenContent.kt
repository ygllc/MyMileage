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

package com.yg.mileage.ui.vehiclesScreen.components.currencySettingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.theme.robotoFlexTopAppBar
import com.yg.mileage.ui.vehiclesScreen.screens.CurrencyDialog
import com.yg.mileage.ui.vehiclesScreen.screens.FuelPriceDialog
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelPrice
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelType
import java.text.DateFormat.getDateInstance
import java.text.DecimalFormat

@Composable
fun CurrencySettingsScreenContent(
    modifier: Modifier = Modifier,
    currencies: List<Currency>,
    fuelPrices: List<FuelPrice>,
    defaultCurrency: Currency?,
    onAddCurrency: (Currency) -> Unit,
    onUpdateCurrency: (Currency) -> Unit,
    onDeleteCurrency: (Currency) -> Unit,
    onSetDefaultCurrency: (String) -> Unit,
    onAddFuelPrice: (FuelPrice) -> Unit,
    onUpdateFuelPrice: (FuelPrice) -> Unit,
    onDeleteFuelPrice: (FuelPrice) -> Unit
) {
    var showAddCurrencyDialog by remember { mutableStateOf(false) }
    var showAddFuelPriceDialog by remember { mutableStateOf(false) }
    var editingCurrency by remember { mutableStateOf<Currency?>(null) }
    var editingFuelPrice by remember { mutableStateOf<FuelPrice?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Default Currency Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Default Currency",
                        fontFamily = robotoFlexTopAppBar,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (defaultCurrency != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${defaultCurrency.symbol} ${defaultCurrency.name} (${defaultCurrency.code})",
                                fontFamily = robotoFlexTopAppBar
                            )
                            IconButton(onClick = { editingCurrency = defaultCurrency }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Currency")
                            }
                        }
                    } else {
                        Text(
                            text = "No default currency set",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Currencies Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Currencies",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { showAddCurrencyDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Currency")
                }
            }
        }

        items(currencies) { currency ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currency.symbol} ${currency.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = currency.code,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (currency.isDefault) {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Row {
                        if (!currency.isDefault) {
                            IconButton(onClick = { onSetDefaultCurrency(currency.id) }) {
                                Text(
                                    text = "Set Default",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = { editingCurrency = currency }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDeleteCurrency(currency) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }

        // Fuel Prices Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fuel Prices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { showAddFuelPriceDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Fuel Price")
                }
            }
        }

        items(fuelPrices) { fuelPrice ->
            val currency = currencies.find { it.id == fuelPrice.currencyId }
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = fuelPrice.fuelType.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${currency?.symbol ?: ""}${DecimalFormat("#,##0.00").format(fuelPrice.pricePerUnit)} per ${if (fuelPrice.fuelType == FuelType.CNG) "KG" else "Ltr"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Updated: ${getDateInstance(1).format(fuelPrice.lastUpdated).format(fuelPrice.lastUpdated)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(onClick = { editingFuelPrice = fuelPrice }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDeleteFuelPrice(fuelPrice) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Currency Dialog
    if (showAddCurrencyDialog || editingCurrency != null) {
        CurrencyDialog(
            currency = editingCurrency,
            onDismiss = {
                showAddCurrencyDialog = false
                editingCurrency = null
            },
            onSave = { currency ->
                if (editingCurrency != null) {
                    onUpdateCurrency(currency)
                } else {
                    onAddCurrency(currency)
                }
                showAddCurrencyDialog = false
                editingCurrency = null
            }
        )
    }

    // Add/Edit Fuel Price Dialog
    if (showAddFuelPriceDialog || editingFuelPrice != null) {
        FuelPriceDialog(
            fuelPrice = editingFuelPrice,
            currencies = currencies,
            onDismiss = {
                showAddFuelPriceDialog = false
                editingFuelPrice = null
            },
            onSave = { fuelPrice ->
                if (editingFuelPrice != null) {
                    onUpdateFuelPrice(fuelPrice)
                } else {
                    onAddFuelPrice(fuelPrice)
                }
                showAddFuelPriceDialog = false
                editingFuelPrice = null
            }
        )
    }
}
