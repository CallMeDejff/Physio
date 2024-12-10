package com.dawidkubica.physio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.User
import com.dawidkubica.physio.ui.theme.RedConfirmed
import com.dawidkubica.physio.ui.theme.typography
import kotlinx.coroutines.delay

@Composable
fun AutoCompleteDetailed(
    itemList: List<Pair<String, String>> = emptyList(),
    userList: List<User> = emptyList(),
    selectedItems: Set<String>,
    onToggleItem: (String) -> Unit,
) {
    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var debounceState by remember { mutableStateOf(false) }

    LaunchedEffect(category) {
        if (debounceState) {
            delay(1000)
            expanded = category.isNotEmpty()
            debounceState = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
                debounceState = true
            },
            placeholder = { Text("Podaj nazwę", style = typography.labelMedium) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                debounceState = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
        ) {
            val filteredItems = when {
                itemList.isNotEmpty() -> {
                    if (category.isNotEmpty()) {
                        itemList.filter {
                            it.second.lowercase().contains(category.lowercase())
                        }.sortedBy { it.second }
                    } else {
                        itemList.sortedBy { it.second }
                    }.take(5)
                }

                userList.isNotEmpty() -> {
                    if (category.isNotEmpty()) {
                        userList.filter {
                            it.name.lowercase().contains(category.lowercase()) ||
                                    it.lastname.lowercase().contains(category.lowercase()) ||
                                    it.email.lowercase().contains(category.lowercase())
                        }.sortedBy { it.name }
                    } else {
                        userList.sortedBy { it.name }
                    }.take(5)
                }

                else -> emptyList()
            }

            filteredItems.forEach { item ->
                val itemId = if (item is Pair<*, *>) item.first as String else (item as User).uid
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                DropdownMenuItem(
                    colors = MenuItemColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        trailingIconColor = MaterialTheme.colorScheme.primary,
                        leadingIconColor= MaterialTheme.colorScheme.primary,
                        disabledTextColor= MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        disabledTrailingIconColor= MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        disabledLeadingIconColor= MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    ),
                    onClick = {
                        onToggleItem(itemId)
                        category = ""
                        expanded = false
                        focusRequester.requestFocus()
                    },
                    text = {
                        Text(
                            text = if (item is Pair<*, *>) item.second as String else "${(item as User).name} ${item.lastname}",
                            style = typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.background(Color.Transparent),
                )
                    }
            }
        }

        if (selectedItems.isNotEmpty()) {
            Text(
                text = "Wybrane:",
                style = typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                selectedItems.forEach { selectedItem ->
                    val itemLabel = itemList.find { it.first == selectedItem }?.second
                        ?: userList.find { it.uid == selectedItem }?.let {
                            "${it.name} ${it.lastname} (${it.email})"
                        } ?: "Nieznane"

                    RemovableItemCard(
                        label = itemLabel,
                        onRemove = { onToggleItem(selectedItem) }
                    )
                }
            }
        }
    }
}


@Composable
fun RemovableItemCard(label: String, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { onRemove() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Remove item",
                    tint = RedConfirmed
                )
            }
        }
    }
}
