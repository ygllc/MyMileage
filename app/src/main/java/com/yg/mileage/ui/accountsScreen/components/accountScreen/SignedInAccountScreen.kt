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

package com.yg.mileage.ui.accountsScreen.components.accountScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.yg.mileage.R
import com.yg.mileage.auth.UserData
import com.yg.mileage.ui.theme.MyMileageShapeDefaults
import com.yg.mileage.ui.theme.adaptiveContent

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignedInAccountScreen(
    currentUser: UserData,
    onSignOutClick: () -> Unit,
    onNavigateToPersonalInfo: () -> Unit, // New parameter
    onNavigateToSecurity: () -> Unit, // New parameter
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .adaptiveContent(windowWidthSizeClass),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Vehicles Header
        if (currentUser.profilePictureUrl != null) {
            AsyncImage(
                model = currentUser.profilePictureUrl,
                contentDescription = "Vehicles Picture",
                modifier = Modifier
                    .size(96.dp)
                    .clip(MaterialShapes.Cookie12Sided.toShape()),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.contacts_product),
                contentDescription = "Default Vehicles Picture",
                modifier = Modifier
                    .size(96.dp)
                    .clip(MaterialShapes.Cookie12Sided.toShape())

            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = currentUser.username ?: "User",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        if (currentUser.email != null) {
            Text(
                text = currentUser.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Account Info List
        // Outer card uses the grouped-card shape so it looks like Tomato's container
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MyMileageShapeDefaults.cardShape, // <-- use new card shape
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column {
                // Top item - use topListItemShape and make the whole surface clickable
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MyMileageShapeDefaults.topListItemShape(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { onNavigateToPersonalInfo() }
                ) {
                    AccountInfoRowContent(
                        icon = R.drawable.person,
                        title = "Personal info",
                        subtitle = "Name, email, phone, profiles",
                        iconBackgroundColor = Color(0xFFE0F7FA)
                    )
                }

                // Divider between rows (Tomato used a subtle separator)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 0.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                )

                // Bottom item - use bottomListItemShape and clickable
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MyMileageShapeDefaults.bottomListItemShape(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { onNavigateToSecurity() }
                ) {
                    AccountInfoRowContent(
                        icon = R.drawable.security,
                        title = "Security & sign-in",
                        subtitle = "Google password, recent security events",
                        iconBackgroundColor = Color(0xFFE8EAF6)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Single sign-out card - use cardShape for the outer card and topListItemShape inside so it looks like a single rounded row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MyMileageShapeDefaults.cardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MyMileageShapeDefaults.topListItemShape(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { onSignOutClick() }
            ) {
                AccountInfoRowContent(
                    icon = R.drawable.logout,
                    title = "Sign out",
                    subtitle = "Sign out of your MyMileage Account",
                    iconBackgroundColor = Color(0xFFFBE9E7)
                )
            }
        }
    }
}
