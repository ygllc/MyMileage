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

package com.yg.mileage.ui.accountsScreen.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yg.mileage.CarViewModel
import com.yg.mileage.auth.DriveService
import com.yg.mileage.data.AppDatabase
import com.yg.mileage.data.Repository
import com.yg.mileage.ui.accountsScreen.components.securitySettingsScreen.ChangePasswordDialog
import com.yg.mileage.ui.accountsScreen.components.securitySettingsScreen.SecurityOptionItem

@Composable
fun SecuritySettingsScreen(carViewModel: CarViewModel) {
    val currentUser by carViewModel.currentUser.collectAsState()
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                showChangePasswordDialog = true
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

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { newPassword ->
                carViewModel.changePassword(newPassword) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        showChangePasswordDialog = false
                    } else {
                        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun SecuritySettingsScreenPreview() {
    val context = LocalContext.current
    val carViewModel: CarViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
