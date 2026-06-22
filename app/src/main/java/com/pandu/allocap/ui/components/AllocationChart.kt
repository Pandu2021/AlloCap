package com.pandu.allocap.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.WarmTerracotta
import com.pandu.allocap.ui.theme.PaleSageMint
import com.pandu.allocap.ui.theme.ColorNeeds
import com.pandu.allocap.ui.theme.ColorWants
import com.pandu.allocap.ui.theme.ColorSavings
import androidx.compose.ui.graphics.Color

@Composable
fun AllocationChart(
    needsPercent: Float,
    wantsPercent: Float,
    savingsPercent: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(needsPercent, wantsPercent, savingsPercent) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 16.dp.toPx()
            
            // Total 360 degrees
            val total = 360f * animatedProgress.value
            
            // Draw background track
            drawCircle(
                color = DeepSpruce.copy(alpha = 0.05f),
                style = Stroke(width = strokeWidth)
            )

            if (needsPercent + wantsPercent + savingsPercent > 0) {
                val totalPercent = (needsPercent + wantsPercent + savingsPercent).coerceAtLeast(1f)
                val scale = 1f / totalPercent
                
                val startAngle = -90f
                val sweepNeeds = total * needsPercent * scale
                val sweepWants = total * wantsPercent * scale
                val sweepSavings = total * savingsPercent * scale

                // Needs
                drawArc(
                    color = ColorNeeds,
                    startAngle = startAngle,
                    sweepAngle = sweepNeeds,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Wants
                drawArc(
                    color = ColorWants,
                    startAngle = startAngle + sweepNeeds,
                    sweepAngle = sweepWants,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Savings
                drawArc(
                    color = ColorSavings,
                    startAngle = startAngle + sweepNeeds + sweepWants,
                    sweepAngle = sweepSavings,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
    }
}
