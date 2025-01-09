package com.dawidkubica.physio.screens.video_player

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forward5
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Replay5
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String,
    isPaused: Boolean = false,
    maxWidth: Boolean = false,
    onReplay: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var controlsVisible by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }
    var videoDuration by remember { mutableStateOf(0L) }
    var isVideoEnded by remember { mutableStateOf(false) }
    var videoAspectRatio by remember { mutableStateOf(16 / 9f) }
    var playerView: PlayerView? = null

    val scope = rememberCoroutineScope()

    val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(
            buildUponParameters()
                .setForceHighestSupportedBitrate(true)
        )
    }

    val renderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true)

    val exoPlayer = remember {
        ExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlayingState: Boolean) {
                        if (playbackState == Player.STATE_ENDED) {
                            isVideoEnded = true
                        }
                    }

                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        videoAspectRatio = if (videoSize.width > 0 && videoSize.height > 0) {
                            videoSize.width.toFloat() / videoSize.height
                        } else {
                            16 / 9f
                        }

                        playerView?.resizeMode = if (videoSize.height > videoSize.width) {
                            android.util.Log.d("VideoPlayer", "Video is landscape")
                            AspectRatioFrameLayout.RESIZE_MODE_FIT
                        } else {
                            android.util.Log.d("VideoPlayer", "Video is portrait")
                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                    }
                })
            }
    }

    fun showControls() {
        controlsVisible = true
        scope.launch {
            delay(1000)
            controlsVisible = false
        }
    }

    LaunchedEffect(videoUrl) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        if (!isPaused) exoPlayer.play()
    }

    LaunchedEffect(isPaused) {
        if (isPaused) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    LaunchedEffect(Unit) {
        controlsVisible = true
        scope.launch {
            delay(1000)
            controlsVisible = false
        }
        while (true) {
            currentPosition = exoPlayer.currentPosition
            videoDuration = exoPlayer.duration
            delay(500)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_DESTROY -> {
                    exoPlayer.release()
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { showControls() }
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    setOnClickListener { showControls() }
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier
                .then(if (maxWidth) Modifier.fillMaxWidth() else Modifier.fillMaxHeight())
                .align(Alignment.Center)
                .aspectRatio(videoAspectRatio)
                .clip(RoundedCornerShape(16.dp))
        )

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (isVideoEnded) {
                    ReplayControl(
                        onReplayClick = {
                            exoPlayer.seekTo(0)
                            exoPlayer.play()
                            isVideoEnded = false
                            onReplay()
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    VideoControls(
                        isPlaying = exoPlayer.isPlaying,
                        onPlayPauseClick = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                            showControls()
                        },
                        onRewindClick = {
                            exoPlayer.seekBack()
                            showControls()
                        },
                        onForwardClick = {
                            exoPlayer.seekForward()
                            showControls()
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                VideoProgressBar(
                    currentPosition = currentPosition,
                    videoDuration = videoDuration,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
fun ReplayControl(
    onReplayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onReplayClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Outlined.Replay,
            contentDescription = "Replay Video",
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun VideoControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onRewindClick: () -> Unit,
    onForwardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onRewindClick) {
            Icon(
                imageVector = Icons.Outlined.Replay5,
                contentDescription = "Rewind 5 seconds",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        IconButton(onClick = onPlayPauseClick) {
            Icon(
                imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        IconButton(onClick = onForwardClick) {
            Icon(
                imageVector = Icons.Outlined.Forward5,
                contentDescription = "Forward 5 seconds",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun VideoProgressBar(
    currentPosition: Long,
    videoDuration: Long,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = if (videoDuration > 0) currentPosition.toFloat() / videoDuration else 0f)
                .height(4.dp)
                .background(Color.White)
        )
    }
}
