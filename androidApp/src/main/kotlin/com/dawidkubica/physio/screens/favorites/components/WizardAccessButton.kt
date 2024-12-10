package com.dawidkubica.physio.screens.favorites.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dawidkubica.physio.screens.favorites.FavoritesViewModel

@Composable
fun WizardAccessButton(
    navController: NavController,
    viewModel: FavoritesViewModel,
) {
    FloatingActionButton(
        onClick = {
            viewModel.onAddExerciseClick { route ->
                navController.navigate(route)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 60.dp)
    ) {
        Icon(Icons.Outlined.Edit, contentDescription = "floating action button.", tint = Color.White)
    }
}