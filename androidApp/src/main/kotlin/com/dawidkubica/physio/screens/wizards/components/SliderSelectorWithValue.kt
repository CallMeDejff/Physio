package com.dawidkubica.physio.screens.wizards.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderSelectorWithValue(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    val thumbRadius = 20.dp
    val valueAsString = value.toInt().toString()
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = (valueRange.endInclusive - valueRange.start).toInt() - 1,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .height(thumbRadius * 2),
        thumb = {
            Canvas(
                modifier = Modifier
                    .size(thumbRadius * 2)
            ) {
                drawCircle(
                    color = primaryColor,
                    radius = thumbRadius.toPx()
                )
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = surfaceColor.toArgb()
                        textSize = 42f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(
                        valueAsString,
                        size.width / 2,
                        size.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
                        textPaint
                    )
                }
            }
        }
    )
}
