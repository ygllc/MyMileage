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

package com.yg.mileage.ui.vehiclesScreen.components.addVehicleScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelType
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle

@Composable
fun VehicleInfoButtons(
    onBack: () -> Unit,
    onSaveVehicle: (Vehicle) -> Unit,
    vehicleToEdit: Vehicle? = null,
    vehicleName: TextFieldValue,
    make: TextFieldValue,
    model: TextFieldValue,
    year: TextFieldValue,
    fuelType: FuelType?,
    registrationNumber: TextFieldValue,
    selectedIconName: String,
    onValidationError: (String) -> Unit = {}
) {
    var isError by remember { mutableStateOf(false) }
    var fuelTypeError by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                val name = vehicleName.text.trim()
                when {
                    name.isEmpty() -> {
                        isError = true
                        onValidationError("Vehicle name cannot be empty")
                    }
                    name.length < 2 -> {
                        isError = true
                        onValidationError("Vehicle name must be at least 2 characters")
                    }
                    fuelType == null -> {
                        fuelTypeError = true
                    }
                    else -> {
                        val newVehicle = vehicleToEdit?.copy(
                            name = name,
                            make = make.text.trim(),
                            model = model.text.trim(),
                            year = year.text.trim(),
                            fuelType = fuelType,
                            registrationNumber = registrationNumber.text.trim(),
                            iconName = selectedIconName
                        ) ?: Vehicle(
                            name = name,
                            make = make.text.trim(),
                            model = model.text.trim(),
                            year = year.text.trim(),
                            fuelType = fuelType,
                            registrationNumber = registrationNumber.text.trim(),
                            iconName = selectedIconName
                        )
                        onSaveVehicle(newVehicle)
                        onBack()
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(if (vehicleToEdit != null) "Update" else "Save")
        }
    }
}
