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

package com.yg.mileage.ui.vehiclesScreen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.vehiclesScreen.components.vehiclesScreen.VehicleCard
import com.yg.mileage.ui.vehiclesScreen.components.vehiclesScreen.VehicleSettingsCard
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreen(
    savedVehicles: List<Vehicle>,
    onAddVehicleClick: () -> Unit,
    onEditVehicleClick: (String) -> Unit,
    onCurrencySettingsClick: () -> Unit,
    onAddVehicle: (Vehicle) -> Unit,
    onEditVehicle: (String, Vehicle) -> Unit,
    onDeleteVehicle: (String) -> Unit,
    canDeleteVehicle: (String) -> Boolean
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(100.dp),
                onClick = onAddVehicleClick,
                shape = MaterialShapes.Cookie9Sided.toShape(),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Vehicle",
                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Settings Section
            VehicleSettingsCard(onCurrencySettingsClick = onCurrencySettingsClick)

            // Grid of vehicles
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Vehicle Cards
                items(savedVehicles) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        canDelete = canDeleteVehicle(vehicle.id),
                        onEdit = { onEditVehicleClick(vehicle.name) },
                        onDelete = { onDeleteVehicle(vehicle.id) }
                    )
                }
            }
        }
    }
}
