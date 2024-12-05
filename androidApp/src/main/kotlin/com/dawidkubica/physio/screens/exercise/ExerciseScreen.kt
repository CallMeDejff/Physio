package com.dawidkubica.physio.screens.exercise

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dawidkubica.physio.screens.exercise.components.DescriptionView
import com.dawidkubica.physio.screens.exercise.components.ExercisesView
import com.dawidkubica.physio.screens.exercise.components.PreviewScreen
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
@ExperimentalMaterial3Api
fun ExerciseScreen(
    popBackStack: () -> Unit,
    viewModel: ExerciseViewModel,
    packageId: String?
) {
    val context = LocalContext.current
    var selectedMediaUrl by remember { mutableStateOf<String?>(null) }
    var selectedMediaType by remember { mutableStateOf<String?>(null) }
    val mediaUrl by viewModel.mediaUris.collectAsState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val headerHeight = 240.dp
    val remainingHeight = (screenHeight - headerHeight).coerceAtLeast(0.dp) + 20.dp

    val sheetState = rememberBottomSheetScaffoldState()

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    if (packageId != null) {
        LaunchedEffect(packageId) {
            viewModel.getExercisePackage(packageId)
        }
    }

    if (selectedMediaUrl != null && selectedMediaType != null) {
        PreviewScreen(
            mediaUrl = selectedMediaUrl!!,
            mediaType = selectedMediaType!!,
            isUrl = true,
            onDismiss = {
                selectedMediaUrl = null
                selectedMediaType = null
            }
        )
    } else {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = remainingHeight,
            sheetContent = {
                BottomSheetContent(
                    popBackStack = popBackStack,
                    viewModel = viewModel,
                    packageId = packageId,
                    onMediaClick = { url, type ->
                        selectedMediaUrl = url
                        selectedMediaType = type
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenHeader(mediaUrl, headerHeight)
        }
    }
}

@Composable
fun ScreenHeader(mediaUrl: String?, headerHeight: Dp) {
    val headerHeightAnimated by animateDpAsState(targetValue = headerHeight, label = "")

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header) = createRefs()

        if (mediaUrl.isNullOrEmpty() || mediaUrl == "null") {
            HeaderView(
                modifier = Modifier
                    .height(headerHeightAnimated)
                    .fillMaxWidth()
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                    },
                100, 0.7f
            )
        } else {
            AsyncImage(
                model = mediaUrl,
                contentDescription = "Exercise Image",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .height(headerHeightAnimated)
                    .fillMaxWidth()
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                    }
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun BottomSheetContent(
    popBackStack: () -> Unit,
    viewModel: ExerciseViewModel,
    packageId: String?,
    onMediaClick: (String, String) -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    var isPlayerReleased by remember { mutableStateOf(false) }

    DisposableEffect(isPlayerReleased) {
        if (isPlayerReleased) {
            popBackStack()
        }
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    DescriptionView(viewModel = viewModel)
                }

                item {
                    ExercisesView(
                        viewModel = viewModel,
                        Modifier.wrapContentHeight(),
                        onMediaClick = onMediaClick
                    )
                }

                item {
                    NavigationButtons(
                        popBackStack = popBackStack,
                        viewModel = viewModel,
                        packageId = packageId
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun NavigationButtons(
    popBackStack: () -> Unit,
    viewModel: ExerciseViewModel,
    packageId: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.onGoBackClick(popBackStack) },
            modifier = Modifier.weight(4f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Cofnij",
                color = Color.White,
                style = typography.labelLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Button(
            onClick = { viewModel.togglePackageFavoriteStatus(packageId.orEmpty()) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Outlined.Favorite,
                contentDescription = "Favorites management",
                tint = Color.White,
                modifier = Modifier
                    .padding(4.dp)
                    .size(36.dp)
            )
        }
    }
}






