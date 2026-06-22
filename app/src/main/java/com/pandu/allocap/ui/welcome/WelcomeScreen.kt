package com.pandu.allocap.ui.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.components.SwipeToUnlockSlider
import com.pandu.allocap.ui.theme.AlloCapTheme
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.SageGradientEnd
import com.pandu.allocap.ui.theme.SageGradientStart

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onUnlockSuccess: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    
    val greeting = if (userName.isNullOrBlank()) {
        "SECURE ACCESS"
    } else {
        "WELCOME BACK, ${userName?.uppercase()}"
    }

    // Animation for entry
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SageGradientStart, SageGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(initialOffsetY = { -20 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Professional Label
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.labelLarge,
                        color = DeepSpruce.copy(alpha = 0.6f),
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Branding
                    Text(
                        text = "AlloCap",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 60.sp,
                            letterSpacing = (-4).sp
                        ),
                        softWrap = false,
                        maxLines = 1,
                        fontWeight = FontWeight.Black,
                        color = DeepSpruce
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tagline
                    Text(
                        text = "Intelligent Capital Management.\nPrivate. Secure. Fully Offline.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp,
                            letterSpacing = 0.5.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = DeepSpruce.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1.5f))

            // Swipe Slider
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1200, delayMillis = 400)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                SwipeToUnlockSlider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Slide to authorize access",
                    onUnlocked = onUnlockSuccess
                )
            }
            
            Text(
                text = "Biometric encryption active",
                style = MaterialTheme.typography.labelSmall,
                color = DeepSpruce.copy(alpha = 0.4f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    AlloCapTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SageGradientStart, SageGradientEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SECURE ACCESS",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeepSpruce.copy(alpha = 0.6f),
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "AlloCap",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 60.sp,
                            letterSpacing = (-4).sp
                        ),
                        softWrap = false,
                        maxLines = 1,
                        fontWeight = FontWeight.Black,
                        color = DeepSpruce
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Intelligent Capital Management.\nPrivate. Secure. Fully Offline.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp,
                            letterSpacing = 0.5.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = DeepSpruce.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.weight(1.5f))

                SwipeToUnlockSlider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Slide to authorize access",
                    onUnlocked = {}
                )
                
                Text(
                    text = "Biometric encryption active",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepSpruce.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
