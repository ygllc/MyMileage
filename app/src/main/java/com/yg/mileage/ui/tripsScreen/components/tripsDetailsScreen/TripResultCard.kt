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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yg.mileage.ui.tripsScreen.screens.AppMessageType.ERROR
import com.yg.mileage.ui.tripsScreen.screens.AppMessageType.INFO
import com.yg.mileage.ui.tripsScreen.screens.AppMessageType.SUCCESS
import com.yg.mileage.ui.tripsScreen.screens.AppMessageType.WARNING
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripResultCard(
    isCalculating: Boolean,
    showMessage: Boolean,
    messageText: String,
    messageType: com.yg.mileage.ui.tripsScreen.screens.AppMessageType,
    tripDistance: Double?,
    fuelEfficiency: Double?,
    fuelCost: Double?,
    fuelUnit: String,
    currencySymbol: String,
    onClose: () -> Unit,
    decimalFormat: DecimalFormat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = when (messageType) {
            ERROR -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = Color.White
            )
            WARNING -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = Color.White
            )
            SUCCESS -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color.White
            )
            INFO, null -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = Color.White
            )
        }
    ) {
        // Header with optional status and close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isCalculating) "Calculating..." else messageText.ifBlank { "Trip saved successfully!" },
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close Result")
            }
        }
        HorizontalDivider(thickness = DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.2f))

        if (isCalculating) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ContainedLoadingIndicator(modifier = Modifier.size(28.dp))
            }
        } else if (tripDistance != null && fuelEfficiency != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Trip Distance: ${decimalFormat.format(tripDistance)} km",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Text(
                    text = "Fuel Efficiency: ${decimalFormat.format(fuelEfficiency)} km/$fuelUnit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                if (fuelCost != null) {
                    Text(
                        text = "Fuel Cost: $currencySymbol${decimalFormat.format(fuelCost)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Need to define AppMessageType enum if not already available
enum class AppMessageType { ERROR, WARNING, SUCCESS, INFO }
