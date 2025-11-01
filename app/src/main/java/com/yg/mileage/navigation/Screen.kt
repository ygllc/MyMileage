package com.yg.mileage.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object TripDetails : Screen("trip_details", "Trip Details", Icons.Filled.Calculate)
    object Profile : Screen("profile", "Vehicles", Icons.Rounded.DirectionsCar)
    object TripLog : Screen("trip_log", "Trips", Icons.Rounded.LocationOn)
    object AddVehicle : Screen("add_vehicle", "Add Vehicle", Icons.Filled.Person)
    object Account : Screen("account", "Account", Icons.Filled.AccountCircle)
    object PersonalInfo : Screen("personal_info", "Personal Info", Icons.Filled.AccountCircle) // Placeholder icon
    object SecuritySettings : Screen("security_settings", "Security Settings", Icons.Filled.Security)
    object CurrencySettings : Screen("currency_settings", "Currency & Fuel Prices", Icons.Filled.AttachMoney)
    object Activities : Screen("activities", "Stats", Icons.Rounded.CalendarMonth)
}

val bottomNavItems = listOf(
    Screen.Profile,
    Screen.TripLog,
    Screen.Activities,
)
