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
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    val label: String
    val description: String

    @Serializable
    data object TripDetails : Screen {
        override val label = "Trip Details"
        override val description = "Create new trips"
    }

    @Serializable
    data object Vehicles : Screen {
        override val label = "Vehicles"
        override val description = "View and manage vehicles"
    }

    @Serializable
    data object Trips : Screen {
        override val label = "Trips"
        override val description = "View and manage trips"
    }

    @Serializable
    data class AddVehicle(val vehicleName: String? = null) : Screen {
        override val label = "Add Vehicle"
        override val description = "Add a new vehicle"
    }

    @Serializable
    data object Account : Screen {
        override val label = "Account"
        override val description = "Manage your account"
    }

    @Serializable
    data object PersonalInfo : Screen {
        override val label = "Personal Info"
        override val description = "Edit your personal info"
    }

    @Serializable
    data object SecuritySettings : Screen {
        override val label = "Security Settings"
        override val description = "Edit your security settings"
    }

    @Serializable
    data object CurrencySettings : Screen {
        override val label = "Currency & Fuel Prices"
        override val description = "Add and manage currencies"
    }

    @Serializable
    data object Activities : Screen {
        override val label = "Stats"
        override val description = "View stats"
    }

    @Serializable
    data object SignIn : Screen {
        override val label = "Sign In"
        override val description = "Sign in to your account"
    }

    @Serializable
    data object SignUp : Screen {
        override val label = "Sign Up"
        override val description = "Sign up for a new account"
    }
}

val Screen.icon: ImageVector?
    get() = when (this) {
        Screen.TripDetails -> Icons.Filled.Calculate
        Screen.Vehicles -> Icons.Rounded.DirectionsCar
        Screen.Trips -> Icons.Rounded.LocationOn
        is Screen.AddVehicle -> Icons.Filled.Person
        Screen.Account -> Icons.Filled.AccountCircle
        Screen.PersonalInfo -> Icons.Filled.AccountCircle
        Screen.SecuritySettings -> Icons.Filled.Security
        Screen.CurrencySettings -> Icons.Filled.AttachMoney
        Screen.Activities -> Icons.Rounded.CalendarMonth
        Screen.SignIn -> null
        Screen.SignUp -> null
    }

val bottomNavItems = listOf<Screen>(
    Screen.Vehicles,
    Screen.Trips,
    Screen.Activities,
)
