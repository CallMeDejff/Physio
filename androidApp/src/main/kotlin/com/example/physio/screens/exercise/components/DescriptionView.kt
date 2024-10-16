package com.example.physio.screens.exercise.components

import android.text.Html
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.physio.screens.exercise.ExerciseViewModel
import com.example.physio.ui.theme.typography

@Composable
fun DescriptionView(
    viewModel: ExerciseViewModel
) {
    val context = LocalContext.current
    val packageName = viewModel.packageName.collectAsState()
    val packageDescription = viewModel.packageDescription.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart),
        contentAlignment = Alignment.TopCenter

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = packageName.value.toString(),
                style = typography.bodyLarge,
            )

            Spacer(modifier = Modifier.size(16.dp))

            val decodedDescription =
                Html.fromHtml(packageDescription.value.toString(), Html.FROM_HTML_MODE_LEGACY)
            Text(
                text = decodedDescription.toString(),
                style = typography.labelMedium
            )
        }
    }
}