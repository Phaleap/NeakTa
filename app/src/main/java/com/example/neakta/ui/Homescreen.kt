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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// ─── Data ────────────────────────────────────────────────────
data class PinCard(
    val id: Int,
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
    val localDirections: String = "",
    val stillExistsPct: Int = 97,
    val yearDiscovered: String = "2024",
    val tags: List<String> = emptyList()
)

val trendingPins = listOf(
    PinCard(
        id = 1,
        title = "បាយហាំ លោក តា ចាន់",
        province = "Siem Reap",
        category = "Food",
        votes = 128,
        story = "A humble stall that has served the same recipe for 40 years, hidden behind the old market where only locals know to look.",
        imageUrl = "https://sovrinmagazine.com/assets/uploadeditor/photo/772390d4204fd2eccb36c60223bccd04.jpg",
        author = "Sophea",
        timeAgo = "2h ago",
        lat = 13.3671, lng = 103.8448,
        localDirections = "Behind the old market near Pub Street. Look for the blue plastic chairs — no signboard.",
        stillExistsPct = 94, yearDiscovered = "2021",
        tags = listOf("STREET FOOD", "BREAKFAST", "LOCALS ONLY")
    ),
    PinCard(
        id = 2,
        title = "វត្តភ្នំជីសូរ",
        province = "Kampot",
        category = "Pagoda",
        votes = 94,
        story = "Perched on a limestone hill, this pagoda offers a breathtaking view at sunrise that no tourist guide has ever written about.",
        imageUrl = "https://sovrinmagazine.com/assets/uploadeditor/photo/772390d4204fd2eccb36c60223bccd04.jpg",
        author = "Dara",
        timeAgo = "5h ago",
        lat = 10.6333, lng = 104.1833,
        localDirections = "Take the road past Kampot river bridge, turn right at the big tamarind tree. The path up is 20 minutes on foot.",
        stillExistsPct = 99, yearDiscovered = "2020",
        tags = listOf("PAGODA", "SUNRISE", "HIDDEN")
    ),
    PinCard(
        id = 3,
        title = "ផ្សារចាស់ក្រោម",
        province = "Phnom Penh",
        category = "Market",
        votes = 76,
        story = "The oldest surviving wet market in the capital, where grandmothers still sell hand-woven silk passed down through generations.",
        imageUrl = "https://sovrinmagazine.com/assets/uploadeditor/photo/772390d4204fd2eccb36c60223bccd04.jpg",
        author = "Maly",
        timeAgo = "1d ago",
        lat = 11.5564, lng = 104.9282,
        localDirections = "Near the riverside, south of Sisowath Quay. Enter through the narrow alley between the two pharmacies.",
        stillExistsPct = 88, yearDiscovered = "2019",
        tags = listOf("MARKET", "SILK", "HERITAGE")
    ),
)

val recentPins = listOf(
    PinCard(
        id = 4,
        title = "ជលប្រទានជ្រោយចង្វារ",
        province = "Kandal",
        category = "Nature",
        votes = 43,
        story = "A hidden riverside spot where fishermen gather at dawn. The mist over the Mekong here is unlike anything you have seen.",
        imageUrl = "https://kptmedia.ap-south-1.linodeobjects.com/uploads/2025/04/487925095_1071515321676124_4446736152088688747_n-768x1024.jpg",
        author = "Virak",
        timeAgo = "3h ago",
        lat = 11.2833, lng = 105.0167,
        localDirections = "Follow the dirt road past Chroy Changvar bridge for 3km. Ask the fishermen at the dock.",
        stillExistsPct = 91, yearDiscovered = "2023",
        tags = listOf("NATURE", "RIVER", "DAWN")
    ),
    PinCard(
        id = 5,
        title = "កុដិព្រះសង្ឃចាស់",
        province = "Battambang",
        category = "Pagoda",
        votes = 61,
        story = "A crumbling monk's sanctuary hidden in bamboo forest. The carvings on the walls date back to the French colonial era.",
        imageUrl = "https://kptmedia.ap-south-1.linodeobjects.com/uploads/2025/04/487925095_1071515321676124_4446736152088688747_n-768x1024.jpg",
        author = "Chanthy",
        timeAgo = "6h ago",
        lat = 13.1, lng = 103.2,
        localDirections = "2km west of Battambang train station. Enter the bamboo forest trail on the left side of Road 57.",
        stillExistsPct = 76, yearDiscovered = "2022",
        tags = listOf("PAGODA", "COLONIAL", "FOREST")
    ),
    PinCard(
        id = 6,
        title = "ឈ្មួញកាត់សូត្រ លោកម៉ែ ស៊ីម",
        province = "Siem Reap",
        category = "Craft",
        votes = 88,
        story = "One of the last remaining silk weavers using traditional hand looms. She weaves stories into every thread.",
        imageUrl = "https://kptmedia.ap-south-1.linodeobjects.com/uploads/2025/04/487925095_1071515321676124_4446736152088688747_n-768x1024.jpg",
        author = "Kosal",
        timeAgo = "12h ago",
        lat = 13.4, lng = 103.87,
        localDirections = "Village of Koh Dach, 30 min from Siem Reap. Ask anyone for 'Mae Sim the weaver' — everyone knows her.",
        stillExistsPct = 100, yearDiscovered = "2020",
        tags = listOf("CRAFT", "SILK", "TRADITION")
    ),
)

