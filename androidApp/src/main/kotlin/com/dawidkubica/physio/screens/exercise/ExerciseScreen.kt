package com.dawidkubica.physio.screens.exercise

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.dawidkubica.physio.screens.exercise.components.DescriptionView
import com.dawidkubica.physio.screens.exercise.components.ExercisesView
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.screens.video_player.HorizontalPlayerScreen
import com.dawidkubica.physio.screens.video_player.PreviewScreen
import com.dawidkubica.physio.ui.components.FullScreenLoader
import com.dawidkubica.physio.ui.theme.typography

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
@ExperimentalMaterial3Api
fun ExerciseScreen(
    popBackStack: () -> Unit,
    viewModel: ExerciseViewModel,
    packageId: String?
) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation
    val fetchedCombinedExercises by viewModel.fetchedCombinedExercises.collectAsState()

    if (packageId != null) {
        LaunchedEffect(packageId) {
            viewModel.getExercisePackage(packageId)
        }
    }

    if (orientation == Configuration.ORIENTATION_LANDSCAPE && fetchedCombinedExercises.isNotEmpty()) {
        HorizontalPlayerScreen(
            exercises = fetchedCombinedExercises,
        )
    } else {
        VerticalScreen(
            popBackStack = popBackStack,
            packageId = packageId,
            viewModel = viewModel,
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalScreen(
    viewModel: ExerciseViewModel,
    packageId: String?,
    popBackStack: () -> Unit,
) {
    val context = LocalContext.current
    var selectedMediaUrl by remember { mutableStateOf<String?>(null) }
    var selectedMediaType by remember { mutableStateOf<String?>(null) }
    var isVideoPaused by remember { mutableStateOf(false) }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = remainingHeight,
            sheetContent = {
                BottomSheetContent(
                    isPaused = isVideoPaused,
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
            ScreenHeader(mediaUrl, headerHeight, viewModel)
        }

        if (selectedMediaUrl != null && selectedMediaType != null) {
            isVideoPaused = true
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
            ) {
                PreviewScreen(
                    mediaUrl = selectedMediaUrl!!,
                    mediaType = selectedMediaType!!,
                    isUrl = true,
                    onDismiss = {
                        isVideoPaused = false
                        selectedMediaUrl = null
                        selectedMediaType = null
                    }
                )
            }
        }
    }
}

@Composable
fun ScreenHeader(mediaUrl: String?, headerHeight: Dp, viewModel: ExerciseViewModel) {
    val headerHeightAnimated by animateDpAsState(targetValue = headerHeight, label = "")
    val isLoading by viewModel.isLoading.collectAsState()

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header) = createRefs()
        if (isLoading) {
            FullScreenLoader(
                Modifier
                    .constrainAs(header)
                    { top.linkTo(parent.top) },
            )
        } else {
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
}

@Composable
@ExperimentalMaterial3Api
fun BottomSheetContent(
    isPaused: Boolean,
    popBackStack: () -> Unit,
    viewModel: ExerciseViewModel,
    packageId: String?,
    onMediaClick: (String, String) -> Unit
) {
    var isPlayerReleased by remember { mutableStateOf(false) }

    DisposableEffect(isPlayerReleased) {
        if (isPlayerReleased) {
            popBackStack()
        }
        onDispose {}
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            DescriptionView(viewModel = viewModel, packageId = packageId)
        }
        item {
            ExercisesView(
                isPaused = isPaused,
                viewModel = viewModel,
                modifier = Modifier.wrapContentHeight(),
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

@Composable
fun NavigationButtons(
    popBackStack: () -> Unit,
    viewModel: ExerciseViewModel,
    packageId: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
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

        if (viewModel.checkUserAssigned()) {
            Button(
                onClick = { viewModel.removeFromUserAssigned(packageId.orEmpty()) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.AssignmentReturn,
                    contentDescription = "assigned management",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(2.dp)
                        .size(36.dp)
                )
            }
        }
    }
}






