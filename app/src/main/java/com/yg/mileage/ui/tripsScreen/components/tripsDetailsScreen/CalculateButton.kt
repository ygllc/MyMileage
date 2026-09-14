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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yg.mileage.CarViewModel
import com.yg.mileage.ui.tripsScreen.screens.AppMessageType
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalculateButton(
    modifier: Modifier = Modifier,
    startMileageText: String,
    endMileageText: String,
    fuelFilledText: String,
    selectedVehicle: Vehicle?,
    manualFuelPriceText: String,
    carViewModel: CarViewModel,
    coroutineScope: CoroutineScope,
    onShowMessage: (AppMessageType, String) -> Unit,
    onCalculationResult: (distance: Double, efficiency: Double, fuelCost: Double?) -> Unit,
    onCalculatingChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    isCalculating: Boolean = false,
) {
    Button(
        onClick = {
            val start = startMileageText.toDoubleOrNull()
            val end = endMileageText.toDoubleOrNull()
            val fuel = fuelFilledText.toDoubleOrNull()

            if (selectedVehicle == null) {
                onShowMessage(AppMessageType.ERROR, "Please select a vehicle profile.")
                return@Button
            }
            if (start == null || end == null) {
                onShowMessage(AppMessageType.ERROR, "Start and End mileage must be valid numbers.")
                return@Button
            }
            if (start < 0 || end < 0) {
                onShowMessage(AppMessageType.ERROR, "Mileage cannot be negative.")
                return@Button
            }
            if (end <= start) {
                onShowMessage(AppMessageType.ERROR, "End mileage must be greater than start mileage.")
                return@Button
            }
            if (fuel == null || fuel <= 0) {
                onShowMessage(AppMessageType.ERROR, "Fuel filled must be a positive number.")
                return@Button
            }

            onCalculatingChange(true)
            coroutineScope.launch(Dispatchers.Default) {
                val distance = end - start
                val efficiency = distance / fuel

                var calculatedFuelCost: Double? = null
                val fuelType = selectedVehicle.fuelType
                val manualPrice = manualFuelPriceText.toDoubleOrNull()
                if (manualPrice != null && manualPrice > 0) {
                    calculatedFuelCost = fuel * manualPrice
                } else {
                    val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType!!)
                    if (latestFuelPrice != null) {
                        calculatedFuelCost = fuel * latestFuelPrice.pricePerUnit
                    }
                }

                delay(1000.milliseconds)
                onCalculationResult(distance, efficiency, calculatedFuelCost)
                onShowMessage(AppMessageType.SUCCESS, "Calculation complete!")
                onCalculatingChange(false)
            }
        },
        shapes = ButtonDefaults.shapes(IconButtonDefaults.smallRoundShape),
        enabled = enabled && !isCalculating,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.Rounded.Calculate, contentDescription = "Calculate Mileage")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Calculate")
    }
}
