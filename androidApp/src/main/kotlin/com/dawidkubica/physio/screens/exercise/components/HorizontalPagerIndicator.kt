package com.dawidkubica.physio.screens.exercise.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.typography
import kotlin.math.absoluteValue

@Composable
fun HorizontalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    targetPage: Int,
    currentPageOffsetFraction: Float,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.DarkGray,
    unselectedIndicatorSize: Dp = 8.dp,
    selectedIndicatorSize: Dp = 8.dp,
    indicatorCornerRadius: Dp = 8.dp,
    indicatorPadding: Dp = 8.dp,
    textColor: Color = Color.Gray
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .wrapContentSize()
            .height(selectedIndicatorSize + indicatorPadding * 2)
    ) {
        Text(
            text = "${currentPage + 1}",
            color = textColor,
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.CenterVertically),
            style = typography.labelSmall
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(indicatorPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { page ->
                val (color, size) =
                    if (currentPage == page || targetPage == page) {
                        val pageOffset =
                            ((currentPage - page) + currentPageOffsetFraction).absoluteValue
                        val offsetPercentage = 1f - pageOffset.coerceIn(0f, 1f)

                        val size = unselectedIndicatorSize +
                                ((selectedIndicatorSize - unselectedIndicatorSize) * offsetPercentage)

                        indicatorColor.copy(alpha = offsetPercentage) to size
                    } else {
                        indicatorColor.copy(alpha = 0.1f) to unselectedIndicatorSize
                    }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(indicatorCornerRadius))
                        .background(color)
                        .width(size)
                        .height(size)
                )
            }
        }

        Text(
            text = "$pageCount",
            color = textColor,
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically),
            style = typography.labelSmall
        )
    }
}
