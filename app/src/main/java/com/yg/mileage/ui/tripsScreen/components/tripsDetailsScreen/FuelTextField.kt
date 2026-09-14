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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelType

@Composable
fun FuelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    fuelType: FuelType?,
    modifier: Modifier = Modifier
) {
    val fuelLabel = when (fuelType) {
        FuelType.PETROL -> "Petrol Filled (Ltr)"
        FuelType.DIESEL -> "Diesel Filled (Ltr)"
        FuelType.CNG -> "CNG Filled (KG)"
        null -> "Fuel Filled"
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                onValueChange(input)
            }
        },
        label = { Text(fuelLabel) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Enter fuel filled (e.g. 5.25)") }
    )
}
