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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yg.mileage.CarViewModel
import com.yg.mileage.data.TripGroupEntity
import com.yg.mileage.ui.tripsScreen.components.tripsScreen.EditGroupDialog
import com.yg.mileage.ui.tripsScreen.components.tripsScreen.NewGroupDialog
import com.yg.mileage.ui.tripsScreen.components.tripsScreen.TripsScreenContent
import com.yg.mileage.ui.tripsScreen.viewModels.Trip
import com.yg.mileage.ui.tripsScreen.viewModels.TripStatus
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import com.yg.mileage.ui.tripsScreen.viewModels.HistoryItem
import com.yg.mileage.ui.tripsScreen.viewModels.SortableHistoryItem
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    carViewModel: CarViewModel,
    onNavigateToTripDetails: () -> Unit
) {
    val defaultCurrency by carViewModel.defaultCurrency.collectAsState()
    val historyItems by carViewModel.historyItems.collectAsState()
    val tripsFilterIndex by carViewModel.tripsFilterIndex.collectAsState()
    
    val coroutineScope = rememberCoroutineScope()
    val isTwoPane = windowWidthSizeClass != WindowWidthSizeClass.Compact
    var selectedTripForPane by rememberSaveable { mutableStateOf<Trip?>(null) }
    var showNewTripPane by rememberSaveable { mutableStateOf(false) }

    var showNewGroupDialog by remember { mutableStateOf(false) }
    var groupToEdit by remember { mutableStateOf<TripGroupEntity?>(null) }

    if (showNewGroupDialog) {
        NewGroupDialog(
            onDismiss = { showNewGroupDialog = false },
            onConfirm = { groupName ->
                coroutineScope.launch {
                    carViewModel.addTripGroup(groupName)
                }
                showNewGroupDialog = false
            }
        )
    }

    if (groupToEdit != null) {
        EditGroupDialog(
            group = groupToEdit!!,
            onDismiss = { groupToEdit = null },
            onConfirm = { groupName ->
                coroutineScope.launch {
                    carViewModel.updateTripGroup(
                        groupToEdit!!.copy(
                            groupName = groupName,
                            updatedAt = Date()
                        )
                    )
                }
                groupToEdit = null
            }
        )
    }

    TripsScreenContent(
        modifier = modifier,
        windowWidthSizeClass = windowWidthSizeClass,
        isTwoPane = isTwoPane,
        historyItems = historyItems,
        tripsFilterIndex = tripsFilterIndex,
        onFilterSelected = { carViewModel.setTripsFilterIndex(it) },
        defaultCurrency = defaultCurrency,
        selectedTripForPane = selectedTripForPane,
        showNewTripPane = showNewTripPane,
        onNavigateToTripDetails = onNavigateToTripDetails,
        onEditTrip = { trip ->
            if (isTwoPane) {
                carViewModel.setEditingTrip(trip)
                carViewModel.setEditingTripGroupId(trip.tripGroup)
                selectedTripForPane = trip
                showNewTripPane = false
            } else {
                carViewModel.setEditingTrip(trip)
                carViewModel.setEditingTripGroupId(trip.tripGroup)
                onNavigateToTripDetails()
            }
        },
        onDeleteTrip = { tripId ->
            coroutineScope.launch {
                carViewModel.deleteTrip(tripId)
            }
            if (selectedTripForPane?.id == tripId) {
                selectedTripForPane = null
                showNewTripPane = false
            }
        },
        onNewTrip = {
            if (isTwoPane) {
                carViewModel.setEditingTrip(null)
                carViewModel.setEditingTripGroupId(null)
                selectedTripForPane = null
                showNewTripPane = true // Show new trip form in supporting pane
            } else {
                carViewModel.setEditingTrip(null)
                carViewModel.setEditingTripGroupId(null)
                onNavigateToTripDetails()
            }
        },
        onNewGroupTrip = {
            showNewGroupDialog = true
        },
        onAddTripToGroup = { groupId ->
            if (isTwoPane) {
                carViewModel.setEditingTrip(null)
                carViewModel.setEditingTripGroupId(groupId)
                selectedTripForPane = null
                showNewTripPane = true // Show new trip form for this group in pane
            } else {
                carViewModel.setEditingTrip(null)
                carViewModel.setEditingTripGroupId(groupId)
                onNavigateToTripDetails()
            }
        },
        onEditGroup = { group ->
            groupToEdit = group
        },
        onDeleteGroup = { group ->
            coroutineScope.launch {
                carViewModel.deleteTripGroup(group)
            }
        },
        carViewModel = carViewModel
    )
}
@Composable
fun FilterBottomSheetContent(onApply: () -> Unit) {
    Spacer(modifier = Modifier.padding(25.dp))
    Text(text = "                              :)")
    Spacer(modifier = Modifier.padding(25.dp))
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    val sampleTrips = listOf(
        Trip(
            id = "1",
            vehicleId = "v1",
            vehicleName = "Toyota Fortuner",
            startMileage = 10000.0,
            endMileage = 10500.0,
            fuelFilled = 50.0,
            tripDistance = 500.0,
            fuelEfficiency = 10.0,
            fuelCost = 5000.0,
            fuelPricePerUnit = 100.0,
            currencyId = "INR",
            status = TripStatus.COMPLETED,
            createdAt = Date(),
            updatedAt = Date()
        ),
        Trip(
            id = "2",
            vehicleId = "v1",
            vehicleName = "Fortuner",
            startMileage = 10500.0,
            endMileage = null,
            fuelFilled = 40.0,
            tripDistance = null,
            fuelEfficiency = null,
            fuelCost = null,
            fuelPricePerUnit = 110.0,
            currencyId = "INR",
            status = TripStatus.DRAFT,
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    MaterialTheme {
        TripsScreenContent(
            historyItems = emptyList(),
            tripsFilterIndex = 0,
            onFilterSelected = {},
            defaultCurrency = Currency("inr", "INR", "Indian Rupee", "?", true),
            onNavigateToTripDetails = {},
            onEditTrip = {},
            onDeleteTrip = {},
            onNewTrip = {},
            onNewGroupTrip = {},
            onAddTripToGroup = {},
            onEditGroup = {},
            onDeleteGroup = {},
            carViewModel = viewModel(),
            selectedTripForPane = null,
            showNewTripPane = true
        )
    }
}
