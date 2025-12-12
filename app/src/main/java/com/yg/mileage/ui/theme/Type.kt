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

@file:OptIn(ExperimentalTextApi::class)

package com.yg.mileage.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yg.mileage.R


val RobotoFlex = FontFamily(
    Font(R.font.roboto_flex_variable)
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
/* Other default text styles to override **/
val robotoFlexTopAppBar = FontFamily(
    Font(
        R.font.roboto_flex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.width(125f),
            FontVariation.weight(1000),
            FontVariation.grade(0), // <-- Int, not Float
            FontVariation.Setting("XOPQ", 96F),
            FontVariation.Setting("XTRA", 500f),
            FontVariation.Setting("YOPQ", 79f),
            FontVariation.Setting("YTAS", 750f),
            FontVariation.Setting("YTDE", -203f),
            FontVariation.Setting("YTFI", 738f),
            FontVariation.Setting("YTLC", 514f),
            FontVariation.Setting("YTUC", 712f)
        )
    )
)

val labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)
val googleFlex400 = FontFamily(
    Font(
        R.font.google_sans_flex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
            FontVariation.Setting("wdth", 100f),
            FontVariation.Setting("GRAD", 0f)
        )
    )
)

val googleFlex600 = FontFamily(
    Font(
        R.font.google_sans_flex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.Setting("wdth", 100f),
            FontVariation.Setting("GRAD", 0f)
        )
    )
)
val googleFlexRoundedHeadline = FontFamily(
    Font(
        R.font.google_sans_flex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(660),            // bold-ish
            FontVariation.Setting("wdth", 96f),  // slight condensation
            FontVariation.Setting("opsz", 48f),  // display optical size -> rounder shapes
            FontVariation.Setting("GRAD", 8f)    // small grade change for softer stems
        )
    )
)

// Body — normal opsz, comfortable weight
val googleFlexRoundedBody = FontFamily(
    Font(
        R.font.google_sans_flex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
            FontVariation.Setting("wdth", 100f),
            FontVariation.Setting("opsz", 14f),
            FontVariation.Setting("GRAD", 0f)
        )
    )
)