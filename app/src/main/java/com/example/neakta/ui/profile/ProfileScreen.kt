package com.example.neakta.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.neakta.data.SessionManager
import com.example.neakta.model.PinResponse
import com.example.neakta.model.UserResponse
import com.example.neakta.ui.auth.Cinzel

private val InkText    = Color(0xFFF7FAFC)
private val MutedText  = Color(0xFFB8C2CC)
private val NightBase  = Color(0xFF0D1117)
private val CardBg     = Color(0xE61A202C)
private val CardBgSoft = Color(0xCC111827)
private val Outline    = Color(0x26FFFFFF)
private val Primary    = Color(0xFF5FD3A6)
private val GlassPanel = Color(0x991A202C)

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(SessionManager(LocalContext.current))
    )
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1117), Color(0xFF111827), Color(0xFF0D1117), NightBase)
                )
            )
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Primary.copy(alpha = 0.18f), Primary.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            ProfileTopBar(onNavigateToSettings = onNavigateToSettings)

            when (state) {
                is ProfileState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is ProfileState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (state as ProfileState.Error).message,
                                style = TextStyle(color = MutedText, fontSize = 14.sp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.fetchProfile() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is ProfileState.Success -> {
                    val data = state as ProfileState.Success
                    LazyColumn(
                        modifier          = Modifier.weight(1f),
                        contentPadding    = PaddingValues(bottom = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item { ProfileSummaryCard(user = data.user) }
                        item { ProfileStatsRow(user = data.user) }
                        item { MyGemsHeader(count = data.pins.size) }
                        if (data.pins.isEmpty()) {
                            item { EmptyGemsState() }
                        } else {
                            items(data.pins) { pin -> GemRow(pin = pin) }
                        }
                        item {
                            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(onNavigateToSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text  = "PROFILE",
                style = TextStyle(fontFamily = Cinzel, fontSize = 24.sp, letterSpacing = 5.sp, color = InkText)
            )
            Text(
                text  = "Your saved gems and reputation",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MutedText)
            )
        }
        Surface(shape = CircleShape, color = GlassPanel, border = androidx.compose.foundation.BorderStroke(1.dp, Outline)) {
            IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = InkText, modifier = Modifier.size(19.dp))
            }
        }
    }
}

@Composable
private fun ProfileSummaryCard(user: UserResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(CardBg, CardBgSoft)))
            .border(1.dp, Outline, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        AsyncImage(
            model            = user.avatarUrl,
            contentDescription = null,
            contentScale     = ContentScale.Crop,
            modifier         = Modifier.size(72.dp).clip(CircleShape).border(1.dp, Primary, CircleShape)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    // display_name if set, otherwise fall back to username
                    text  = user.displayName?.takeIf { it.isNotBlank() } ?: user.username,
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = InkText)
                )
                Icon(Icons.Default.Star, null, tint = Primary, modifier = Modifier.size(15.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(13.dp))
                Text(
                    text  = "@${user.username}",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = MutedText),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            // Level badge based on total_pins
            val level = when {
                user.totalPins >= 20 -> "Gem Master Lv.5"
                user.totalPins >= 10 -> "Gem Hunter Lv.4"
                user.totalPins >= 5  -> "Explorer Lv.3"
                user.totalPins >= 2  -> "Scout Lv.2"
                else                 -> "Newcomer Lv.1"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Primary.copy(alpha = 0.16f))
                    .border(1.dp, Primary.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = level, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary))
            }
        }
    }
}

@Composable
private fun ProfileStatsRow(user: UserResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(CardBg)
            .border(1.dp, Outline, RoundedCornerShape(24.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatCell(value = user.totalPins.toString(), label = "Gems",   modifier = Modifier.weight(1f))
        VerticalStatDivider()
        StatCell(value = user.karmaPoints.toString(), label = "Karma", modifier = Modifier.weight(1f))
        VerticalStatDivider()
        // Member since — just the year from createdAt
        val year = user.createdAt?.take(4) ?: "—"
        StatCell(value = year, label = "Since", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(text = value, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp, fontWeight = FontWeight.Black, color = InkText))
        Text(text = label, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = MutedText))
    }
}

@Composable
private fun VerticalStatDivider() {
    Box(modifier = Modifier.width(1.dp).height(46.dp).background(Outline))
}

@Composable
private fun MyGemsHeader(count: Int) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(text = "My Gems", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, fontWeight = FontWeight.Black, color = InkText))
        Text(text = "$count total", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary))
    }
}

@Composable
private fun EmptyGemsState() {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "💎", fontSize = 32.sp)
        Text(text = "No gems yet", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkText))
        Text(text = "Drop your first hidden gem!", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = MutedText))
    }
}

@Composable
private fun GemRow(pin: PinResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBgSoft)
            .border(1.dp, Outline, RoundedCornerShape(22.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Placeholder box since imageUrl not available yet
        Box(
            modifier = Modifier
                .width(88.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Primary.copy(alpha = 0.08f))
                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = pin.categoryIcon ?: "📍",
                fontSize = 24.sp
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text     = pin.title,
                style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Black, color = InkText),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                text  = "${pin.provinceName ?: ""} · ${pin.categoryName ?: ""}",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = MutedText),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                text  = pin.createdAt?.take(10) ?: "",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, color = MutedText.copy(alpha = 0.5f))
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(Icons.Default.KeyboardArrowUp, null, tint = Primary, modifier = Modifier.size(15.dp))
            Text(
                text  = pin.upvoteCount.toString(),
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Primary)
            )
        }
    }
}