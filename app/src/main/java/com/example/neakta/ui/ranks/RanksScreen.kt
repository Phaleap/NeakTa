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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.core.AppLanguage
import com.example.neakta.ui.core.LocalAppLanguage
import com.example.neakta.ui.home.ElectricBlue
import com.example.neakta.ui.home.LimePop
import com.example.neakta.ui.home.PinCard
import com.example.neakta.ui.home.PinViewModel
import com.example.neakta.ui.home.PinsState
import com.example.neakta.ui.home.SoftOutline

private val InkText    = Color(0xFFF7FAFC)
private val MutedText  = Color(0xFFB8C2CC)
private val DeepIndigo = Color(0xFF0D1117)
private val CardStart  = Color(0xE61A202C)
private val CardEnd    = Color(0xCC111827)
private val GlassPanel = Color(0x991A202C)

@Composable
fun RanksScreen(
    onNavigateToProfile: () -> Unit = {},
    onPinClick: (PinCard) -> Unit = {},
    viewModel: PinViewModel
) {
    val pinsState by viewModel.pinsState.collectAsState()
    val languageState = LocalAppLanguage.current
    val isKhmer = languageState.current == AppLanguage.KHMER

    var selectedCategory by remember { mutableStateOf("All") }
    val categories   = listOf("All", "Food", "Pagoda", "Nature", "Market", "Craft")
    val categoriesKh = listOf("ទាំងអស់", "អាហារ", "វត្ត", "ធម្មជាតិ", "ផ្សារ", "សិប្បកម្ម")

    val allPins = when (val state = pinsState) {
        is PinsState.Success -> state.pins.map { pin ->
            PinCard(
                id       = pin.id,
                title    = pin.title,
                province = pin.provinceName ?: "",
                category = pin.categoryName ?: "",
                votes    = pin.upvoteCount,
                story    = pin.story,
                imageUrl = pin.imageUrl ?: "",
                author   = pin.authorUsername ?: "",
                timeAgo  = pin.createdAt?.take(10) ?: "",
                lat      = pin.lat?.toDouble() ?: 11.5564,
                lng      = pin.lng?.toDouble() ?: 104.9282
            )
        }.sortedByDescending { it.votes }
        else -> emptyList()
    }

    val filtered = if (selectedCategory == "All") allPins
    else allPins.filter { it.category == selectedCategory }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF111827), Color(0xFF0D1117), DeepIndigo)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Brush.radialGradient(colors = listOf(ElectricBlue.copy(alpha = 0.10f), Color.Transparent)))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────
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
                        text  = if (isKhmer) "ចំណាត់ថ្នាក់" else "RANKS",
                        style = TextStyle(fontFamily = Cinzel, fontSize = 24.sp, letterSpacing = 5.sp, color = InkText)
                    )
                    Text(
                        text  = if (isKhmer) "Gems ដែលបានរក្សាទុកច្រើនបំផុតនៅកម្ពុជា"
                        else "Most saved gems in Cambodia",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MutedText)
                    )
                }
                Surface(shape = CircleShape, color = GlassPanel, border = androidx.compose.foundation.BorderStroke(1.dp, SoftOutline)) {
                    IconButton(onClick = onNavigateToProfile, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.Default.Person, "Profile", tint = InkText, modifier = Modifier.size(18.dp))
                    }
                }
            }

            LazyColumn(
                modifier        = Modifier.weight(1f),
                contentPadding  = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                when (pinsState) {
                    is PinsState.Loading -> item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = LimePop)
                        }
                    }
                    is PinsState.Error -> item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text  = if (isKhmer) "ផ្ទុកចំណាត់ថ្នាក់បរាជ័យ 😕" else "Failed to load ranks 😕",
                                style = TextStyle(color = MutedText, fontSize = 14.sp)
                            )
                        }
                    }
                    is PinsState.Success -> {
                        // ── Podium ────────────────────────────
                        if (allPins.size >= 3) {
                            item {
                                PodiumSection(pins = allPins.take(3), onPinClick = onPinClick, isKhmer = isKhmer)
                            }
                        }

                        // ── Section header ────────────────────
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text  = if (isKhmer) "តារាងចំណាត់ថ្នាក់" else "LEADERBOARD",
                                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp, color = ElectricBlue)
                                )
                                Text(
                                    text  = if (isKhmer) "Gems ទាំងអស់\nចំណាត់ថ្នាក់ដោយមហាជន" else "Every gem,\nranked by the crowd",
                                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 28.sp, lineHeight = 31.sp, fontWeight = FontWeight.Black, color = InkText)
                                )
                            }
                        }

                        // ── Category filter ───────────────────
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                categories.forEachIndexed { index, cat ->
                                    val isSelected   = cat == selectedCategory
                                    val displayLabel = if (isKhmer) categoriesKh[index] else cat
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(if (isSelected) GlassPanel else Color(0xFF191731), RoundedCornerShape(999.dp))
                                            .border(1.dp, if (isSelected) LimePop.copy(alpha = 0.70f) else SoftOutline, RoundedCornerShape(999.dp))
                                            .clickable { selectedCategory = cat }
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text  = displayLabel,
                                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp, color = if (isSelected) LimePop else MutedText)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // ── Ranked list ───────────────────────
                        if (filtered.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🔍", fontSize = 32.sp)
                                    Text(
                                        text  = if (isKhmer) "មិនទាន់មាន gems សម្រាប់ $selectedCategory នៅឡើយ"
                                        else "No $selectedCategory gems yet",
                                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkText)
                                    )
                                    Text(
                                        text  = if (isKhmer) "ក្លាយជាអ្នកដំបូងបន្ថែម!" else "Be the first to pin one!",
                                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = MutedText)
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(filtered) { index, pin ->
                                RankListRow(pin = pin, rank = allPins.indexOf(pin) + 1, onClick = { onPinClick(pin) }, isKhmer = isKhmer)
                                if (index < filtered.lastIndex) {
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 1.dp, color = SoftOutline)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    }
}