val provinceSpotlight = Triple("Battambang", 247, "The Bamboo Province")

// ─── Colors ──────────────────────────────────────────────────
private val InkText = Color(0xFFF7FAFC)
private val MutedText = Color(0xFFB8C2CC)
val ElectricBlue = Color(0xFF5FD3A6)
val Bubblegum = Color(0xFF5FD3A6)
val LimePop = Color(0xFF5FD3A6)
private val DeepIndigo = Color(0xFF0D1117)
private val CardStart = Color(0xE61A202C)
private val CardEnd = Color(0xCC111827)
val SoftOutline = Color(0x26FFFFFF)
private val GlassPanel = Color(0x991A202C)

// ─── HomeScreen ──────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit = {},
    onNavigateToRanks: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onPinClick: (PinCard) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // ✅ Category filter state
    var selectedCategory by remember { mutableStateOf("All") }

    // ✅ Filtered pins
    val filteredTrending = if (selectedCategory == "All")
        trendingPins
    else
        trendingPins.filter { it.category == selectedCategory }

    val filteredRecent = if (selectedCategory == "All")
        recentPins
    else
        recentPins.filter { it.category == selectedCategory }

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            LimePop.copy(alpha = 0.18f),
                            LimePop.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(LimePop.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(onNavigateToProfile = onNavigateToProfile)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    HeroSpotlightCard(
                        province = provinceSpotlight.first,
                        pinCount = provinceSpotlight.second,
                        tagline = provinceSpotlight.third,
                        onExploreClick = {}
                    )
                }

                // ✅ Category filter chips
                item {
                    CategoryFilterRow(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }

                item {
                    SectionHeader(
                        eyebrow = "TRENDING NOW",
                        title = "Spots everyone wants\non their feed",
                        subtitle = ""
                    )
                }

                item {
                    TrendingRow(pins = filteredTrending, onPinClick = onPinClick)
                }

                if (filteredTrending.isEmpty()) {
                    item { EmptyFilterState(category = selectedCategory) }
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = 1.dp,
                        color = SoftOutline
                    )
                }

                item {
                    SectionHeader(
                        eyebrow = "JUST DROPPED",
                        title = "Fresh finds with\nreal local energy",
                        subtitle = ""
                    )
                }

                items(filteredRecent) { pin ->
                    EditorialStoryCard(pin = pin, onPinClick = onPinClick)
                }

                if (filteredRecent.isEmpty()) {
                    item { EmptyFilterState(category = selectedCategory) }
                }

                item {
                    Spacer(
                        modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                    )
                }
            }

            FloatingBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToRanks = onNavigateToRanks,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

// ─── Category Filter Row ─────────────────────────────────────
@Composable
private fun CategoryFilterRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Food", "Pagoda", "Nature", "Market", "Craft")

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isSelected)
                            Brush.horizontalGradient(listOf(GlassPanel, GlassPanel))
                        else
                            Brush.horizontalGradient(
                                listOf(Color(0xFF191731), Color(0xFF191731))
                            )
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) LimePop.copy(alpha = 0.70f) else SoftOutline,
                        shape = RoundedCornerShape(999.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
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
}

// ─── Empty Filter State ──────────────────────────────────────
@Composable
private fun EmptyFilterState(category: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "🔍", fontSize = 32.sp)
        Text(
            text = "No $category gems yet",
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

// ─── Top Bar ─────────────────────────────────────────────────
@Composable
private fun HomeTopBar(onNavigateToProfile: () -> Unit) {
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
                text = "NEAKTA",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 24.sp,
                    letterSpacing = 5.sp,
                    color = InkText
                )
            )
            Text(
                text = "Cambodia, remixed for discovery",
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
}

