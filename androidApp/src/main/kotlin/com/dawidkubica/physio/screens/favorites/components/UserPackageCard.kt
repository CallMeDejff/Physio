package com.dawidkubica.physio.screens.favorites.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.ui.components.PremiumLabel
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun UserPackageCard(
    id: String,
    name: String,
    isPremium: Boolean,
    imageUrl: String?,
    onClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable { onClick(id) }
            .width(320.dp)
            .fillMaxWidth()
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
            ) {
                if (isPremium) {
                    PremiumLabel(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .zIndex(1f)
                            .padding(8.dp),
                        label = "P",
                        typography = typography.headlineMedium
                    )
                }
                if (imageUrl.isNullOrEmpty() || imageUrl == "null") {
                    HeaderView(
                        modifier = Modifier,
                        0, 0.7f
                    )
                } else {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Exercise Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .heightIn(max = 100.dp)
                    .wrapContentHeight()
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
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
