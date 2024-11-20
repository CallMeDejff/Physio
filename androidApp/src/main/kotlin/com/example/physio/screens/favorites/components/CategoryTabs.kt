package com.example.physio.screens.favorites.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.physio.models.Category
import com.example.physio.ui.theme.PurpleGrey80
import com.example.physio.ui.theme.typography

@Composable
fun CategoryTabs(
    categories: List<Category>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    BoxWithConstraints(
        Modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PurpleGrey80)
            .padding(4.dp)
    ) {
        if (categories.isNotEmpty()) {
            val maxWidth = this.maxWidth
            val tabWidth = maxWidth / categories.size

            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                label = "indicator offset"
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .width(tabWidth)
                    .fillMaxHeight()
                    .background(Color.White)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEachIndexed { index, category ->
                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onCategorySelected(index) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.title,
                            style = typography.labelMedium,
                            color = if (index == selectedIndex) Color.Black else Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

