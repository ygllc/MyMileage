@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yg.mileage.ui.theme.MileageCalculatorTheme
import com.yg.mileage.ui.theme.primaryLight

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MileageCalculatorTheme {
                MainScreen()
            }
        }
    }
}

@Preview
@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Welcome to Expressive UI",
                style = MaterialTheme.typography.headlineMedium,
                color = primaryLight
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {},
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("Click Me")
            }
        }
    }
}
@Preview
@Composable
fun ContainedLoading() {
    LoadingIndicator(
        color = primaryLight,
        polygons = listOf(
            MaterialShapes.SoftBurst,
            MaterialShapes.Cookie9Sided,
            MaterialShapes.Pentagon,
            MaterialShapes.Pill,
            MaterialShapes.Sunny,
            MaterialShapes.Cookie4Sided,
            MaterialShapes.Oval,
            MaterialShapes.VerySunny,
            MaterialShapes.Ghostish,
            MaterialShapes.Cookie12Sided
        )
    )
    ContainedLoadingIndicator()
}
@Preview
@Composable
fun SplitButtonDemo() {
    var checked2 by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        SplitButtonLayout(leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = { },
            ) {
                Icon(
                    Icons.Filled.Edit,
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                    contentDescription = "Localized description",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("My Button")
            }
        }, trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked = checked2,
                onCheckedChange = { checked2 = it },
            ) {
                val rotation: Float by animateFloatAsState(
                    targetValue = if (checked2) 180f else 0f, label = "Trailing Icon Rotation"
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier
                        .size(SplitButtonDefaults.TrailingIconSize)
                        .graphicsLayer {
                            this.rotationZ = rotation
                        },
                    contentDescription = "Localized description"
                )
            }
        })

        DropdownMenu(expanded = checked2, onDismissRequest = { checked2 = false }) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {},
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) })
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {},
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) })
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Send Feedback") },
                onClick = {},
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) })
        }
    }
}
/**
 * Optional Path to be created for the RoundRect if the corner radii are not identical This
 * is because Canvas has a built in API for drawing round rectangles with the same corner
 * radii in all 4 corners. However, if each corner has a different corner radii, a path must
 * be drawn instead
 */

@Composable
fun ContactListItem(name: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp), // Add padding around the card
        shape = RoundedCornerShape(12.dp), // Adjust the corner radius as needed
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Optional: Add elevation
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(16.dp) // Padding for the content inside the card
        )
    }
}

@Composable
@Preview
fun SplitButton() {
    val options = listOf("Breakfast", "Lunch", "Snack", "Dinner")
    var selectedIndex by remember { mutableIntStateOf(0) }

    ButtonGroup(
        modifier = Modifier.padding(horizontal = 8.dp),
        overflowIndicator = {}
    ) {
        options.forEachIndexed { index, label ->
            toggleableItem(
                checked = selectedIndex == index,
                onCheckedChange = { selectedIndex = index },
                label = label,
            )
        }
}

@Composable
fun MLKitTextRecognitionButton() {
    Button(
        onClick = {  },
    ) {
        Text("Recognize Text")
    }
  }
}

