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

package com.yg.mileage.ui.tripsScreen.components.tripsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yg.mileage.CarViewModel
import com.yg.mileage.ui.theme.MyMileageShapeDefaults
import com.yg.mileage.ui.tripsScreen.screens.MileageCalculatorScreen
import com.yg.mileage.ui.tripsScreen.viewModels.Trip

@Composable
fun TripDetailsPane(
    carViewModel: CarViewModel,
    selectedTrip: Trip?,
    showNewTripPane: Boolean = false
) {
    // Only show the pane when we are creating or editing a trip
    val hasContent = showNewTripPane || selectedTrip != null
    if (!hasContent) {
        // Empty supporting pane
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 420.dp)
                .padding(16.dp),
            shape = MyMileageShapeDefaults.cardShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            MileageCalculatorScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                carViewModel = carViewModel
            )
        }
    }
}