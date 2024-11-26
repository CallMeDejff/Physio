package com.dawidkubica.physio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun FilterableItemSelector(
    itemList: List<Pair<String, String>>,
    selectedItems: Set<String>,
    onToggleItem: (String) -> Unit,
    showSearchIcon: Boolean = true,
    onSearch: ((String) -> Unit)? = null
) {
    var showSearchField by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val filteredItems = remember(searchText, itemList) {
        if (searchText.isEmpty()) itemList
        else itemList.filter { it.second.contains(searchText, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showSearchIcon) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            showSearchField = !showSearchField
                        }
                        .background(Color.Transparent)
                        .border(2.dp, colorPrimary, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search Icon",
                        tint = colorPrimary
                    )
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredItems) { item ->
                    val isSelected = selectedItems.contains(item.first)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleItem(item.first) }
                            .border(
                                width = 1.dp,
                                color = if (isSelected) colorPrimary else Color.Gray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = item.second,
                            color = if (isSelected) colorPrimary else Color.Gray,
                            style = typography.labelMedium
                        )
                    }
                }
            }
        }

        if (showSearchField) {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    onSearch?.invoke(it)
                },
                textStyle = typography.labelMedium,
                placeholder = { Text("Podaj nazwę", style = typography.labelMedium) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, colorPrimary, RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                singleLine = true
            )
        }
    }
}
