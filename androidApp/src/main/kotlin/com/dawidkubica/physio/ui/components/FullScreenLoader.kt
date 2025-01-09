package com.dawidkubica.physio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.R

@Composable
fun FullScreenLoader(
    modifier: Modifier? = Modifier,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val logoImage = painterResource(R.drawable.logo_clear)
        Image(
            painter = logoImage,
            contentDescription = "Centered Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .scale(0.7f)
        )
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(90.dp)
        )
    }
}