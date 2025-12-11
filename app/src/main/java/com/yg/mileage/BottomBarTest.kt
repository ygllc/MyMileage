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

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.yg.mileage.ui.theme.MileageCalculatorTheme

data class NavBarItem(val label: String, val icon: ImageVector)

@Composable
fun FloatingBottomNavigationBar(
    items: List<NavBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    expandedFraction: Float,
    modifier: Modifier = Modifier
) {
    // Animate the corner radius based on expandedFraction
    val cornerRadius by animateDpAsState(
        targetValue = lerp(32.dp, 12.dp, expandedFraction),
        label = "NavBarCornerRadius"
    )
    val navBarShape: Shape = RoundedCornerShape(cornerRadius)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .shadow(8.dp, navBarShape),
        shape = navBarShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                IconButton(onClick = { onItemSelected(index) }) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
@Composable
@Preview
fun BottomBarPreview() {
FloatingBottomNavigationBar(
    items = listOf(
        NavBarItem("Home", Icons.Default.Home),
        NavBarItem("Search", Icons.Default.Search),
    ),
    selectedIndex = 0,
    onItemSelected = {},
    expandedFraction = 0.5f,
    modifier = Modifier.wrapContentHeight()
)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingNavBarDemo() {
    var expansionFraction by remember { mutableStateOf(0f) }

    val navBarElevation = 6.dp
    // Removed unused navBarCornerRadius. The animatedCornerRadius is used instead.
    // val navBarCornerRadius = 26.dp

    // Animate corner rounding and translation
    val animatedCornerRadius by animateDpAsState(
        targetValue = if (expansionFraction < 0.2f) {
            lerp(12.dp, 26.dp, (expansionFraction / 0.2f).coerceIn(0f, 1f))
        } else {
            26.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "CornerRadiusAnim"
    )

    val translateY by animateDpAsState(
        targetValue = lerp(0.dp, 36.dp, expansionFraction),
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "TranslateYAnim"
    )

    val density = LocalDensity.current
    val translateYPx = with(density) { translateY.toPx() }

    Scaffold(
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .graphicsLayer { translationY = translateYPx }
            ) {
                Surface(
                    shape = RoundedCornerShape(animatedCornerRadius),
                    tonalElevation = navBarElevation,
                    shadowElevation = navBarElevation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .align(Alignment.Center)
                ) {
                    // Replaced the Button and Text composables with IconButtons.
                    // This fixes the "unreachable code" warning caused by TODO()
                    // and aligns with the intended UI.
                    Row(
                        Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = { /* Handle Home click */ }) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        }
                        IconButton(onClick = { /* Handle Search click */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = { /* Handle Library click */ }) {
                            Icon(
                                imageVector = Icons.Filled.LibraryBooks,
                                contentDescription = "Library"
                            )
                        }
                    }
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Simulated player expansion: ${"%.2f".format(expansionFraction)}")
                Slider(
                    value = expansionFraction,
                    onValueChange = { expansionFraction = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FloatingNavBarDemoPreview() {
    MileageCalculatorTheme {
        FloatingNavBarDemo()
    }
}
