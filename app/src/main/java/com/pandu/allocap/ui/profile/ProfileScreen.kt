package com.pandu.allocap.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pandu.allocap.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val userBio by viewModel.userBio.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val selectedAvatarIndex by viewModel.selectedAvatarIndex.collectAsState()

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateProfileImage(it.toString()) }
    }

    val avatars = listOf(
        Icons.Rounded.Face,
        Icons.Rounded.AccountCircle,
        Icons.Rounded.ChildCare,
        Icons.Rounded.EmojiEmotions,
        Icons.Rounded.CrueltyFree
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = Champagne) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Champagne)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PremiumNavy)
            )
        },
        containerColor = PremiumNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(24.dp, CircleShape, spotColor = ElectricMint)
                    .border(4.dp, Brush.linearGradient(listOf(ElectricMint, GoldenSun)), CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(PremiumTeal)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = avatars[selectedAvatarIndex],
                        contentDescription = "Default Avatar",
                        modifier = Modifier.size(100.dp),
                        tint = ElectricMint
                    )
                }
                
                // Edit Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Icon(
                        Icons.Rounded.PhotoCamera,
                        contentDescription = "Edit",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp).size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Info
            Text(
                text = userName ?: "User Name",
                style = MaterialTheme.typography.headlineLarge,
                color = Champagne,
                fontWeight = FontWeight.Black
            )
            
            Text(
                text = userBio ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Champagne.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Avatar Selection
            Text(
                text = "Premium Avatars",
                style = MaterialTheme.typography.labelLarge,
                color = ElectricMint,
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(avatars) { index, icon ->
                    val isSelected = selectedAvatarIndex == index && profileImageUri == null
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ElectricMint.copy(alpha = 0.2f) else PremiumTeal.copy(alpha = 0.4f))
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) ElectricMint else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { 
                                viewModel.updateAvatar(index)
                                viewModel.updateProfileImage(null)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Avatar $index",
                            tint = if (isSelected) ElectricMint else Champagne.copy(alpha = 0.4f),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Stats or Menu Items
            ProfileMenuItem(title = "Account Security", icon = Icons.Rounded.Security)
            ProfileMenuItem(title = "App Settings", icon = Icons.Rounded.Settings)
            ProfileMenuItem(title = "Help & Support", icon = Icons.Rounded.HelpOutline)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Logout or Reset */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Logout Securely", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(title: String, icon: ImageVector) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { },
        color = PremiumTeal.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = ElectricMint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Champagne, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Champagne.copy(alpha = 0.3f))
        }
    }
}
