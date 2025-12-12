/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2025  Yojit Ghadi
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

package com.yg.mileage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yg.mileage.auth.DriveService
import com.yg.mileage.data.AppDatabase
import com.yg.mileage.data.Repository

@Composable
fun SecuritySettingsScreen(carViewModel: CarViewModel) {
    val currentUser by carViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        currentUser?.let { user ->
            Text(
                text = "Account: ${user.email ?: "N/A"}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Placeholder security options
            SecurityOptionItem(title = "Change Password") {
                // TODO: Implement change password functionality
            }
            SecurityOptionItem(title = "Two-Step Verification") {
                // TODO: Implement 2SV setup
            }
            SecurityOptionItem(title = "Manage Devices") {
                // TODO: Implement device management
            }
            SecurityOptionItem(title = "Recent Security Activity") {
                // TODO: Implement recent activity display
            }

        } ?: run {
            Text("User not signed in or information unavailable.")
        }
    }
}

@Composable
private fun SecurityOptionItem(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp)
    ) {
        Text(title)
    }
}


@Preview
@Composable
fun SecuritySettingsScreenPreview() {
    val context = LocalContext.current
    val carViewModel: CarViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(context)
                val driveService = DriveService(context)
                val repository = Repository(database, driveService)
                @Suppress("UNCHECKED_CAST")
                return CarViewModel(repository) as T
            }
        }
    )
    SecuritySettingsScreen(carViewModel = carViewModel)
}

@Preview
@Composable
private fun SecurityOptionItemPreview() {
    SecurityOptionItem(title = "Sample Option") { }
}

