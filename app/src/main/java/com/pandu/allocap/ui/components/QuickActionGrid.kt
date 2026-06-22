package com.pandu.allocap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.WarmTerracotta

@Composable
fun QuickActionGrid(
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(
                title = "Add Transaction",
                icon = Icons.Outlined.AddCircleOutline,
                onClick = { onActionClick("add") },
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "Sandbox Mode",
                icon = Icons.Outlined.ModelTraining,
                onClick = { onActionClick("sandbox") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(
                title = "Envelope Status",
                icon = Icons.Outlined.Email,
                onClick = { onActionClick("envelope") },
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "Local Analytics",
                icon = Icons.Outlined.Analytics,
                onClick = { onActionClick("analytics") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(DeepSpruce.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = WarmTerracotta,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepSpruce
            )
        }
    }
}
