package com.example.physio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.colorSecondary
import com.example.physio.ui.theme.gray
import com.example.physio.ui.theme.typography
import kotlinx.coroutines.delay

@Composable
fun AutoComplete(
    itemList: List<Pair<String, String>>,
    selectedItems: Set<String>,
    onToggleItem: (String) -> Unit
) {
    var category by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
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
            .padding(8.dp)
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
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .focusRequester(focusRequester),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                }
            },
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

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                debounceState = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(color = Color.White)
        ) {
            val filteredItems = if (category.isNotEmpty()) {
                itemList.filter {
                    it.second.lowercase().contains(category.lowercase())
                }.sortedBy { it.second }
            } else {
                itemList.sortedBy { it.second }
            }.take(5)

            filteredItems.forEach { item ->
                val isSelected = selectedItems.contains(item.first)
                DropdownMenuItem(
                    onClick = {
                        onToggleItem(item.first)
                        category = ""
                        expanded = false
                        focusRequester.requestFocus()
                    },
                    text = {
                        Text(
                            text = item.second,
                            style = typography.labelMedium,
                            color = if (isSelected) {
                                colorSecondary
                            } else {
                                colorPrimary
                            }
                        )
                    }
                )
            }
        }

        if (selectedItems.isNotEmpty()) {
            Text(
                text = "Wybrane: ${
                    selectedItems.joinToString(", ") { id ->
                        itemList.find { it.first == id }?.second ?: ""
                    }
                }",
                style = typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}