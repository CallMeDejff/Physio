package com.dawidkubica.physio.screens.favorites.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import com.dawidkubica.physio.models.Category
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun DiscoverCard(
    exercisePackages: List<ExercisePackage>,
    onExerciseClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            if (exercisePackages.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(4.dp)
                ) {
                    items(exercisePackages) { exercisePackage ->
                        UserPackageCard(
                            id = exercisePackage.id,
                            name = exercisePackage.name,
                            description = exercisePackage.description,
                            imageUrl = exercisePackage.mediaUrls.firstOrNull().toString(),
                            isPremium = exercisePackage.premium,
                            increased = false,
                            onClick = onExerciseClick
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center,
                )
                {
                    Text(
                        text = "Brak elementów do wyświetlenia",
                        style = typography.labelMedium.copy(color = Color.Gray),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}