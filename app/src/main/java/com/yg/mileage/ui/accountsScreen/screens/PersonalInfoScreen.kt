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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yg.mileage.CarViewModel
import com.yg.mileage.R
import com.yg.mileage.ui.accountsScreen.components.personalInfoScreen.PersonalInfoCard
import com.yg.mileage.ui.accountsScreen.components.personalInfoScreen.PersonalInfoItem
import com.yg.mileage.ui.accountsScreen.components.personalInfoScreen.ProfileInfo

/**
 * MyMileage screen that displays the user's account details and profile information.
 *
 * This view centralizes the management of the current user's identity within the
 * MyMileage ecosystem, providing a branded interface to view profile headers and
 * update personal credentials such as names and display information.
 *
 * @param carViewModel The [CarViewModel] serving as the primary data source for user state and profile interactions.
 */
@Composable
fun PersonalInfoScreen(carViewModel: CarViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Your existing ProfileInfo (likely the header/image)
        ProfileInfo(carViewModel)

        // 2. Implementation of the Card with the topCard parameter
        PersonalInfoCard(
            topCard = PersonalInfoItem(
                icon = R.drawable.edit_name,
                label = R.string.change_name,
                contentDescription = R.string.change_name_description
            ),
            middleCard = PersonalInfoItem(
                icon = R.drawable.edit_name,
                label = R.string.change_name,
                contentDescription = R.string.change_name_description,
            ),
            bottomCard = PersonalInfoItem(
                icon = R.drawable.edit_name,
                label = R.string.change_name,
                contentDescription = R.string.change_name_description,
            )
        )
    }
}