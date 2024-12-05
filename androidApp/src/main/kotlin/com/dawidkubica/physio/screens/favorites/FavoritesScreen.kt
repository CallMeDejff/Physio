package com.dawidkubica.physio.screens.favorites

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dawidkubica.physio.models.Category
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.Reminder
import com.dawidkubica.physio.screens.favorites.components.CategoryCard
import com.dawidkubica.physio.screens.favorites.components.CategoryTabs
import com.dawidkubica.physio.screens.favorites.components.DiscoverCard
import com.dawidkubica.physio.screens.favorites.components.WizardAccessButton
import com.dawidkubica.physio.screens.reminders.components.ReminderItem
import com.dawidkubica.physio.ui.components.FullScreenLoader
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
    val discoverPackages by viewModel._userFavoritePackagesList.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val nextReminder by viewModel.nextReminder.collectAsState()

    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    if (isLoading) {
        FullScreenLoader()
    } else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp, top = 10.dp)
                ) {
                    item {
                        GreetingSection(userName = userName)
                    }

                    item {
                        ReminderSection(nextReminder = nextReminder)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        CategorySection(
                            categories = categories,
                            selectedCategoryIndex = selectedCategoryIndex,
                            onCategorySelected = { selectedCategoryIndex = it },
                            onExerciseClick = { packageId ->
                                navController.navigate("exercise_screen/${packageId}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        DiscoverSection(
                            discoverPackages = discoverPackages,
                            onExerciseClick = { packageId ->
                                navController.navigate("exercise_screen/${packageId}")
                            }
                        )
                    }
                }

                if (userType == 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .wrapContentSize()
                    ) {
                        WizardAccessButton(
                            navController = navController,
                            viewModel = viewModel,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Column {
        Text(
            text = " Cześć, $userName 👋",
            style = typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = " Co dzisiaj zamierzasz ćwiczyć?",
            style = typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ReminderSection(nextReminder: Reminder?) {
    nextReminder?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            ReminderItem(
                reminder = it,
                deletable = false,
            )
        }
    }
}

@Composable
fun CategorySection(
    categories: List<Category>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onExerciseClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 4.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            CategoryTabs(
                categories = categories,
                selectedIndex = selectedCategoryIndex,
                onCategorySelected = onCategorySelected
            )

            val selectedCategory = categories.getOrNull(selectedCategoryIndex)

            selectedCategory?.let { category ->
                CategoryCard(
                    title = category.title,
                    icon = category.icon!!,
                    exercisePackages = category.exercisePackages,
                    onExerciseClick = onExerciseClick
                )
            }
        }
    }
}

@Composable
fun DiscoverSection(
    discoverPackages: List<ExercisePackage>,
    onExerciseClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(4.dp, MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(top = 4.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {

            Box(
                modifier = Modifier
                    //.shadow(2.dp, RoundedCornerShape(8.dp))
                    .fillMaxWidth(0.98f)
                    .height(52.dp)
                    //.border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.Start),
            ) {
                Column {
                    Text(
                        text = " Odkryj coś nowego: ",
                        style = typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.Start)
                    )
                    Text(
                        text = " Co dzisiaj zamierzasz ćwiczyć?",
                        style = typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            DiscoverCard(
                exercisePackages = discoverPackages,
                onExerciseClick = onExerciseClick
            )
        }
    }
}


