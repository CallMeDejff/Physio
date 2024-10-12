package com.example.physio.screens.search

import android.text.Html
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.physio.ui.AutoComplete
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.typography

@Composable
fun SearchScreen(
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingResults by viewModel.isLoadingResults.collectAsState()

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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = buildAnnotatedString {
                    append("Znajdź swój ")
                    withStyle(style = SpanStyle(color = colorPrimary)) {
                        append("pakiet ćwiczeń.")
                    }
                },
                style = typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            AutoComplete(
                itemList = equipmentList,
                selectedItems = selectedEquipment,
                onToggleItem = { equipmentId -> viewModel.toggleEquipment(equipmentId) }
            )

            AutoComplete(
                itemList = conditionsList,
                selectedItems = selectedCondition,
                onToggleItem = { conditionId -> viewModel.toggleCondition(conditionId) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.searchForMatchingPackages() },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.dp, color = colorPrimary, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
            ) {
                if (isLoadingResults) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search button",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Szukaj", color = Color.White, style = typography.labelLarge)
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


@Composable
private fun ExercisePackageCard(
    id: String,
    name: String,
    description: String,
    onClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable { onClick(id) }
            .border(width = 2.dp, color = colorPrimary, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = name,
                    style = typography.labelLarge
                )
                if (expanded) {
                    val decodedDescription = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                    Text(
                        text = decodedDescription.toString(),
                        style = typography.labelMedium
                    )
                }
            }

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription =
                    if (expanded) {
                        "show less"
                    } else {
                        "show more"
                    }
                )
            }
        }
    }
}
