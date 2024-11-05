package com.example.physio.screens.favorites.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Card(
        modifier = Modifier
            .width(screenWidth * 7 / 10)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(imageVector = icon, contentDescription = "category icon")
                    Text(
                        text = title,
                        style = typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            items(exercisePackages) { exercisePackage ->
                ExercisePackageCard(
                    id = exercisePackage.id,
                    name = exercisePackage.name,
                    description = exercisePackage.description,
                    expandable = false,
                    onClick = onExerciseClick
                )
            }
        }
    }
}
