package com.example.neakta.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.core.AppLanguage
import com.example.neakta.ui.core.LocalAppLanguage

// ─── Data ────────────────────────────────────────────────────
data class PinCard(
    val id: String,
    val title: String,
    val province: String,
    val category: String,
    val votes: Int,
    val story: String,
    val imageUrl: String,
    val author: String,
    val timeAgo: String,
    val lat: Double = 11.5564,
    val lng: Double = 104.9282,
    val tags: List<String> = emptyList(),
    val mediaUrls: List<String> = emptyList(),
    val localDirections: String = "",
    val stillExistsPct: Int = 97,
    val yearDiscovered: String = "2024"
)

val provinceSpotlight = Triple("Battambang", 247, "The Bamboo Province")

// ─── Colors ──────────────────────────────────────────────────
private val InkText    = Color(0xFFF7FAFC)
private val MutedText  = Color(0xFFB8C2CC)
val ElectricBlue       = Color(0xFF5FD3A6)
val Bubblegum          = Color(0xFF5FD3A6)
val LimePop            = Color(0xFF5FD3A6)
private val DeepIndigo = Color(0xFF0D1117)
private val CardStart  = Color(0xE61A202C)
private val CardEnd    = Color(0xCC111827)
val SoftOutline        = Color(0x26FFFFFF)
private val GlassPanel = Color(0x991A202C)

// ─── HomeScreen ──────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit = {},
    onPinClick: (PinCard) -> Unit = {},
    viewModel: PinViewModel
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val pinsState by viewModel.pinsState.collectAsState()

    val languageState = LocalAppLanguage.current
    val isKhmer = languageState.current == AppLanguage.KHMER

    var selectedCategory by remember { mutableStateOf("All") }

    val allPins = when (val state = pinsState) {
        is PinsState.Success -> state.pins.map { pin ->
            // FIXED - all fields wired
            PinCard(
                id              = pin.id,
                title           = pin.title,
                province        = pin.provinceName ?: "",
                category        = pin.categoryName ?: "",
                votes           = pin.upvoteCount,
                story           = pin.story,
                imageUrl        = pin.imageUrl ?: "",
                author          = pin.authorUsername ?: "",
                timeAgo         = pin.createdAt?.take(10) ?: "",
                lat             = pin.lat?.toDouble() ?: 11.5564,
                lng             = pin.lng?.toDouble() ?: 104.9282,
                localDirections = pin.localDirections ?: "",
                tags            = pin.tags ?: emptyList(),
                mediaUrls       = pin.mediaUrls ?: emptyList(),
                stillExistsPct  = if (pin.score > 0) pin.score.coerceIn(0, 100) else 97,
                yearDiscovered  = pin.createdAt?.take(4) ?: "2024"
            )
        }
        else -> emptyList()
    }

    val filteredPins = if (selectedCategory == "All") allPins
    else allPins.filter { it.category == selectedCategory }

    val trendingPins = filteredPins.sortedByDescending { it.votes }.take(5)
    val recentPins   = filteredPins.sortedByDescending { it.timeAgo }.take(10)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1117), Color(0xFF111827), Color(0xFF0D1117), DeepIndigo)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(
                    Brush.linearGradient(
                        listOf(LimePop.copy(alpha = 0.18f), LimePop.copy(alpha = 0.06f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(onNavigateToProfile = onNavigateToProfile, isKhmer = isKhmer)

            LazyColumn(
                modifier        = Modifier.weight(1f),
                contentPadding  = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    HeroSpotlightCard(
                        province       = provinceSpotlight.first,
                        pinCount       = allPins.size,
                        tagline        = provinceSpotlight.third,
                        onExploreClick = {},
                        isKhmer        = isKhmer
                    )
                }

                item {
                    CategoryFilterRow(
                        selectedCategory   = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        isKhmer            = isKhmer
                    )
                }

                when (pinsState) {
                    is PinsState.Loading -> item {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = LimePop)
                        }
                    }
                    is PinsState.Error -> item {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = if (isKhmer) "ផ្ទុក gems បរាជ័យ 😕" else "Failed to load gems 😕",
                                style = TextStyle(color = MutedText, fontSize = 14.sp)
                            )
                        }
                    }
                    is PinsState.Success -> {
                        item {
                            SectionHeader(
                                eyebrow  = if (isKhmer) "កំពុងពេញនិយម" else "TRENDING NOW",
                                title    = if (isKhmer) "កន្លែងដែលអ្នករាល់គ្នា\nចង់បាននៅលើ feed"
                                else "Spots everyone wants\non their feed",
                                subtitle = ""
                            )
                        }
                        item {
                            TrendingRow(pins = trendingPins, onPinClick = onPinClick, isKhmer = isKhmer)
                        }
                        if (trendingPins.isEmpty()) {
                            item { EmptyFilterState(category = selectedCategory, isKhmer = isKhmer) }
                        }
                        item {
                            HorizontalDivider(
                                modifier  = Modifier.padding(horizontal = 20.dp),
                                thickness = 1.dp,
                                color     = SoftOutline
                            )
                        }
                        item {
                            SectionHeader(
                                eyebrow  = if (isKhmer) "ថ្មីៗនេះ" else "JUST DROPPED",
                                title    = if (isKhmer) "រកឃើញថ្មី\nជាមួយថាមពលក្នុងស្រុក"
                                else "Fresh finds with\nreal local energy",
                                subtitle = ""
                            )
                        }
                        items(recentPins) { pin ->
                            EditorialStoryCard(pin = pin, onPinClick = onPinClick, isKhmer = isKhmer)
                        }
                        if (recentPins.isEmpty()) {
                            item { EmptyFilterState(category = selectedCategory, isKhmer = isKhmer) }
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

// ─── Top Bar ─────────────────────────────────────────────────
@Composable
private fun HomeTopBar(onNavigateToProfile: () -> Unit, isKhmer: Boolean) {
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
                text  = "NEAKTA",
                style = TextStyle(fontFamily = Cinzel, fontSize = 24.sp, letterSpacing = 5.sp, color = InkText)
            )
            Text(
                text  = if (isKhmer) "កម្ពុជា រៀបចំឡើងវិញសម្រាប់ការរុករក"
                else "Cambodia, remixed for discovery",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MutedText)
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
}

// ─── Category Filter Row ─────────────────────────────────────
@Composable
private fun CategoryFilterRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    isKhmer: Boolean
) {
    val categories   = listOf("All", "Food", "Pagoda", "Nature", "Market", "Craft")
    val categoriesKh = listOf("ទាំងអស់", "អាហារ", "វត្ត", "ធម្មជាតិ", "ផ្សារ", "សិប្បកម្ម")

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected   = selectedCategory == category
            val displayLabel = if (isKhmer) categoriesKh[index] else category
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isSelected)
                            Brush.horizontalGradient(listOf(GlassPanel, GlassPanel))
                        else
                            Brush.horizontalGradient(listOf(Color(0xFF191731), Color(0xFF191731)))
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) LimePop.copy(alpha = 0.70f) else SoftOutline,
                        shape = RoundedCornerShape(999.dp)
                    )
                    .clickable { onCategorySelected(category) } // always use English key
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text  = displayLabel,
                    style = TextStyle(
                        fontFamily  = FontFamily.SansSerif,
                        fontSize    = 11.sp,
                        fontWeight  = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color       = if (isSelected) LimePop else MutedText
                    )
                )
            }
        }
    }
}

