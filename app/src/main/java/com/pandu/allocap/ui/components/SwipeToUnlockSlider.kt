package com.pandu.allocap.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.WarmTerracotta
import com.pandu.allocap.ui.theme.WarmTerracottaDark
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeToUnlockSlider(
    modifier: Modifier = Modifier,
    text: String = "Slide to Unlock Access",
    onUnlocked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var trackWidth by remember { mutableStateOf(0) }
    val thumbSize = 56.dp
    val thumbSizePx = with(LocalDensity.current) { thumbSize.toPx() }
    
    // Use Animatable for smooth, controlled motion
    val thumbOffset = remember { Animatable(0f) }
    val maxOffset = if (trackWidth > 0) trackWidth - thumbSizePx else 0f

    // Pulsing animation for the thumb to invite interaction
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Calculate alpha for the hint text based on swipe progress
    val textAlpha = (1f - (thumbOffset.value / (maxOffset.takeIf { it > 0 } ?: 1f))).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        DeepSpruce.copy(alpha = 0.05f),
                        DeepSpruce.copy(alpha = 0.12f)
                    )
                )
            )
            .onGloballyPositioned { trackWidth = it.size.width },
        contentAlignment = Alignment.CenterStart
    ) {
        // Track Hint Text
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(textAlpha),
            color = DeepSpruce.copy(alpha = 0.6f),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )

        // The Sliding Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.value.roundToInt(), 0) }
                .size(thumbSize)
                .padding(4.dp)
                .scale(pulseScale)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = DeepSpruce,
                    spotColor = WarmTerracotta
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(WarmTerracotta, WarmTerracottaDark)
                    )
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            thumbOffset.snapTo((thumbOffset.value + delta).coerceIn(0f, maxOffset))
                        }
                    },
                    onDragStopped = {
                        if (thumbOffset.value >= maxOffset * 0.85f) {
                            scope.launch {
                                thumbOffset.animateTo(
                                    targetValue = maxOffset,
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                )
                                onUnlocked()
                            }
                        } else {
                            scope.launch {
                                thumbOffset.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
