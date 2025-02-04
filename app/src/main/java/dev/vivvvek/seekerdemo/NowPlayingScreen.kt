/*
 * Copyright 2023 Vivek Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.vivvvek.seekerdemo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import dev.vivvvek.seeker.Segment
import dev.vivvvek.seeker.rememberSeekerState
import dev.vivvvek.seekerdemo.ui.theme.SeekerTheme

@Composable
fun NowPlayingScreen() {
    val viewModel: NowPlayingViewModel = viewModel()
    val position by viewModel.position.collectAsState()
    val readAheadPosition by viewModel.readAheadPosition.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isDragging by interactionSource.collectIsDraggedAsState()

    val gap by animateDpAsState(if (isDragging) 2.dp else 0.dp, label = "gap")
    val thumbRadius by animateDpAsState(if (isDragging) 12.dp else 6.dp, label = "thumb radius")

    val seekerState = rememberSeekerState()

    val range = 0f..viewModel.length

    Scaffold(
        topBar = { TopBar() }
    ) {
        Column(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .aspectRatio(1f)
                    .padding(56.dp)
                    .background(
                        color = MaterialTheme.colors.surface.copy(0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
            )

            Text(
                text = "Podcast #1",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "By several people",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.button
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(formatSeconds(position), style = MaterialTheme.typography.caption)
                        Text(
                            formatSeconds(viewModel.length),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
                Seeker(
                    value = position,
                    onValueChange = viewModel::setPosition,
                    onValueChangeFinished = viewModel::onPositionChangeFinished,
                    range = range,
                    readAheadValue = readAheadPosition,
                    state = seekerState,
                    interactionSource = interactionSource,
                    segments = viewModel.segments,
                    colors = SeekerDefaults.seekerColors(
                        trackColor = Color(0xFF22233b),
                        readAheadColor = Color(0xFF484A74),
                        progressColor = Color.White,
                        thumbColor = Color.White
                    ),
                    dimensions = SeekerDefaults.seekerDimensions(
                        thumbRadius = thumbRadius,
                        gap = gap
                    )
                )
                CurrentSegment(
                    currentSegment = seekerState.currentSegment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colors.surface.copy(0.03f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                )
            }
            Controls(
                isPlaying = isPlaying,
                onPlayPause = { viewModel.playOrPause() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 32.dp),
            )
        }
    }
}

@Composable
fun Controls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.round_skip_previous_24), contentDescription = "")
        }
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .requiredSize(72.dp)
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24
                ),
                contentDescription = "",
                tint = Color.Black
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.round_skip_next_24), contentDescription = "")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CurrentSegment(
    currentSegment: Segment,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = currentSegment,
            transitionSpec = {
                if (targetState.start > initialState.start) {
                    slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "segment",
        ) { currentSegment ->
            Row {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    Text(
                        text = formatSeconds(currentSegment.start),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.button
                    )
                }
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = " ${currentSegment.name}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.round_keyboard_arrow_down_24),
                    contentDescription = ""
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_more_vert_24),
                    contentDescription = ""
                )
            }
        },
        title = {
            Text(
                text = "Now playing",
                fontWeight = FontWeight.Bold
            )
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

fun formatSeconds(seconds: Float): String {
    val minutes = (seconds / 60).toInt()
    val remaining = (seconds % 60).toInt()
    return "$minutes:$remaining"
}

@Preview(showBackground = true)
@Composable
fun PreView() {
    SeekerTheme {
        NowPlayingScreen()
    }
}
