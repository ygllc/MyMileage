package com.yg.mileage.ui.tripsScreen.viewModels

import com.yg.mileage.data.TripGroupEntity

sealed class HistoryItem {
    data class SingleTrip(val trip: Trip) : HistoryItem()
    data class GroupHeader(val group: TripGroupEntity) : HistoryItem()
    data class GroupTrip(val trip: Trip, val isLast: Boolean) : HistoryItem()
}

sealed class SortableHistoryItem(val time: Long) {
    class SingleTripItem(val trip: Trip, time: Long) : SortableHistoryItem(time)
    class GroupBlock(val group: TripGroupEntity, val trips: List<Trip>, time: Long) : SortableHistoryItem(time)
}
