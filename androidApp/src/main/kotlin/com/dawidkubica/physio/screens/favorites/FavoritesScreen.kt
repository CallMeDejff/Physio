package com.dawidkubica.physio.screens.favorites

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dawidkubica.physio.screens.favorites.components.CategoryCard
import com.dawidkubica.physio.screens.favorites.components.CategoryTabs
import com.dawidkubica.physio.screens.favorites.components.WizardAccessButton
import com.dawidkubica.physio.screens.reminders.components.ReminderItem
import com.dawidkubica.physio.ui.theme.PurpleGrey80
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by viewModel.fetchedCategories.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val nextReminder by viewModel.nextReminder.collectAsState()

    var selectedCategoryIndex by remember { mutableStateOf(0) }

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
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
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp, top = 10.dp)
                ) {
                    Text(
                        text = "Cześć, $userName 👋",
                        style = typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (nextReminder !== null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    BorderStroke(4.dp, Color.Transparent),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(color = PurpleGrey80, shape = RoundedCornerShape(16.dp))
                                .padding(4.dp)
                                .align(Alignment.CenterHorizontally),
                        ) {
                            nextReminder?.let { reminder ->
                                ReminderItem(
                                    reminder = reminder,
                                    deletable = false,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(4.dp, Color.Transparent),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(color = PurpleGrey80, shape = RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            CategoryTabs(
                                categories = categories,
                                selectedIndex = selectedCategoryIndex,
                                onCategorySelected = { index -> selectedCategoryIndex = index }
                            )

                            val selectedCategory = categories.getOrNull(selectedCategoryIndex)

                            selectedCategory?.let { category ->
                                CategoryCard(
                                    title = category.title,
                                    icon = category.icon,
                                    exercisePackages = category.exercisePackages,
                                    onExerciseClick = { packageId ->
                                        navController.navigate("exercise_screen/${packageId}")
                                    }
                                )
                            }
                        }
                    }
                }

                if (userType == 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
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



