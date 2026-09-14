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
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yg.mileage.CarViewModel
import com.yg.mileage.ui.tripsScreen.screens.AppMessageType
import com.yg.mileage.ui.tripsScreen.viewModels.Trip
import com.yg.mileage.ui.tripsScreen.viewModels.TripStatus
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SaveTripButton(
    modifier: Modifier = Modifier,
    startMileageText: String,
    endMileageText: String,
    fuelFilledText: String,
    selectedVehicle: Vehicle?,
    selectedCurrency: Currency?,
    defaultCurrency: Currency?,
    manualFuelPriceText: String,
    isFuelPriceModified: Boolean,
    defaultFuelPrice: Double?,
    editingTripGroupId: String?,
    currentTripId: String?,
    isTripInProgress: Boolean,
    tripDistance: Double?,
    customCalculationResult: Double?,
    fuelCost: Double?,
    carViewModel: CarViewModel,
    coroutineScope: CoroutineScope,
    onShowMessage: (AppMessageType, String) -> Unit,
    onTripDistanceChange: (Double?) -> Unit,
    onCalculationResultChange: (Double?) -> Unit,
    onFuelCostChange: (Double?) -> Unit,
    onTripProgressChange: (Boolean) -> Unit,
    onCurrentTripIdChange: (String?) -> Unit,
    enabled: Boolean = true,
) {
    Button(
        onClick = {
            val start = startMileageText.toDoubleOrNull()
            val end = endMileageText.toDoubleOrNull()
            val fuel = fuelFilledText.toDoubleOrNull()
            val vehicle = selectedVehicle

            if (vehicle == null) {
                onShowMessage(AppMessageType.ERROR, "Please select a vehicle profile.")
                return@Button
            }
            if (start != null && end != null && end < start) {
                onShowMessage(AppMessageType.ERROR, "End mileage cannot be less than start mileage.")
                return@Button
            }

            val isComplete = start != null && end != null && fuel != null && fuel > 0

            if (isComplete && (tripDistance == null || customCalculationResult == null)) {
                val distance = end - start
                val efficiency = distance / fuel
                onTripDistanceChange(distance)
                onCalculationResultChange(efficiency)

                val fuelType = vehicle.fuelType
                val manualPrice = manualFuelPriceText.toDoubleOrNull()
                coroutineScope.launch {
                    if (manualPrice != null && manualPrice > 0) {
                        onFuelCostChange(fuel * manualPrice)
                    } else {
                        val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType!!)
                        if (latestFuelPrice != null) {
                            onFuelCostChange(fuel * latestFuelPrice.pricePerUnit)
                        }
                    }
                }
            }

            coroutineScope.launch {
                var tripFuelPricePerUnit: Double? = null
                var tripCurrencyId: String? = null

                val fuelType = vehicle.fuelType
                val manualPrice = manualFuelPriceText.toDoubleOrNull()

                when {
                    isFuelPriceModified && manualPrice != null && manualPrice > 0 -> {
                        tripFuelPricePerUnit = manualPrice
                        tripCurrencyId = selectedCurrency?.id ?: defaultCurrency?.id
                    }
                    !isFuelPriceModified && defaultFuelPrice != null -> {
                        tripFuelPricePerUnit = defaultFuelPrice
                        tripCurrencyId = selectedCurrency?.id ?: defaultCurrency?.id
                    }
                    manualPrice != null && manualPrice > 0 -> {
                        tripFuelPricePerUnit = manualPrice
                        tripCurrencyId = selectedCurrency?.id ?: defaultCurrency?.id
                    }
                    else -> {
                        val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType!!)
                        tripFuelPricePerUnit = latestFuelPrice?.pricePerUnit
                        tripCurrencyId = selectedCurrency?.id ?: latestFuelPrice?.currencyId
                    }
                }

                val trip = Trip(
                    id = currentTripId ?: UUID.randomUUID().toString(),
                    vehicleId = vehicle.id,
                    vehicleName = vehicle.name,
                    startMileage = start,
                    endMileage = end,
                    fuelFilled = fuel,
                    tripDistance = if (isComplete) tripDistance else null,
                    fuelEfficiency = if (isComplete) customCalculationResult else null,
                    fuelCost = if (isComplete) fuelCost else null,
                    fuelPricePerUnit = tripFuelPricePerUnit,
                    currencyId = tripCurrencyId,
                    tripGroup = editingTripGroupId,
                    status = if (isComplete) TripStatus.COMPLETED else TripStatus.DRAFT,
                )

                if (currentTripId != null || isTripInProgress) {
                    carViewModel.updateTrip(trip)
                } else {
                    carViewModel.addTrip(trip)
                }
                onTripProgressChange(true)
                onCurrentTripIdChange(trip.id)
                onShowMessage(
                    if (isComplete) AppMessageType.SUCCESS else AppMessageType.WARNING,
                    if (isComplete) "Trip saved successfully!" else "Trip Saved as Draft"
                )
            }
        },
        enabled = enabled,
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = modifier
    ) {
        Icon(Icons.Rounded.Save, contentDescription = "Save Trip")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save Trip")
    }
}
