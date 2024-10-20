package com.example.physio.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.physio.R

val typography = androidx.compose.material3.Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),

    headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.helvetica_neue_bold)),
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp
    ),

    headlineMedium = TextStyle(
        fontSize = 19.sp,
        fontFamily = FontFamily(Font(R.font.helvetica_neue_bold)),
        fontWeight = FontWeight.Normal,
        lineHeight = 19.sp
    ),


    labelLarge = TextStyle(
        fontSize = 22.sp,
        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp
    ),
    labelMedium = TextStyle(
        fontSize = 19.sp,
        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
        fontWeight = FontWeight.Normal,
        lineHeight = 19.sp
    ),
    labelSmall = TextStyle(
        fontSize = 17.sp,
        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
        fontWeight = FontWeight.Normal,
        lineHeight = 17.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
        fontWeight = FontWeight.Normal,
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