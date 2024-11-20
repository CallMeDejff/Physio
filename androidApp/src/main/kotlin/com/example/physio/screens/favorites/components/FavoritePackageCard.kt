package com.example.physio.screens.favorites.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.physio.screens.sign_in.components.HeaderView
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.typography

@Composable
fun FavoritePackageCard(
    id: String,
    name: String,
    mediaUrls: List<String>, // Dodajemy mediaUrls
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrl =
        if (mediaUrls.isNotEmpty()) mediaUrls[0] else "" // Sprawdzamy, czy mamy URL obrazu

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .clickable { onClick(id) }
            .border(width = 2.dp, color = colorPrimary, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth(0.6f) // Karta ma zajmować około 60% szerokości
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Jeśli mamy obrazek
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Exercise Image",
                    modifier = Modifier
                        .fillMaxWidth() // Obrazek zajmuje całą szerokość karty
                        .height(200.dp) // Obrazek ma mieć wysokość 2/3 karty
                        .clip(RoundedCornerShape(16.dp)) // Zaokrąglone rogi
                )
            } else {
                // Jeśli nie ma obrazu, używamy domyślnego HeaderView
                HeaderView(
                    modifier = Modifier
                        .fillMaxWidth() // Obrazek zajmuje całą szerokość
                        .height(200.dp), // Obrazek ma wysokość 200dp
                    0, 0.5f
                )
            }

            // Sekcja tekstowa z nazwą
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = name,
                    style = typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis // Dodajemy wielokropek jeśli tekst jest za długi
                )
            }
        }
    }
}

