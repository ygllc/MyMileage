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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.yg.mileage.ui.theme.adaptiveContent
import com.yg.mileage.ui.vehiclesScreen.components.addVehicleScreen.VehicleInfoButtons
import com.yg.mileage.ui.vehiclesScreen.components.addVehicleScreen.VehicleInfoTextFields
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddVehicleScreen(
    onSaveVehicle: (Vehicle) -> Unit,
    onBack: () -> Unit,
    vehicleToEdit: Vehicle? = null,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact
) {
    var vehicleName by remember { mutableStateOf(TextFieldValue(vehicleToEdit?.name ?: "")) }
    var make by remember { mutableStateOf(TextFieldValue(vehicleToEdit?.make ?: "")) }
    var model by remember { mutableStateOf(TextFieldValue(vehicleToEdit?.model ?: "")) }
    var year by remember { mutableStateOf(TextFieldValue(vehicleToEdit?.year ?: "")) }
    var fuelType by remember { mutableStateOf(vehicleToEdit?.fuelType) }
    var registrationNumber by remember { mutableStateOf(TextFieldValue(vehicleToEdit?.registrationNumber ?: "")) }
    var selectedIconName by remember { mutableStateOf(vehicleToEdit?.iconName ?: "DirectionsCar") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .adaptiveContent(windowWidthSizeClass)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VehicleInfoTextFields(
            vehicleName = vehicleName,
            onVehicleNameChange = { vehicleName = it },
            make = make,
            onMakeChange = { make = it },
            model = model,
            onModelChange = { model = it },
            year = year,
            onYearChange = { year = it },
            fuelType = fuelType,
            onFuelTypeChange = { fuelType = it },
            registrationNumber = registrationNumber,
            onRegistrationNumberChange = { registrationNumber = it },
            selectedIconName = selectedIconName,
            onSelectedIconNameChange = { selectedIconName = it },
            windowWidthSizeClass = windowWidthSizeClass
        )

        VehicleInfoButtons(
            onBack = onBack,
            onSaveVehicle = onSaveVehicle,
            vehicleToEdit = vehicleToEdit,
            vehicleName = vehicleName,
            make = make,
            model = model,
            year = year,
            fuelType = fuelType,
            registrationNumber = registrationNumber,
            selectedIconName = selectedIconName
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun AddVehicleScreenPreview() {
    AddVehicleScreen(
        onSaveVehicle = {},
        onBack = {}
    )
}
