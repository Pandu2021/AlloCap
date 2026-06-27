package com.pandu.allocap.ui.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.components.SwipeToUnlockSlider
import com.pandu.allocap.ui.theme.*

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
                    colors = listOf(PremiumNavy, PremiumTeal, PremiumEmerald)
                )
            )
    ) {
        // Decorative background elements for "Premium" feel
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricMint.copy(alpha = 0.07f), Color.Transparent),
                    center = center.copy(y = center.y * 1.5f),
                    radius = size.width
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.8f))

            // Cartoon Animation (Premium & Cute)
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1000, 200)) + scaleIn(initialScale = 0.8f)
            ) {
                PremiumCartoonAnimation()
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(initialOffsetY = { -20 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Professional Label
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricMint.copy(alpha = 0.8f),
                        letterSpacing = 6.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Branding
                    Text(
                        text = "AlloCap",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 64.sp,
                            letterSpacing = (-2).sp,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = androidx.compose.ui.geometry.Offset(4f, 4f),
                                blurRadius = 8f
                            )
                        ),
                        softWrap = false,
                        maxLines = 1,
                        fontWeight = FontWeight.Black,
                        color = Champagne
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tagline
                    Text(
                        text = "Intelligent Capital Management.\nPrivate. Secure. Fully Offline.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp,
                            letterSpacing = 0.5.sp,
                            color = Champagne.copy(alpha = 0.7f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1.2f))

            // Swipe Slider
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1200, delayMillis = 600)) + slideInVertically(initialOffsetY = { 60 })
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
                color = ElectricMint.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun PremiumCartoonAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "cartoon")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .graphicsLayer {
                translationY = bounce
                rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        // Glow behind the cartoon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(ElectricMint.copy(alpha = 0.2f), CircleShape)
        )
        
        // Cartoonish Wallet Icon with premium look
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Rounded.AccountBalanceWallet,
                contentDescription = null,
                tint = GoldenSun,
                modifier = Modifier
                    .size(80.dp)
                    .shadow(16.dp, CircleShape, spotColor = GoldenSun)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Animated "Sparkles"
            Row {
                repeat(3) { index ->
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "sparkle"
                    )
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Rounded.Star,
                        contentDescription = null,
                        tint = ElectricMint,
                        modifier = Modifier
                            .size(12.dp)
                            .alpha(alpha)
                    )
                }
            }
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
                        colors = listOf(PremiumNavy, PremiumTeal, PremiumEmerald)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.8f))

                PremiumCartoonAnimation()

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SECURE ACCESS",
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricMint.copy(alpha = 0.8f),
                        letterSpacing = 6.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "AlloCap",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 64.sp,
                            letterSpacing = (-2).sp
                        ),
                        softWrap = false,
                        maxLines = 1,
                        fontWeight = FontWeight.Black,
                        color = Champagne
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Intelligent Capital Management.\nPrivate. Secure. Fully Offline.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp,
                            letterSpacing = 0.5.sp,
                            color = Champagne.copy(alpha = 0.7f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(1.2f))

                SwipeToUnlockSlider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Slide to authorize access",
                    onUnlocked = {}
                )
                
                Text(
                    text = "Biometric encryption active",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricMint.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
