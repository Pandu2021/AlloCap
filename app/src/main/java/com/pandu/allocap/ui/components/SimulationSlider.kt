package com.pandu.allocap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.WarmTerracotta

@Composable
fun SimulationSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    safeRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    val isUnsafe = value !in safeRange
    val trackColor = if (isUnsafe) Color(0xFFE57373) else WarmTerracotta

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = DeepSpruce,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isUnsafe) Color(0xFFD32F2F) else DeepSpruce,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = WarmTerracotta,
                activeTrackColor = trackColor,
                inactiveTrackColor = DeepSpruce.copy(alpha = 0.1f)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        if (isUnsafe) {
            Text(
                text = if (value < safeRange.start) "Ratio too low" else "Ratio too high",
                color = Color(0xFFD32F2F),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