// ─── Hero Spotlight Card ─────────────────────────────────────
@Composable
private fun HeroSpotlightCard(
    province: String,
    pinCount: Int,
    tagline: String,
    onExploreClick: () -> Unit,
    isKhmer: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(CardStart, CardEnd)))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(LimePop.copy(alpha = 0.55f), LimePop.copy(alpha = 0.16f), Color.Transparent)
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CapsuleLabel(text = if (isKhmer) "ពេញនិយមអាទិត្យនេះ" else "HOT THIS WEEK")
                    Text(
                        text  = province,
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 31.sp, lineHeight = 34.sp, fontWeight = FontWeight.Black, color = InkText)
                    )
                    Text(
                        text  = tagline,
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MutedText)
                    )
                }
                Surface(
                    shape  = RoundedCornerShape(24.dp),
                    color  = GlassPanel,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftOutline)
                ) {
                    Column(
                        modifier            = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text  = pinCount.toString(),
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 30.sp, fontWeight = FontWeight.Black, color = LimePop)
                        )
                        Text(
                            text  = if (isKhmer) "pins កំពុងឡើង" else "pins going up",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MutedText)
                        )
                    }
                }
            }
            Surface(
                onClick   = onExploreClick,
                shape     = RoundedCornerShape(18.dp),
                color     = GlassPanel,
                border    = androidx.compose.foundation.BorderStroke(1.dp, LimePop.copy(alpha = 0.65f)),
                modifier  = Modifier.width(if (isKhmer) 180.dp else 156.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.TravelExplore, contentDescription = null, tint = LimePop, modifier = Modifier.size(18.dp))
                    Text(
                        text  = if (isKhmer) "មើលភាពល្បី" else "See the hype",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = LimePop)
                    )
                }
            }
        }
    }
}

