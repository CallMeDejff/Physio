package com.example.physio.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.physio.R

// Set of Material typography styles to start with
val typography = androidx.compose.material3.Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    labelLarge = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontSize = 14.sp,
        fontFamily = FontFamily(Font(com.physio.android.R.font.helvetica_neue_regular)),
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(com.physio.android.R.font.helvetica_neue_regular)),
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(com.physio.android.R.font.helvetica_neue_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp
    )
    /* Other default text styles to override
    button = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.W500,
    fontSize = 14.sp
    ),
    caption = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
    )
    */
)