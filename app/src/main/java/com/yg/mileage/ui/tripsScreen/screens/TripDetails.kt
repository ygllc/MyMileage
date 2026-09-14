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

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage.ui.tripsScreen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yg.mileage.CarViewModel
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.CalculateButton
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.CurrencyDropdownField
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.FuelPriceTextField
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.FuelTextField
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.MileageTextField
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.SaveTripButton
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.TripResultCard
import com.yg.mileage.ui.tripsScreen.components.tripsDetailsScreen.VehicleDropdownField
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelType
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle
import kotlinx.coroutines.launch
import java.text.DecimalFormat

// --- Use your own message type enum --- //
enum class AppMessageType { ERROR, WARNING, SUCCESS, INFO }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MileageCalculatorScreen(
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel
) {
    var startMileageText by remember { mutableStateOf("") }
    var endMileageText by remember { mutableStateOf("") }
    var fuelFilledText by remember { mutableStateOf("") }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var vehicleDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }
    var manualFuelPriceText by remember { mutableStateOf("") }
    var isFuelPriceModified by remember { mutableStateOf(false) }
    var defaultFuelPrice by remember { mutableStateOf<Double?>(null) }
    var tripDistance by remember { mutableStateOf<Double?>(null) }
    var customCalculationResult by remember { mutableStateOf<Double?>(null) }
    var fuelCost by remember { mutableStateOf<Double?>(null) }
    var messageType by remember { mutableStateOf<AppMessageType?>(null) }
    var messageText by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }
    var isCalculating by remember { mutableStateOf(false) }

    val editingTrip by carViewModel.editingTrip.collectAsState()
    val editingTripGroupId by carViewModel.editingTripGroupId.collectAsState()
    val tripGroups by carViewModel.tripGroups.collectAsState()

    val defaultCurrency by carViewModel.defaultCurrency.collectAsState()
    val currencies by carViewModel.currencies.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var currentTripId by rememberSaveable(editingTrip?.id) { mutableStateOf(editingTrip?.id) }
    var isTripInProgress by rememberSaveable(editingTrip != null) { mutableStateOf(editingTrip != null) }

    val groupName = remember(editingTripGroupId, tripGroups) {
        editingTripGroupId?.let { id -> tripGroups.find { it.id == id }?.groupName }
    }

    fun showMessage(type: AppMessageType, text: String) {
        messageType = type
        messageText = text
        showMessage = true
    }

    val decimalFormat = remember { DecimalFormat("#,##0.0##") }
    val savedVehicles by carViewModel.savedVehicles.collectAsState()

    val fuelUnit = when (selectedVehicle?.fuelType) {
        FuelType.PETROL, FuelType.DIESEL -> "Ltr"
        FuelType.CNG -> "KG"
        null -> "Ltr"
    }

    LaunchedEffect(editingTrip) {
        editingTrip?.let { trip ->
            selectedVehicle = savedVehicles.find { it.name == trip.vehicleName }
            startMileageText = trip.startMileage?.toString() ?: ""
            endMileageText = trip.endMileage?.toString() ?: ""
            fuelFilledText = trip.fuelFilled?.toString() ?: ""
            tripDistance = trip.tripDistance
            customCalculationResult = trip.fuelEfficiency
            fuelCost = trip.fuelCost
            // Set currency from trip or default
            selectedCurrency = currencies.find { it.id == trip.currencyId } ?: defaultCurrency
            // If trip has a fuel price, use it; otherwise populate from active fuel price
            if (trip.fuelPricePerUnit != null) {
                manualFuelPriceText = trip.fuelPricePerUnit.toString()
                isFuelPriceModified = true
            } else {
                isFuelPriceModified = false
                selectedVehicle?.fuelType?.let { fuelType ->
                    val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType)
                    latestFuelPrice?.let {
                        manualFuelPriceText = it.pricePerUnit.toString()
                        defaultFuelPrice = it.pricePerUnit
                    } ?: run {
                        manualFuelPriceText = ""
                        defaultFuelPrice = null
                    }
                }
            }
        } ?: run {
            startMileageText = ""
            endMileageText = ""
            fuelFilledText = ""
            tripDistance = null
            customCalculationResult = null
            fuelCost = null
            selectedCurrency = defaultCurrency
            isFuelPriceModified = false
            manualFuelPriceText = ""
            defaultFuelPrice = null
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (groupName != null) {
            item {
                Text(
                    text = "Group: $groupName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        if (isCalculating || showMessage || (tripDistance != null && customCalculationResult != null)) {
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item {
                TripResultCard(
                    isCalculating = isCalculating,
                    showMessage = showMessage,
                    messageText = messageText,
                    // Use a safe call or a default if messageType is null
                    messageType = messageType ?: AppMessageType.INFO,
                    tripDistance = tripDistance,
                    fuelEfficiency = customCalculationResult,
                    fuelCost = fuelCost,
                    fuelUnit = fuelUnit,
                    currencySymbol = selectedCurrency?.symbol ?: defaultCurrency?.symbol ?: "",
                    decimalFormat = decimalFormat,
                    onClose = {
                        showMessage = false // Ensure showMessage is also toggled
                        isCalculating = false
                        tripDistance = null
                        customCalculationResult = null
                        fuelCost = null
                    }
                )
            }
        }
        item {
            VehicleDropdownField(
                selectedVehicle = selectedVehicle,
                savedVehicles = savedVehicles,
                expanded = vehicleDropdownExpanded,
                onExpandedChange = { expanded -> vehicleDropdownExpanded = expanded },
                onVehicleSelected = { vehicle ->
                    selectedVehicle = vehicle
                    vehicleDropdownExpanded = false
                    tripDistance = null
                    customCalculationResult = null
                    fuelCost = null
                    showMessage = false
                    if (editingTrip != null) {
                        carViewModel.setEditingTrip(null)
                    }
                    startMileageText = ""
                    endMileageText = ""
                    fuelFilledText = ""
                    selectedCurrency = defaultCurrency
                    isFuelPriceModified = false
                    vehicle.fuelType?.let { fuelType ->
                        coroutineScope.launch {
                            val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType)
                            latestFuelPrice?.let {
                                manualFuelPriceText = it.pricePerUnit.toString()
                                defaultFuelPrice = it.pricePerUnit
                            } ?: run {
                                manualFuelPriceText = ""
                                defaultFuelPrice = null
                            }
                        }
                    } ?: run {
                        manualFuelPriceText = ""
                        defaultFuelPrice = null
                    }
                }
            )
        }
        // Currency selection
        item {
            CurrencyDropdownField(
                selectedCurrency = selectedCurrency,
                currencies = currencies,
                expanded = currencyDropdownExpanded,
                onExpandedChange = { expanded -> currencyDropdownExpanded = expanded },
                onCurrencySelected = { currency ->
                    selectedCurrency = currency
                    currencyDropdownExpanded = false
                }
            )
        }
        if (selectedVehicle != null) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                MileageTextField(
                    value = startMileageText,
                    onValueChange = {
                        startMileageText = it
                        tripDistance = null
                        customCalculationResult = null
                        fuelCost = null
                        showMessage = false
                    },
                    label = "Trip Start"
                )
            }
            item {
                MileageTextField(
                    value = endMileageText,
                    onValueChange = {
                        endMileageText = it
                        tripDistance = null
                        customCalculationResult = null
                        fuelCost = null
                        showMessage = false
                    },
                    label = "Trip End"
                )
            }
//        item {
//            AddSubTripButton(modifier)
//        }
            item {
                FuelTextField(
                    value = fuelFilledText,
                    onValueChange = { itInput ->
                        fuelFilledText = itInput
                        tripDistance = null
                        customCalculationResult = null
                        fuelCost = null
                        showMessage = false
                    },
                    fuelType = selectedVehicle?.fuelType
                )
            }

            // Manual fuel price per unit
            item {
                FuelPriceTextField(
                    value = manualFuelPriceText,
                    onValueChange = { input ->
                        manualFuelPriceText = input
                        isFuelPriceModified = true
                        fuelCost = null
                        showMessage = false
                    },
                    fuelType = selectedVehicle?.fuelType
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculateButton(
                        startMileageText = startMileageText,
                        endMileageText = endMileageText,
                        fuelFilledText = fuelFilledText,
                        selectedVehicle = selectedVehicle,
                        manualFuelPriceText = manualFuelPriceText,
                        carViewModel = carViewModel,
                        coroutineScope = coroutineScope,
                        onShowMessage = ::showMessage,
                        onCalculationResult = { distance, efficiency, calculatedFuelCost ->
                            tripDistance = distance
                            customCalculationResult = efficiency
                            fuelCost = calculatedFuelCost
                        },
                        onCalculatingChange = { isCalculating = it },
                        enabled = true,
                        isCalculating = isCalculating,
                        modifier = Modifier.weight(1f)
                    )

                    SaveTripButton(
                        startMileageText = startMileageText,
                        endMileageText = endMileageText,
                        fuelFilledText = fuelFilledText,
                        selectedVehicle = selectedVehicle,
                        selectedCurrency = selectedCurrency,
                        defaultCurrency = defaultCurrency,
                        manualFuelPriceText = manualFuelPriceText,
                        isFuelPriceModified = isFuelPriceModified,
                        defaultFuelPrice = defaultFuelPrice,
                        editingTripGroupId = editingTripGroupId,
                        currentTripId = currentTripId,
                        isTripInProgress = isTripInProgress,
                        tripDistance = tripDistance,
                        customCalculationResult = customCalculationResult,
                        fuelCost = fuelCost,
                        carViewModel = carViewModel,
                        coroutineScope = coroutineScope,
                        onShowMessage = ::showMessage,
                        onTripDistanceChange = { tripDistance = it },
                        onCalculationResultChange = { customCalculationResult = it },
                        onFuelCostChange = { fuelCost = it },
                        onTripProgressChange = { isTripInProgress = it },
                        onCurrentTripIdChange = { currentTripId = it },
                        enabled = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
