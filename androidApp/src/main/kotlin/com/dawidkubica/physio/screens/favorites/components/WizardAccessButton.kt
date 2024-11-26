package com.dawidkubica.physio.screens.favorites.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dawidkubica.physio.screens.favorites.FavoritesViewModel
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.ghost_white

@Composable
fun WizardAccessButton(
    navController: NavController,
    viewModel: FavoritesViewModel
) {
    FloatingActionButton(
        onClick = {
            viewModel.onAddExerciseClick { route ->
                navController.navigate(route)
            }
        },
        containerColor = colorPrimary,
        contentColor = ghost_white,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 60.dp)
    ) {
        Icon(Icons.Outlined.Edit, contentDescription = "floating action button.")
    }
}