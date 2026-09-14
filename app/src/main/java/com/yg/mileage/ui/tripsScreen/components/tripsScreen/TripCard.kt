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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.theme.AppFonts.robotoMonoBold
import com.yg.mileage.ui.tripsScreen.viewModels.Trip
import com.yg.mileage.ui.tripsScreen.viewModels.TripStatus
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import java.text.SimpleDateFormat

@Composable
fun TripCard(
    trip: Trip,
    dateFormat: SimpleDateFormat,
    defaultCurrency: Currency?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    shape: CornerBasedShape
) {
    val isDraft = trip.status == TripStatus.DRAFT
    val isDark = isSystemInDarkTheme()

    val cardColor = when {
        isDraft && isDark -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        isDraft -> MaterialTheme.colorScheme.errorContainer
        isDark -> MaterialTheme.colorScheme.surfaceContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.vehicleName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDraft) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = { /* Share logic */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isDraft) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 1.dp)
            ) {
                Text(
                    text = trip.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDraft) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
            Row {
                Text(
                    text = "Start: ${trip.startMileage ?: "--"} km",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = robotoMonoBold,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "End: ${trip.endMileage ?: "--"} km",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = robotoMonoBold,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                Text(
                    text = "Fuel: ${trip.fuelFilled ?: "--"} Ltr",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = robotoMonoBold,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                trip.fuelEfficiency?.let {
                    Text(
                        text = "Efficiency: ${
                            String.format(
                                LocalLocale.current.platformLocale,
                                "%.2f",
                                it
                            )
                        } km/Ltr",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = robotoMonoBold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row {
                trip.tripDistance?.let {
                    Text(
                        text = "Distance: $it km",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = robotoMonoBold,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                trip.fuelCost?.let { cost ->
                    val currencySymbol = defaultCurrency?.symbol ?: trip.currencyId ?: ""
                    Text(
                        text = "Fuel Cost: $currencySymbol${
                            String.format(
                                LocalLocale.current.platformLocale,
                                "%.2f",
                                cost
                            )
                        }",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = robotoMonoBold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "Updated: ${dateFormat.format(trip.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontFamily = robotoMonoBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}