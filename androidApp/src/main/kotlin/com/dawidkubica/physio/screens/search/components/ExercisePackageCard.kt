package com.dawidkubica.physio.screens.search.components

import android.text.Html
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ExercisePackageCard(
    id: String,
    name: String,
    description: String,
    isPremium: Boolean,
    increased: Boolean = false,
    imageUrl: String?,
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
            .fillMaxWidth()
            .border(width = 2.dp, color = colorPrimary, shape = RoundedCornerShape(16.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
            ) {
                if (isPremium) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Premium content",
                        tint = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(30.dp)
                            .zIndex(1f)
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Gray
                            )
                            .padding(4.dp)
                    )
                }
                if (imageUrl.isNullOrEmpty() || imageUrl == "null") {
                    HeaderView(
                        modifier = Modifier,
                        0, if (increased) 0.7f else 0.5f
                    )
                } else {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Exercise Image",
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(4f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = name,
                        style = typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (expanded) {
                        val decodedDescription =
                            Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY).toString()

                        val limitedDescription = if (decodedDescription.length > 100) {
                            decodedDescription.take(100) + "..."
                        } else {
                            decodedDescription
                        }

                        Text(
                            text = limitedDescription,
                            style = typography.labelMedium
                        )
                    }
                }

                if (increased) {
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = { expanded = !expanded }
                    ) {
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
    }
}