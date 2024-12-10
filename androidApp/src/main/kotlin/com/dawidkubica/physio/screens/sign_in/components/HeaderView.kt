package com.dawidkubica.physio.screens.sign_in.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun HeaderView(
    modifier: Modifier,
    paddingValue: Int = 60,
    logoScale: Float = 1f,
    image: Int = R.drawable.logo_clear
) {
    val centeredImage = painterResource(id = image)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = centeredImage,
            contentDescription = "Centered Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(
                    top = 0.dp,
                    bottom = paddingValue.dp
                )
                .align(Alignment.Center)
                .scale(logoScale)
        )
    }
}