// ─── Section Header ──────────────────────────────────────────
@Composable
private fun SectionHeader(eyebrow: String, title: String, subtitle: String) {
    Column(
        modifier            = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text  = eyebrow,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp, color = ElectricBlue)
        )
        Text(
            text  = title,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 30.sp, lineHeight = 33.sp, fontWeight = FontWeight.Black, color = InkText)
        )
        if (subtitle.isNotBlank()) {
            Text(
                text  = subtitle,
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp, color = MutedText)
            )
        }
    }
}

// ─── Trending Row ────────────────────────────────────────────
@Composable
private fun TrendingRow(pins: List<PinCard>, onPinClick: (PinCard) -> Unit, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        pins.forEachIndexed { index, pin ->
            TrendingCard(pin = pin, rank = index + 1, onClick = { onPinClick(pin) }, isKhmer = isKhmer)
        }
    }
}

// ─── Trending Card ───────────────────────────────────────────
@Composable
private fun TrendingCard(pin: PinCard, rank: Int, onClick: () -> Unit, isKhmer: Boolean) {
    Box(
        modifier = Modifier
            .width(248.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .background(CardStart)
            .border(1.dp, SoftOutline, RoundedCornerShape(24.dp))
    ) {
        AsyncImage(model = pin.imageUrl.ifBlank { null }, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0x55111827), Color(0xCC0D1117), Color(0xF00D1117))
                    )
                )
        )
        Row(
            modifier              = Modifier.align(Alignment.TopStart).padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CapsuleLabel(text = if (isKhmer) "#$rank កំពុងឡើង" else "#$rank rising")
            CapsuleLabel(text = pin.category.uppercase())
        }
        Column(
            modifier            = Modifier.align(Alignment.BottomStart).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = ElectricBlue, modifier = Modifier.size(13.dp))
                Text(
                    text  = pin.province,
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp, color = ElectricBlue)
                )
            }
            Text(
                text     = pin.title,
                style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 23.sp, lineHeight = 25.sp, fontWeight = FontWeight.Black, color = InkText),
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Text(
                text     = pin.story,
                style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 19.sp, color = MutedText),
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.KeyboardArrowUp, null, tint = LimePop, modifier = Modifier.size(16.dp))
                Text(
                    text  = if (isKhmer) "${pin.votes} រក្សាទុក" else "${pin.votes} saves",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.4.sp, color = LimePop)
                )
            }
        }
    }
}

// ─── Editorial Story Card ────────────────────────────────────
@Composable
private fun EditorialStoryCard(pin: PinCard, onPinClick: (PinCard) -> Unit, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(CardStart, CardEnd)))
            .border(1.dp, SoftOutline, RoundedCornerShape(24.dp))
            .clickable { onPinClick(pin) }
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .width(118.dp)
                .height(158.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(CardStart)
        ) {
            AsyncImage(model = pin.imageUrl.ifBlank { null }, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                CapsuleLabel(text = pin.category.uppercase())
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Bubblegum, modifier = Modifier.size(13.dp))
                    Text(
                        text  = pin.province,
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.4.sp, color = Bubblegum)
                    )
                }
                Text(
                    text  = pin.timeAgo,
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MutedText)
                )
            }
            Text(
                text     = pin.title,
                style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp, lineHeight = 26.sp, fontWeight = FontWeight.Black, color = InkText),
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Text(
                text     = pin.story,
                style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp, color = MutedText),
                maxLines = 3, overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = if (isKhmer) "ដោយ ${pin.author}" else "By ${pin.author}",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MutedText)
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.KeyboardArrowUp, null, tint = LimePop, modifier = Modifier.size(16.dp))
                    Text(
                        text  = "+${pin.votes}",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = LimePop)
                    )
                }
            }
        }
    }
}

// ─── Empty Filter State ──────────────────────────────────────
@Composable
private fun EmptyFilterState(category: String, isKhmer: Boolean) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "🔍", fontSize = 32.sp)
        Text(
            text  = if (isKhmer) "មិនទាន់មាន gems សម្រាប់ $category នៅឡើយ"
            else "No $category gems yet",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkText)
        )
        Text(
            text  = if (isKhmer) "ក្លាយជាអ្នកដំបូងបន្ថែម!" else "Be the first to pin one!",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = MutedText)
        )
    }
}

// ─── Capsule Label ───────────────────────────────────────────
@Composable
private fun CapsuleLabel(text: String) {
    Box(
        modifier = Modifier
            .background(GlassPanel, RoundedCornerShape(999.dp))
            .border(1.dp, SoftOutline, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text  = text,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.3.sp, color = InkText)
        )
    }
}