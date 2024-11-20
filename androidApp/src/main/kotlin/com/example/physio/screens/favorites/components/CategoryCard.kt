package com.example.physio.screens.favorites.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.physio.models.ExercisePackage
import com.example.physio.screens.search.components.ExercisePackageCard
import com.example.physio.ui.theme.typography

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
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        if (exercisePackages.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                items(exercisePackages) { exercisePackage ->
                    ExercisePackageCard(
                        id = exercisePackage.id,
                        name = exercisePackage.name,
                        description = exercisePackage.description,
                        imageUrl = exercisePackage.mediaUrls.firstOrNull().toString(),
                        increased = false,
                        onClick = onExerciseClick
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    text = "Brak elementów do wyświetlenia",
                    style = typography.labelMedium.copy(color = Color.Gray),
                )
            }
        }
    }
}