// ─── Podium Section ───────────────────────────────────────────
@Composable
private fun PodiumSection(pins: List<PinCard>, onPinClick: (PinCard) -> Unit, isKhmer: Boolean) {
    if (pins.size < 3) return

    val gold        = Color(0xFFFFD700)
    val silver      = Color(0xFFC0C0C0)
    val bronze      = Color(0xFFCD7F32)
    val medalColors = listOf(gold, silver, bronze)
    val medals      = listOf("🥇", "🥈", "🥉")
    val order       = listOf(1, 0, 2)

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.Bottom
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
                        .border(1.dp, color.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
                        .clickable { onPinClick(pin) }
                ) {
                    AsyncImage(model = pin.imageUrl.ifBlank { null }, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xBB0D1117)))))
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(GlassPanel, RoundedCornerShape(999.dp))
                            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = medal, fontSize = 12.sp)
                    }
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = pin.title, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, lineHeight = 15.sp, fontWeight = FontWeight.Black, color = InkText), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.KeyboardArrowUp, null, tint = color, modifier = Modifier.size(13.dp))
                            Text(
                                text  = if (isKhmer) "${pin.votes} រក្សាទុក" else "${pin.votes} saves",
                                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = color)
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
private fun RankListRow(pin: PinCard, rank: Int, onClick: () -> Unit, isKhmer: Boolean) {
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
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (rank <= 3) rankColor.copy(alpha = 0.15f) else GlassPanel)
                .border(1.dp, if (rank <= 3) rankColor.copy(alpha = 0.55f) else SoftOutline, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "#$rank", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (rank <= 3) rankColor else MutedText))
        }

        Box(modifier = Modifier.size(58.dp).clip(RoundedCornerShape(14.dp))) {
            AsyncImage(model = pin.imageUrl.ifBlank { null }, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = ElectricBlue, modifier = Modifier.size(11.dp))
                Text(text = pin.province, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.4.sp, color = ElectricBlue))
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .background(GlassPanel, RoundedCornerShape(999.dp))
                        .border(1.dp, SoftOutline, RoundedCornerShape(999.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = pin.category.uppercase(), style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.3.sp, color = MutedText))
                }
            }
            Text(text = pin.title, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 17.sp, lineHeight = 19.sp, fontWeight = FontWeight.Black, color = InkText), maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                text  = if (isKhmer) "ដោយ ${pin.author}" else "By ${pin.author}",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = MutedText)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Icon(Icons.Default.KeyboardArrowUp, null, tint = LimePop, modifier = Modifier.size(18.dp))
            Text(text = "${pin.votes}", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Black, color = LimePop))
            Text(
                text  = if (isKhmer) "រក្សាទុក" else "saves",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.3.sp, color = MutedText)
            )
        }
    }
}