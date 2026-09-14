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

package com.yg.mileage.ui.accountsScreen.components.personalInfoScreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.theme.MyMileageShapeDefaults

data class PersonalInfoItem(
    @StringRes val label: Int,
    @StringRes val contentDescription: Int,
    @DrawableRes val icon: Int? = null,
    val onClick: () -> Unit = {}
)

@Composable
fun PersonalInfoCard(
    topCard: PersonalInfoItem,
    middleCard: PersonalInfoItem,
    bottomCard: PersonalInfoItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MyMileageShapeDefaults.cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column {
            // Top item
            PersonalInfoRow(item = topCard, shape = MyMileageShapeDefaults.topListItemShape())

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.surface)

            // Middle item
            PersonalInfoRow(item = middleCard, shape = MyMileageShapeDefaults.middleListItemShape())

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.surface)

            // Bottom item
            PersonalInfoRow(item = bottomCard, shape = MyMileageShapeDefaults.bottomListItemShape())
        }
    }
}

@Composable
private fun PersonalInfoRow(item: PersonalInfoItem, shape: Shape) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = item.onClick
    ) {
        // Use Material 3's ListItem for a standard, polished look
        ListItem(
            headlineContent = { Text(text = stringResource(item.label), style = MaterialTheme.typography.labelMedium) },
            supportingContent = { Text(text = stringResource(item.contentDescription), style = MaterialTheme.typography.bodyLarge) },
            leadingContent = item.icon?.let { { Icon(painter = painterResource(it), contentDescription = null) } }
        )
    }
}
