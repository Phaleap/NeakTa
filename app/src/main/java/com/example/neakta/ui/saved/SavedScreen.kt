package com.example.neakta.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import com.example.neakta.ui.home.ElectricBlue
import com.example.neakta.ui.home.LimePop
import com.example.neakta.ui.home.PinCard
import com.example.neakta.ui.home.SoftOutline

private val InkText    = Color(0xFFF7FAFC)
private val MutedText  = Color(0xFFB8C2CC)
private val DeepIndigo = Color(0xFF0D1117)
private val CardStart  = Color(0xE61A202C)
private val CardEnd    = Color(0xCC111827)
private val GlassPanel = Color(0x991A202C)

@Composable
fun SavedScreen(
    onPinClick: (PinCard) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val viewModel: SavedViewModel = viewModel(factory = SavedViewModel.Factory(session))
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchSavedPins()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1117), Color(0xFF111827), Color(0xFF0D1117), DeepIndigo)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text  = "SAVED",
                        style = TextStyle(
                            fontFamily    = FontFamily.SansSerif,
                            fontSize      = 24.sp,
                            letterSpacing = 5.sp,
                            fontWeight    = FontWeight.Black,
                            color         = InkText
                        )
                    )
                    Text(
                        text  = "Your collected gems",
                        style = TextStyle(
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color      = MutedText
                        )
                    )
                }
                Surface(
                    shape  = CircleShape,
                    color  = GlassPanel,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftOutline)
                ) {
                    IconButton(onClick = onNavigateToProfile, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = InkText, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // ── Content ──────────────────────────────────────────
            when (val s = state) {
                is SavedState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LimePop)
                    }
                }

                is SavedState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text  = "Failed to load saved pins 😕",
                            style = TextStyle(color = MutedText, fontSize = 14.sp)
                        )
                    }
                }

                is SavedState.Success -> {
                    if (s.pins.isEmpty()) {
                        // ── Empty State ───────────────────────────
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "🔖", fontSize = 48.sp)
                                Text(
                                    text  = "No saved gems yet",
                                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkText)
                                )
                                Text(
                                    text  = "Tap the bookmark on any pin to save it",
                                    style = TextStyle(fontSize = 13.sp, color = MutedText)
                                )
                            }
                        }
                    } else {
                        // ── Pin List ──────────────────────────────
                        LazyColumn(
                            contentPadding      = PaddingValues(bottom = 24.dp, top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Text(
                                    text     = "${s.pins.size} gems saved",
                                    style    = TextStyle(
                                        fontSize   = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.2.sp,
                                        color      = ElectricBlue
                                    ),
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                            items(s.pins) { pin ->
                                SavedPinCard(pin = pin, onClick = { onPinClick(pin) })
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
}

@Composable
private fun SavedPinCard(pin: PinCard, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(CardStart, CardEnd)))
            .border(1.dp, SoftOutline, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardStart)
        ) {
            AsyncImage(
                model            = pin.imageUrl.ifBlank { null },
                contentDescription = null,
                contentScale     = ContentScale.Crop,
                modifier         = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(GlassPanel, RoundedCornerShape(999.dp))
                    .border(1.dp, SoftOutline, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text  = pin.category.uppercase(),
                    style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = InkText)
                )
            }
        }

        // Info
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.LocationOn, null, tint = ElectricBlue, modifier = Modifier.size(12.dp))
                Text(
                    text  = pin.province,
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ElectricBlue)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text  = pin.timeAgo,
                    style = TextStyle(fontSize = 11.sp, color = MutedText)
                )
            }
            Text(
                text     = pin.title,
                style    = TextStyle(fontSize = 20.sp, lineHeight = 22.sp, fontWeight = FontWeight.Black, color = InkText),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text     = pin.story,
                style    = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, color = MutedText),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.BookmarkRemove, null, tint = LimePop, modifier = Modifier.size(14.dp))
                Text(
                    text  = "${pin.votes} saves",
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = LimePop)
                )
            }
        }
    }
}