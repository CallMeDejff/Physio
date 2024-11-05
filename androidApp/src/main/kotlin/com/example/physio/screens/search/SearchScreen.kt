package com.example.physio.screens.search

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.physio.screens.search.components.ExercisePackageCard
import com.example.physio.ui.components.AutoComplete
import com.example.physio.ui.theme.PurpleGrey80
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.typography

@Composable
fun SearchScreen(
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingResults by viewModel.isLoadingResults.collectAsState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val equipmentList by viewModel.equipmentList.collectAsState()
    val selectedCondition by viewModel.selectedConditions.collectAsState()
    val conditionsList by viewModel.conditionsList.collectAsState()
    val matchingPackages by viewModel.matchingPackages.collectAsState()

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadEquipmentList()
        viewModel.loadConditionList()
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
                Column(
                    modifier = modifier
                        .padding(vertical = 32.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(" Znajdź swój ")
                            withStyle(style = SpanStyle(color = colorPrimary)) {
                                append("pakiet ćwiczeń.")
                            }
                        },
                        style = typography.headlineLarge,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(screenHeight * 2 / 5, screenHeight * 3 / 5)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(4.dp, Color.Transparent),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .wrapContentHeight(Alignment.Top)
                            .background(color = PurpleGrey80, shape = RoundedCornerShape(16.dp))
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            AutoComplete(
                                itemList = equipmentList,
                                selectedItems = selectedEquipment,
                                onToggleItem = { equipmentId ->
                                    viewModel.toggleEquipment(
                                        equipmentId
                                    )
                                }
                            )

                            AutoComplete(
                                itemList = conditionsList,
                                selectedItems = selectedCondition,
                                onToggleItem = { conditionId ->
                                    viewModel.toggleCondition(
                                        conditionId
                                    )
                                }
                            )

                            Button(
                                onClick = { viewModel.searchForMatchingPackages() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 2.dp,
                                        color = colorPrimary,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
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
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (matchingPackages.isNotEmpty()) {
                            items(matchingPackages) { (id, name, description) ->
                                ExercisePackageCard(
                                    id = id,
                                    name = name,
                                    description = description,
                                    expandable = true,
                                    onClick = { packageId ->
                                        navigate("exercise_screen/${packageId}")
                                    }
                                )
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
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
            }
        }
    }
}
