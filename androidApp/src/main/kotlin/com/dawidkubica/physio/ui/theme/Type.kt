package com.dawidkubica.physio.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val typography = androidx.compose.material3.Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),

    headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 22.sp
    ),

    headlineMedium = TextStyle(
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 19.sp
    ),


    labelLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp
    ),
    labelMedium = TextStyle(
        fontSize = 19.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 19.sp
    ),
    labelSmall = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 17.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 28.sp
    ),

    bodyMedium = TextStyle(
        fontSize = 18.sp,
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