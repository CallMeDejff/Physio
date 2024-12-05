package com.dawidkubica.physio.screens.favorites.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun CategoryCard(
    title: String,
    icon: ImageVector,
    exercisePackages: List<ExercisePackage>,
    onExerciseClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f))
    ) {
        if (exercisePackages.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(0.dp)
            ) {
                items(exercisePackages) { exercisePackage ->
                    UserPackageCard(
                        id = exercisePackage.id,
                        name = exercisePackage.name,
                        description = exercisePackage.description,
                        imageUrl = exercisePackage.mediaUrls.firstOrNull().toString(),
                        isPremium = exercisePackage.premium,
                        onClick = onExerciseClick
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(226.dp),
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