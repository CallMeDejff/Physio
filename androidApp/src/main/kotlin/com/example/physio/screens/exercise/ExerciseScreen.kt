package com.example.physio.screens.exercise

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.physio.screens.exercise.components.DescriptionView
import com.example.physio.screens.exercise.components.PreviewScreen
import com.example.physio.screens.sign_in.components.HeaderView
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.ghost_white
import com.example.physio.ui.theme.typography

@Composable
fun ExerciseScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    viewModel: ExerciseViewModel,
    packageId: String?
) {
    var selectedMediaUrl by remember { mutableStateOf<String?>(null) }

    if (packageId != null) {
        LaunchedEffect(packageId) {
            viewModel.getExercisePackage(packageId)
        }
    }
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colorPrimary)
        }
    } else {
        if (selectedMediaUrl != null) {
            PreviewScreen(mediaUrl = selectedMediaUrl!!) {
                selectedMediaUrl = null
            }
        } else {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (header, descriptionView, navigationButtons) = createRefs()

                HeaderView(
                    modifier = Modifier
                        .height(320.dp)
                        .fillMaxWidth()
                        .constrainAs(header) {
                            top.linkTo(parent.top)
                        },
                    200, 0.7f
                )

                Card(
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    colors = CardDefaults.cardColors(containerColor = ghost_white),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(descriptionView) {
                            top.linkTo(header.bottom)
                            bottom.linkTo(navigationButtons.top)
                        }
                ) {
                    LazyColumn(
                        Modifier.fillMaxSize()
                    ) {
                        item {
                            DescriptionView(viewModel = viewModel)
                        }

                        item {
                            ExercisesView(
                                viewModel = viewModel,
                                Modifier.heightIn(300.dp, 400.dp),
                                onMediaClick = { mediaUrl -> selectedMediaUrl = mediaUrl }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                        .constrainAs(navigationButtons) {
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    Button(
                        onClick = { viewModel.onGoBackClick(popBackStack) },
                        modifier = Modifier.weight(4f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
                    ) {
                        Text(
                            text = "Cofnij",
                            color = Color.White,
                            style = typography.labelLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = { viewModel.onGoBackClick(popBackStack) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = "Favorites management",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

