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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yg.mileage.CarViewModel
import com.yg.mileage.data.TripGroupEntity
import com.yg.mileage.ui.theme.MyMileageShapeDefaults
import com.yg.mileage.ui.theme.adaptiveContent
import com.yg.mileage.ui.tripsScreen.screens.FilterBottomSheetContent
import com.yg.mileage.ui.tripsScreen.screens.HistoryItem
import com.yg.mileage.ui.tripsScreen.screens.SortableHistoryItem
import com.yg.mileage.ui.tripsScreen.viewModels.Trip
import com.yg.mileage.ui.tripsScreen.viewModels.TripStatus
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripsScreenContent(
    modifier: Modifier = Modifier,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    isTwoPane: Boolean = false,
    trips: List<Trip>,
    tripGroups: List<TripGroupEntity>,
    defaultCurrency: Currency?,
    selectedTripForPane: Trip?,
    showNewTripPane: Boolean,
    onNavigateToTripDetails: () -> Unit,
    onEditTrip: (Trip) -> Unit,
    onDeleteTrip: (String) -> Unit,
    onNewTrip: () -> Unit,
    onNewGroupTrip: () -> Unit,
    onAddTripToGroup: (String) -> Unit,
    onEditGroup: (TripGroupEntity) -> Unit,
    onDeleteGroup: (TripGroupEntity) -> Unit,
    carViewModel: CarViewModel
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    var filterIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Prepare list items
    val historyItems = remember(trips, tripGroups, filterIndex) {
        val filteredTrips = when (filterIndex) {
            1 -> trips.filter { it.status == TripStatus.COMPLETED }
            2 -> trips.filter { it.status == TripStatus.DRAFT }
            else -> trips
        }

        val items = mutableListOf<HistoryItem>()
        val tripsByGroup = filteredTrips.filter { it.tripGroup != null }.groupBy { it.tripGroup!! }
        val orphanTrips = filteredTrips.filter { it.tripGroup == null }

        val tempItems = mutableListOf<SortableHistoryItem>()

        // Add Groups
        tripGroups.forEach { group ->
            val groupTrips = tripsByGroup[group.id]?.sortedByDescending { it.updatedAt } ?: emptyList()
            val sortTime = group.updatedAt.time.coerceAtLeast(groupTrips.firstOrNull()?.updatedAt?.time ?: 0L)
            tempItems.add(SortableHistoryItem.GroupBlock(group, groupTrips, sortTime))
        }

        // Add Orphan Trips
        orphanTrips.forEach { trip ->
            tempItems.add(SortableHistoryItem.SingleTripItem(trip, trip.updatedAt.time))
        }

        // Sort by time descending
        tempItems.sortByDescending { it.time }

        // Flatten to HistoryItem list
        tempItems.forEach { item ->
            when (item) {
                is SortableHistoryItem.SingleTripItem -> {
                    items.add(HistoryItem.SingleTrip(item.trip))
                }
                is SortableHistoryItem.GroupBlock -> {
                    items.add(HistoryItem.GroupHeader(item.group))
                    item.trips.forEachIndexed { index, trip ->
                        val isLast = index == item.trips.lastIndex
                        items.add(HistoryItem.GroupTrip(trip, isLast))
                    }
                }
            }
        }
        items
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            FilterBottomSheetContent(
                onApply = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            )
        }
    }

    // Sync editing trip state with selectedTripForPane / new-trip flag for two-pane mode
    LaunchedEffect(selectedTripForPane, showNewTripPane, isTwoPane) {
        if (isTwoPane) {
            if (selectedTripForPane != null) {
                carViewModel.setEditingTrip(selectedTripForPane)
                carViewModel.setEditingTripGroupId(selectedTripForPane.tripGroup)
            } else if (showNewTripPane) {
                // Show new trip form in the pane
                carViewModel.setEditingTrip(null)
                carViewModel.setEditingTripGroupId(null)
            }
        }
    }

    if (isTwoPane) {
        val hasPaneContent = showNewTripPane || selectedTripForPane != null
        // Two-pane layout for medium/expanded widths
        Row(
            modifier = modifier
                .fillMaxSize()
                .adaptiveContent(windowWidthSizeClass)
        ) {
            // Left pane: Trip list
            Column(
                modifier = Modifier
                    .then(
                        if (hasPaneContent) {
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        } else {
                            Modifier.fillMaxSize()
                        }
                    )
                    .padding(vertical = 14.dp)
            ) {
                // FILTER SEGMENTED BUTTONS AT THE TOP
                TripHistoryFilterSegmented(
                    selectedIndex = filterIndex,
                    onSelected = { filterIndex = it },
                    onFilterClick = { showBottomSheet = true }
                )

                Spacer(Modifier.height(12.dp)) // space between filter and list

                if (historyItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No trips to display.\nCreate or change your filter!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(historyItems) { item ->
                            when (item) {
                                is HistoryItem.SingleTrip -> {
                                    Box(modifier = Modifier.padding(vertical = 7.dp)) {
                                        TripCard(
                                            trip = item.trip,
                                            dateFormat = dateFormat,
                                            defaultCurrency = defaultCurrency,
                                            onEdit = { onEditTrip(item.trip) },
                                            onDelete = { onDeleteTrip(item.trip.id) },
                                            shape = MyMileageShapeDefaults.cardShape
                                        )
                                    }
                                }
                                is HistoryItem.GroupHeader -> {
                                    Box(modifier = Modifier.padding(top = 14.dp)) {
                                        GroupHeaderItem(
                                            group = item.group,
                                            onAddTrip = { onAddTripToGroup(item.group.id) },
                                            onEdit = { onEditGroup(item.group) },
                                            onDelete = { onDeleteGroup(item.group) }
                                        )
                                    }
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 0.dp),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }

                                is HistoryItem.GroupTrip -> {
                                    val shape = if (item.isLast) MyMileageShapeDefaults.bottomListItemShape() else MyMileageShapeDefaults.middleListItemShape()
                                    TripCard(
                                        trip = item.trip,
                                        dateFormat = dateFormat,
                                        defaultCurrency = defaultCurrency,
                                        onEdit = { onEditTrip(item.trip) },
                                        onDelete = { onDeleteTrip(item.trip.id) },
                                        shape = shape
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 0.dp),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for FAB
                        }
                    }
                }

                // Floating Action Button for adding new trip (left pane only)
                Box(Modifier.fillMaxSize()) {
                    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

                    FloatingActionButtonMenu(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        expanded = fabMenuExpanded,
                        button = {
                            val containerColor = MaterialTheme.colorScheme.primaryContainer
                            ToggleFloatingActionButton(
                                modifier = Modifier
                                    .semantics {
                                        stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                                        contentDescription = "Toggle menu"
                                    }
                                    .animateFloatingActionButton(
                                        visible = true, alignment = Alignment.BottomEnd
                                    ),
                                checked = fabMenuExpanded,
                                containerColor = { containerColor },
                                onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
                            ) {
                                val imageVector by remember {
                                    derivedStateOf {
                                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                                    }
                                }
                                Icon(
                                    painter = rememberVectorPainter(imageVector),
                                    contentDescription = null,
                                    modifier = Modifier.animateIcon({ checkedProgress })
                                )
                            }
                        }
                    ) {
                        // --- Menu Content ---

                        // Item 1: New Trip
                        FloatingActionButtonMenuItem(
                            onClick = {
                                fabMenuExpanded = false
                                onNewTrip()
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = "Add New Trip"
                                )
                            },
                            text = { Text(text = "New Trip") },
                        )

                        // --- Item 2: New Group Trip ---
                        FloatingActionButtonMenuItem(
                            onClick = {
                                fabMenuExpanded = false
                                onNewGroupTrip()
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.GroupAdd,
                                    contentDescription = "New Group Trip"
                                )
                            },
                            text = { Text(text = "New Group Trip") },
                        )
                    }
                }
            }

            if (hasPaneContent) {
                // Divider between panes
                HorizontalDivider(
                    modifier = Modifier.width(1.dp),
                    thickness = 1.dp
                )

                // Right pane: Trip details
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    TripDetailsPane(
                        carViewModel = carViewModel,
                        selectedTrip = selectedTripForPane,
                        showNewTripPane = showNewTripPane
                    )
                }
            }
        }
    } else {
        // Single-pane layout for compact width (phones) - existing behavior
        Box(
            modifier = modifier
                .fillMaxSize()
                .adaptiveContent(windowWidthSizeClass)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 14.dp)
            ) {
                // FILTER SEGMENTED BUTTONS AT THE TOP
                TripHistoryFilterSegmented(
                    selectedIndex = filterIndex,
                    onSelected = { filterIndex = it },
                    onFilterClick = { showBottomSheet = true }
                )

                Spacer(Modifier.height(12.dp)) // space between filter and list

                if (historyItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No trips to display.\nCreate or change your filter!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(historyItems) { item ->
                            when (item) {
                                is HistoryItem.SingleTrip -> {
                                    Box(modifier = Modifier.padding(vertical = 7.dp)) {
                                        TripCard(
                                            trip = item.trip,
                                            dateFormat = dateFormat,
                                            defaultCurrency = defaultCurrency,
                                            onEdit = { onEditTrip(item.trip) },
                                            onDelete = { onDeleteTrip(item.trip.id) },
                                            shape = MyMileageShapeDefaults.cardShape
                                        )
                                    }
                                }
                                is HistoryItem.GroupHeader -> {
                                    Box(modifier = Modifier.padding(top = 14.dp)) {
                                        GroupHeaderItem(
                                            group = item.group,
                                            onAddTrip = { onAddTripToGroup(item.group.id) },
                                            onEdit = { onEditGroup(item.group) },
                                            onDelete = { onDeleteGroup(item.group) }
                                        )
                                    }
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 0.dp),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }

                                is HistoryItem.GroupTrip -> {
                                    val shape = if (item.isLast) MyMileageShapeDefaults.bottomListItemShape() else MyMileageShapeDefaults.middleListItemShape()
                                    TripCard(
                                        trip = item.trip,
                                        dateFormat = dateFormat,
                                        defaultCurrency = defaultCurrency,
                                        onEdit = { onEditTrip(item.trip) },
                                        onDelete = { onDeleteTrip(item.trip.id) },
                                        shape = shape
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 0.dp),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for FAB
                        }
                    }
                }
            }

            // Floating Action Button for adding new trip
            Box(Modifier.fillMaxSize()) {
                var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

                FloatingActionButtonMenu(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    expanded = fabMenuExpanded,
                    button = {
                        val containerColor = MaterialTheme.colorScheme.primaryContainer
                        ToggleFloatingActionButton(
                            modifier = Modifier
                                .semantics {
                                    stateDescription =
                                        if (fabMenuExpanded) "Expanded" else "Collapsed"
                                    contentDescription = "Toggle menu"
                                }
                                .animateFloatingActionButton(
                                    visible = true, alignment = Alignment.BottomEnd
                                ),
                            checked = fabMenuExpanded,
                            containerColor = { containerColor },
                            onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
                        ) {
                            val imageVector by remember {
                                derivedStateOf {
                                    if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                                }
                            }
                            Icon(
                                painter = rememberVectorPainter(imageVector),
                                contentDescription = null,
                                modifier = Modifier.animateIcon({ checkedProgress })
                            )
                        }
                    }
                ) {
                    // --- Menu Content ---

                    // Item 1: New Trip
                    FloatingActionButtonMenuItem(
                        onClick = {
                            fabMenuExpanded = false
                            onNewTrip()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add New Trip"
                            )
                        },
                        text = { Text(text = "New Trip") },
                    )

                    // --- Item 2: New Group Trip ---
                    FloatingActionButtonMenuItem(
                        onClick = {
                            fabMenuExpanded = false
                            onNewGroupTrip()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.GroupAdd,
                                contentDescription = "New Group Trip"
                            )
                        },
                        text = { Text(text = "New Group Trip") },
                    )
                }
            }
        }
    }
}
