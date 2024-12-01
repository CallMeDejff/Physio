package com.dawidkubica.physio.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun PremiumLabel(
    modifier: Modifier = Modifier,
    label: String
) {
    val gradientColors = listOf(Color.Magenta, Color.Cyan, Color.Magenta)
    val infiniteTransition = rememberInfiniteTransition()

    val currentFontSizePx = with(LocalDensity.current) { typography.labelMedium.fontSize.toPx() }
    val currentFontSizeDoublePx = currentFontSizePx * 15

    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = currentFontSizeDoublePx,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(animatedOffset, 0f),
        end = Offset(animatedOffset + currentFontSizeDoublePx, 0f),
        tileMode = TileMode.Repeated
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = brush,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(brush = brush)) {
                    append(label)
                }
            },
            style = typography.labelMedium
        )
    }
}