// ─── Hero Spotlight Card ─────────────────────────────────────
@Composable
private fun HeroSpotlightCard(
    province: String,
    pinCount: Int,
    tagline: String,
    onExploreClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(CardStart, CardEnd)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        LimePop.copy(alpha = 0.55f),
                        LimePop.copy(alpha = 0.16f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CapsuleLabel(text = "HOT THIS WEEK")
                    Text(
                        text = province,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 31.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = InkText
                        )
                    )
                    Text(
                        text = tagline,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = GlassPanel,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = pinCount.toString(),
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = LimePop
                            )
                        )
                        Text(
                            text = "pins going up",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MutedText
                            )
                        )
                    }
                }
            }

            Surface(
                onClick = onExploreClick,
                shape = RoundedCornerShape(18.dp),
                color = GlassPanel,
                border = androidx.compose.foundation.BorderStroke(1.dp, LimePop.copy(alpha = 0.65f)),
                modifier = Modifier.width(156.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TravelExplore,
                        contentDescription = null,
                        tint = LimePop,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "See the hype",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LimePop
                        )
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
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = eyebrow,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.4.sp,
                color = ElectricBlue
            )
        )
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 30.sp,
                lineHeight = 33.sp,
                fontWeight = FontWeight.Black,
                color = InkText
            )
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MutedText
                )
            )
        }
    }
}

// ─── Trending Row ────────────────────────────────────────────
@Composable
private fun TrendingRow(pins: List<PinCard>, onPinClick: (PinCard) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        pins.forEachIndexed { index, pin ->
            TrendingCard(pin = pin, rank = index + 1, onClick = { onPinClick(pin) })
        }
    }
}

// ─── Trending Card ───────────────────────────────────────────
@Composable
private fun TrendingCard(pin: PinCard, rank: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(248.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .background(CardStart)
            .border(1.dp, SoftOutline, RoundedCornerShape(24.dp))
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
                            Color(0x55111827),
                            Color(0xCC0D1117),
                            Color(0xF00D1117)
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CapsuleLabel(text = "#$rank rising")
            CapsuleLabel(text = pin.category.uppercase())
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = pin.province,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = ElectricBlue
                    )
                )
            }
            Text(
                text = pin.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 23.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Black,
                    color = InkText
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pin.story,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    color = MutedText
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = LimePop,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${pin.votes} saves",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.4.sp,
                        color = LimePop
                    )
                )
            }
        }
    }
}

// ─── Editorial Story Card ────────────────────────────────────
@Composable
private fun EditorialStoryCard(pin: PinCard, onPinClick: (PinCard) -> Unit) {
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
        ) {
            AsyncImage(
                model = pin.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                CapsuleLabel(text = pin.category.uppercase())
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Bubblegum,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = pin.province,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.4.sp,
                            color = Bubblegum
                        )
                    )
                }
                Text(
                    text = pin.timeAgo,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText
                    )
                )
            }
            Text(
                text = pin.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = InkText
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pin.story,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MutedText
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${pin.author}",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = LimePop,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+${pin.votes}",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LimePop
                        )
                    )
                }
            }
        }
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
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp,
                color = InkText
            )
        )
    }
}

// ─── Bottom Nav ──────────────────────────────────────────────
@Composable
private fun FloatingBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToRanks: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(GlassPanel, GlassPanel)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        LimePop.copy(alpha = 0.65f),
                        LimePop.copy(alpha = 0.18f)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = selectedTab == 0
        ) { onTabSelected(0) }

        Surface(
            onClick = onNavigateToAdd,
            shape = CircleShape,
            color = GlassPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, LimePop.copy(alpha = 0.70f)),
            modifier = Modifier.size(58.dp),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Gem",
                    tint = LimePop,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        BottomNavItem(
            icon = Icons.Default.Leaderboard,
            label = "Ranks",
            selected = selectedTab == 2
        ) { onTabSelected(2); onNavigateToRanks() }

        BottomNavItem(
            icon = Icons.Default.Person,
            label = "Profile",
            selected = selectedTab == 3
        ) { onTabSelected(3); onNavigateToProfile() }
    }
}

// ─── Bottom Nav Item ─────────────────────────────────────────
@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (selected) Color.White.copy(alpha = 0.08f) else Color.Transparent,
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) LimePop else MutedText.copy(alpha = 0.65f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp,
                color = if (selected) LimePop else MutedText.copy(alpha = 0.65f)
            )
        )
    }
}
