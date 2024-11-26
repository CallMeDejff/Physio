package com.dawidkubica.physio.screens.search

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.screens.search.components.ExercisePackageCard
import com.dawidkubica.physio.ui.components.FilterableItemSelector
import com.dawidkubica.physio.ui.theme.PurpleGrey80
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.gray
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun SearchScreen(
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingResults by viewModel.isLoadingResults.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val packagesToShow by viewModel.filteredPackages.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.apply {
            loadEquipmentList()
            loadConditionList()
            loadBodyPartsList()
        }
    }

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
        SearchContent(
            searchText = searchText,
            onSearchTextChanged = { searchText = it },
            isLoadingResults = isLoadingResults,
            onSearchClick = {
                viewModel.apply {
                    searchForMatchingPackages()
                    filterPackagesList(searchText)
                }
            },
            matchingPackages = packagesToShow,
            navigate = navigate,
            viewModel = viewModel
        )
    }
}

@Composable
fun FullScreenLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun SearchContent(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    isLoadingResults: Boolean,
    onSearchClick: () -> Unit,
    matchingPackages: List<ExercisePackage>,
    navigate: (String) -> Unit,
    viewModel: SearchViewModel
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp, top = 10.dp)
        ) {
            SearchHeader()

            SearchFilters(
                isLoadingResults = isLoadingResults,
                onSearchClick = onSearchClick,
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                viewModel = viewModel,
            )

            SearchResults(
                matchingPackages = matchingPackages,
                navigate = navigate
            )

        }
    }
}


@Composable
fun SearchHeader() {
    Text(
        text = buildAnnotatedString {
            append(" Znajdź swój ")
            withStyle(style = SpanStyle(color = colorPrimary)) {
                append("pakiet ćwiczeń.")
            }
        },
        style = typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SearchFilters(
    isLoadingResults: Boolean,
    onSearchClick: () -> Unit,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    viewModel: SearchViewModel
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(PurpleGrey80)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                placeholder = {
                    Text(
                        text = "Wpisz nazwę pakietu...",
                        style = typography.labelMedium,
                        maxLines = 1
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions.Default,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = colorPrimary,
                    unfocusedIndicatorColor = gray,
                )
            )
            if (isExpanded) {
                FilterSection(viewModel = viewModel)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .height(50.dp)
                        .weight(8f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
                ) {
                    if (isLoadingResults) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "search button",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Szukaj",
                            color = Color.White,
                            style = typography.labelLarge
                        )
                    }
                }

                Button(
                    onClick = { isExpanded = !isExpanded },
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary),
                    modifier = Modifier
                        .height(50.dp)
                        .weight(2f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        if (isExpanded) Icons.Outlined.ArrowDropUp else Icons.Outlined.ArrowDropDown,
                        contentDescription = "Expand"
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(viewModel: SearchViewModel) {
    FilterableItemSelector(
        itemList = viewModel.filteredConditionsList.collectAsState().value,
        selectedItems = viewModel.selectedConditions.collectAsState().value,
        onToggleItem = viewModel::toggleCondition,
        showSearchIcon = false
    )
    FilterableItemSelector(
        itemList = viewModel.filteredBodyPartsList.collectAsState().value,
        selectedItems = viewModel.selectedBodyParts.collectAsState().value,
        onToggleItem = viewModel::toggleBodyPart,
        showSearchIcon = false
    )
    FilterableItemSelector(
        itemList = viewModel.filteredEquipmentList.collectAsState().value,
        selectedItems = viewModel.selectedEquipment.collectAsState().value,
        onToggleItem = viewModel::toggleEquipment,
        onSearch = { query ->
            viewModel.apply {
                filterBodyPartsList(query)
                filterEquipmentList(query)
                filterConditionsList(query)
            }
        }
    )
}

@Composable
fun SearchResults(
    matchingPackages: List<ExercisePackage>,
    navigate: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (matchingPackages.isNotEmpty()) {
            items(matchingPackages) { pck ->
                ExercisePackageCard(
                    id = pck.id,
                    name = pck.name,
                    description = pck.description,
                    isPremium = pck.premium,
                    increased = true,
                    imageUrl = pck.mediaUrls.firstOrNull().toString(),
                    onClick = { navigate("exercise_screen/${pck.id}") }
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Brak wyników",
                        style = typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

