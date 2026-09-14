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

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage.ui.accountsScreen.screens

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yg.mileage.auth.UserData
import com.yg.mileage.ui.accountsScreen.components.accountScreen.SignedInAccountScreen

// Main entry point for the screen
@Composable
fun AccountScreen(
    currentUser: UserData?,
    onSignOutClick: () -> Unit,
    onNavigateToPersonalInfo: () -> Unit, // New parameter
    onNavigateToSecurity: () -> Unit, // New parameter
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact
) {
    if (currentUser != null) {
        SignedInAccountScreen(
            currentUser = currentUser,
            onSignOutClick = onSignOutClick,
            onNavigateToPersonalInfo = onNavigateToPersonalInfo, // Passed through
            onNavigateToSecurity = onNavigateToSecurity, // Passed through
            windowWidthSizeClass = windowWidthSizeClass
        )
    }
}
// --- PREVIEWS ---
@Preview(showBackground = true, name = "Signed In")
@Composable
fun AccountScreenPreview_UserLoggedIn() {
    val user = UserData(userId = "123", username = "John Doe", profilePictureUrl = null, email = "john.doe@example.com")
    AccountScreen(
        currentUser = user,
        onSignOutClick = {},
        onNavigateToPersonalInfo = {}, // Added for preview
        onNavigateToSecurity = {} // Added for preview
    )
}

@Preview(showBackground = true, name = "Signed Out - Google")
@Composable
fun AccountScreenPreview_UserLoggedOut() {
    AccountScreen(
        currentUser = null,
        onSignOutClick = {},
        onNavigateToPersonalInfo = {}, // Added for preview
        onNavigateToSecurity = {} // Added for preview
    )
}
