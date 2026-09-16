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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelType
import com.yg.mileage.ui.vehiclesScreen.viewModel.VehicleIcons
import com.yg.mileage.ui.vehiclesScreen.viewModel.getVehicleIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleInfoTextFields(
    vehicleName: TextFieldValue,
    onVehicleNameChange: (TextFieldValue) -> Unit,
    make: TextFieldValue,
    onMakeChange: (TextFieldValue) -> Unit,
    model: TextFieldValue,
    onModelChange: (TextFieldValue) -> Unit,
    year: TextFieldValue,
    onYearChange: (TextFieldValue) -> Unit,
    fuelType: FuelType?,
    onFuelTypeChange: (FuelType?) -> Unit,
    registrationNumber: TextFieldValue,
    onRegistrationNumberChange: (TextFieldValue) -> Unit,
    selectedIconName: String,
    onSelectedIconNameChange: (String) -> Unit,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact
) {
    val isWideLayout = windowWidthSizeClass != WindowWidthSizeClass.Compact
    val fieldSpacerHeight = if (isWideLayout) 12.dp else 16.dp
    val sectionSpacerHeight = if (isWideLayout) 24.dp else 32.dp

    // Vehicle Name TextField (Required)
    OutlinedTextField(
        value = vehicleName,
        onValueChange = onVehicleNameChange,
        label = { Text("Vehicle Name *") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(fieldSpacerHeight))

    // Vehicle Icon Dropdown
    var iconDropdownExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = iconDropdownExpanded,
        onExpandedChange = { iconDropdownExpanded = !iconDropdownExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedIconName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Vehicle Icon") },
            leadingIcon = {
                Icon(
                    imageVector = getVehicleIcon(selectedIconName),
                    contentDescription = null
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = iconDropdownExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
        )
        ExposedDropdownMenu(
            expanded = iconDropdownExpanded,
            onDismissRequest = { iconDropdownExpanded = false }
        ) {
            VehicleIcons.forEach { (name, icon) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(name)
                        }
                    },
                    onClick = {
                        onSelectedIconNameChange(name)
                        iconDropdownExpanded = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(fieldSpacerHeight))

    // Make TextField (Optional)
    OutlinedTextField(
        value = make,
        onValueChange = onMakeChange,
        label = { Text("Make") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(fieldSpacerHeight))

    // Model TextField (Optional)
    OutlinedTextField(
        value = model,
        onValueChange = onModelChange,
        label = { Text("Model") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(fieldSpacerHeight))

    // Year TextField (Optional)
    OutlinedTextField(
        value = year,
        onValueChange = onYearChange,
        label = { Text("Year") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(fieldSpacerHeight))

    // Fuel Type Dropdown (Required)
    var fuelTypeExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = fuelTypeExpanded,
        onExpandedChange = { fuelTypeExpanded = !fuelTypeExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = fuelType?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Fuel Type *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelTypeExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                ),
        )
        ExposedDropdownMenu(
            expanded = fuelTypeExpanded,
            onDismissRequest = { fuelTypeExpanded = false }
        ) {
            FuelType.entries.forEach { fuel ->
                DropdownMenuItem(
                    text = { Text(fuel.displayName) },
                    onClick = {
                        onFuelTypeChange(fuel)
                        fuelTypeExpanded = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(fieldSpacerHeight))

    // Registration Number TextField (Optional)
    OutlinedTextField(
        value = registrationNumber,
        onValueChange = onRegistrationNumberChange,
        label = { Text("Registration Number") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(sectionSpacerHeight))
}
