package com.example.neakta.ui.ranks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.home.*

// ─── Colors (from HomeScreen) ─────────────────────────────────
private val InkText    = Color(0xFFF7FAFC)
private val MutedText  = Color(0xFFB8C2CC)
private val DeepIndigo = Color(0xFF0D1117)
private val CardStart  = Color(0xE61A202C)
private val CardEnd    = Color(0xCC111827)
private val GlassPanel = Color(0x991A202C)

// ─── All pins ranked by votes ─────────────────────────────────
private val rankedPins: List<PinCard>
    get() = (trendingPins + recentPins).sortedByDescending { it.votes }

// ─── RanksScreen ─────────────────────────────────────────────
@Composable
fun RanksScreen(
    onNavigateHome: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onPinClick: (PinCard) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(3) }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Food", "Pagoda", "Nature", "Market", "Craft")

    val filtered = if (selectedCategory == "All") rankedPins
    else rankedPins.filter { it.category == selectedCategory }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1117),
                        Color(0xFF111827),
                        Color(0xFF0D1117),
                        DeepIndigo
                    )
                )
            )
    ) {
        // Subtle glow top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ElectricBlue.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────
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
                        text = "RANKS",
                        style = TextStyle(
                            fontFamily = Cinzel,
                            fontSize = 24.sp,
                            letterSpacing = 5.sp,
                            color = InkText
                        )
                    )
                    Text(
                        text = "Most saved gems in Cambodia",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText
                        )
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = GlassPanel,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftOutline)
                ) {
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = InkText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                // ── Podium (top 3) ────────────────────────────
                item {
                    PodiumSection(
                        pins = rankedPins.take(3),
                        onPinClick = onPinClick
                    )
                }

                // ── Section header ────────────────────────────
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "LEADERBOARD",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp,
                                color = ElectricBlue
                            )
                        )
                        Text(
                            text = "Every gem,\nranked by the crowd",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 28.sp,
                                lineHeight = 31.sp,
                                fontWeight = FontWeight.Black,
                                color = InkText
                            )
                        )
                    }
                }

                // ── Category filter ───────────────────────────
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        categories.forEach { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        if (isSelected) GlassPanel
                                        else Color(0xFF191731),
                                        RoundedCornerShape(999.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) LimePop.copy(alpha = 0.70f) else SoftOutline,
                                        shape = RoundedCornerShape(999.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        color = if (isSelected) LimePop else MutedText
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Ranked list ───────────────────────────────
                if (filtered.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🔍", fontSize = 32.sp)
                            Text(
                                text = "No $selectedCategory gems yet",
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InkText
                                )
                            )
                            Text(
                                text = "Be the first to pin one!",
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 13.sp,
                                    color = MutedText
                                )
                            )
                        }
                    }
                } else {
                    itemsIndexed(filtered) { index, pin ->
                        RankListRow(
                            pin = pin,
                            rank = rankedPins.indexOf(pin) + 1,  // global rank
                            onClick = { onPinClick(pin) }
                        )
                        if (index < filtered.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                thickness = 1.dp,
                                color = SoftOutline
                            )
                        }
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                    )
                }
            }
        }
    }
}

// ─── Podium Section ───────────────────────────────────────────
@Composable
private fun PodiumSection(pins: List<PinCard>, onPinClick: (PinCard) -> Unit) {
    if (pins.size < 3) return

    val gold   = Color(0xFFFFD700)
    val silver = Color(0xFFC0C0C0)
    val bronze = Color(0xFFCD7F32)

    val medalColors = listOf(gold, silver, bronze)
    val medals      = listOf("🥇", "🥈", "🥉")
    // Podium heights: #1 tallest in center
    val order = listOf(1, 0, 2) // silver | gold | bronze display order

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top 3 podium cards side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            order.forEach { i ->
                val pin   = pins[i]
                val color = medalColors[i]
                val medal = medals[i]
                val h     = when (i) { 0 -> 210.dp; 1 -> 175.dp; else -> 158.dp }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(h)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.dp,
                            color.copy(alpha = 0.55f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onPinClick(pin) }
                ) {
                    AsyncImage(
                        model = pin.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xBB0D1117)
                                    )
                                )
                            )
                    )
                    // Medal badge top-start
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(GlassPanel, RoundedCornerShape(999.dp))
                            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = medal,
                            fontSize = 12.sp
                        )
                    }
                    // Info bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = pin.title,
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = InkText
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${pin.votes} saves",
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = color
                                )
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(thickness = 1.dp, color = SoftOutline)
    }
}

// ─── Rank List Row ────────────────────────────────────────────
@Composable
private fun RankListRow(pin: PinCard, rank: Int, onClick: () -> Unit) {
    val rankColor = when (rank) {
        1    -> Color(0xFFFFD700)
        2    -> Color(0xFFC0C0C0)
        3    -> Color(0xFFCD7F32)
        else -> MutedText
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (rank <= 3) rankColor.copy(alpha = 0.15f) else GlassPanel
                )
                .border(
                    1.dp,
                    if (rank <= 3) rankColor.copy(alpha = 0.55f) else SoftOutline,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (rank <= 3) rankColor else MutedText
                )
            )
        }

        // Thumbnail
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(14.dp))
        ) {
            AsyncImage(
                model = pin.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = pin.province,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.4.sp,
                        color = ElectricBlue
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .background(GlassPanel, RoundedCornerShape(999.dp))
                        .border(1.dp, SoftOutline, RoundedCornerShape(999.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = pin.category.uppercase(),
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp,
                            color = MutedText
                        )
                    )
                }
            }
            Text(
                text = pin.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 17.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = InkText
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "By ${pin.author}",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = MutedText
                )
            )
        }

        // Votes pill
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = LimePop,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "${pin.votes}",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = LimePop
                )
            )
            Text(
                text = "saves",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp,
                    color = MutedText
                )
            )
        }
    }
}
