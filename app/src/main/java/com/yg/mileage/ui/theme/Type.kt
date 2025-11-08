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
@OptIn(ExperimentalTextApi::class)
val robotoFlexTopBar = FontFamily(
    Font(
        R.font.roboto_flex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.width(125f),
            FontVariation.weight(1000),
            FontVariation.grade(0),
            FontVariation.Setting("XOPQ", 96F),
            FontVariation.Setting("XTRA", 500F),
            FontVariation.Setting("YOPQ", 79F),
            FontVariation.Setting("YTAS", 750F),
            FontVariation.Setting("YTDE", -203F),
            FontVariation.Setting("YTFI", 738F),
            FontVariation.Setting("YTLC", 514F),
            FontVariation.Setting("YTUC", 712F)
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