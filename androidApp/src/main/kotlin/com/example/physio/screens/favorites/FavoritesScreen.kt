package com.example.physio.screens.favorites

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.physio.screens.exercise.components.ButtonItem
import com.example.physio.screens.exercise.components.ButtonType
import com.example.physio.screens.exercise.components.MenuButtons
import com.example.physio.screens.favorites.components.CategoryCard
import com.example.physio.screens.favorites.components.WizardAccessButton
import com.example.physio.service.UserPreferences
import com.example.physio.ui.theme.PurpleGrey80
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.colorSecondary
import com.example.physio.ui.theme.typography

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableStateOf(ButtonType.ASSIGNED) }
    val categories by viewModel.fetchedCategories.collectAsState()

    val favorites by viewModel.fetchedFavorites.collectAsState()

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 25.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(vertical = 32.dp),
                ) {
                    item {
                        Text(
                            text = " Cześć, ${userPreferences.getUserName()} 👋",
                            style = typography.headlineLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 3 / 5)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    BorderStroke(4.dp, Color.Transparent),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(color = PurpleGrey80, shape = RoundedCornerShape(16.dp))
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(categories) { category ->
                                    CategoryCard(
                                        title = category.title,
                                        content = category.content
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    BorderStroke(4.dp, Color.Transparent),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(color = PurpleGrey80, shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text("Tekst", style = typography.bodyMedium)
                        }
                    }
                }

                if (userPreferences.getUserType() == 1) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        WizardAccessButton(
                            navController = navController,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }
}
