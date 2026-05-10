package com.example.neakta.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.auth.CormorantGaramond
import com.example.neakta.ui.auth.GemGold
import com.example.neakta.ui.auth.GemGoldDim
import com.example.neakta.ui.auth.NightBase
import com.example.neakta.ui.auth.TextPrimary
import com.example.neakta.ui.auth.TextSecondary
import com.example.neakta.ui.auth.BorderGold
import com.example.neakta.ui.auth.RuleColor

// ─── Mock Data ───────────────────────────────────────────────
data class PinCard(
    val id: Int,
    val title: String,
    val province: String,
    val category: String,
    val votes: Int,
    val story: String,
    val imageUrl: String,
    val author: String,
    val timeAgo: String
)

val trendingPins = listOf(
    PinCard(1, "បាយហាំ លោក តា ចាន់", "Siem Reap", "Food", 128,
        "A humble stall that has served the same recipe for 40 years, hidden behind the old market where only locals know to look.",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Bai_Sach_Chrouk.jpg/640px-Bai_Sach_Chrouk.jpg",
        "Sophea", "2h ago"),
    PinCard(2, "វត្តភ្នំជីសូរ", "Kampot", "Pagoda", 94,
        "Perched on a limestone hill, this pagoda offers a breathtaking view at sunrise that no tourist guide has ever written about.",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/Phnom_Chhngok_cave_temple.jpg/640px-Phnom_Chhngok_cave_temple.jpg",
        "Dara", "5h ago"),
    PinCard(3, "ផ្សារចាស់ក្រោម", "Phnom Penh", "Market", 76,
        "The oldest surviving wet market in the capital, where grandmothers still sell hand-woven silk passed down through generations.",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Phsar_Thmei_Central_Market_Phnom_Penh.jpg/640px-Phsar_Thmei_Central_Market_Phnom_Penh.jpg",
        "Maly", "1d ago"),
)

val recentPins = listOf(
    PinCard(4, "ជលប្រទានជ្រោយចង្វារ", "Kandal", "Nature", 43,
        "A hidden riverside spot where fishermen gather at dawn. The mist over the Mekong here is unlike anything you have seen.",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Mekong_River_Laos.jpg/640px-Mekong_River_Laos.jpg",
        "Virak", "3h ago"),
    PinCard(5, "កុដិព្រះសង្ឃចាស់", "Battambang", "Pagoda", 61,
        "A crumbling monk's sanctuary hidden in bamboo forest. The carvings on the walls date back to the French colonial era.",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/58/Battambang_temple.jpg/640px-Battambang_temple.jpg",
        "Chanthy", "6h ago"),
    PinCard(6, "ឈ្មួញកាត់សូត្រ លោកម៉ែ ស៊ីម", "Siem Reap", "Craft", 88,
        "One of the last remaining silk weavers using traditional hand looms. She weaves stories into every thread.",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Silk_weaving_Cambodia.jpg/640px-Silk_weaving_Cambodia.jpg",
        "Kosal", "12h ago"),
)

val provinceSpotlight = Triple("Battambang", 247, "The Bamboo Province")

// ─── Home Screen ─────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToExplore: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToRanks: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onPinClick: (PinCard) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(NightBase)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(NightBase, NightBase.copy(alpha = 0.95f))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "NEAKTA",
                        style = TextStyle(
                            fontFamily = Cinzel,
                            fontSize = 22.sp,
                            letterSpacing = 6.sp,
                            color = GemGold
                        )
                    )
                    Text(
                        text = "Cambodia's living archive",
                        style = TextStyle(
                            fontFamily = CormorantGaramond,
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = TextSecondary
                        )
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF1A1712), CircleShape)
                            .border(1.dp, BorderGold, CircleShape)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null,
                            tint = GemGold, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF1A1712), CircleShape)
                            .border(1.dp, BorderGold, CircleShape)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = GemGold, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // ── FEED ──────────────────────────────────────────
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                // Province spotlight
                item {
                    ProvinceSpotlightCard(
                        province = provinceSpotlight.first,
                        pinCount = provinceSpotlight.second,
                        tagline = provinceSpotlight.third
                    )
                }

                // Trending section
                item {
                    SectionHeader(title = "TRENDING THIS WEEK", subtitle = "Most upvoted gems")
                }
                item {
                    TrendingRow(pins = trendingPins, onPinClick = onPinClick)
                }

                // Divider
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .height(1.dp)
                            .background(RuleColor)
                    )
                }

                // Recent stories section
                item {
                    SectionHeader(title = "RECENT STORIES", subtitle = "Freshly pinned by the community")
                }
                items(recentPins) { pin ->
                    StoryCard(pin = pin, onPinClick = onPinClick)
                }
            }

            // ── BOTTOM NAV ────────────────────────────────────
            NeaktaBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToExplore = onNavigateToExplore,
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToRanks = onNavigateToRanks,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

// ─── Province Spotlight ──────────────────────────────────────
@Composable
fun ProvinceSpotlightCard(province: String, pinCount: Int, tagline: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1A1410), Color(0xFF221A0E))
                ),
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(GemGold.copy(alpha = 0.6f), GemGold.copy(alpha = 0.1f))
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROVINCE SPOTLIGHT",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 8.sp,
                        letterSpacing = 2.sp,
                        color = GemGoldDim
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = province,
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 20.sp,
                        letterSpacing = 2.sp,
                        color = GemGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = tagline,
                    style = TextStyle(
                        fontFamily = CormorantGaramond,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextSecondary
                    )
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$pinCount",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 32.sp,
                        color = GemGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "gems found",
                    style = TextStyle(
                        fontFamily = CormorantGaramond,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                )
            }
        }
    }
}

// ─── Section Header ──────────────────────────────────────────
@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Cinzel,
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = GemGold
            )
        )
        Text(
            text = subtitle,
            style = TextStyle(
                fontFamily = CormorantGaramond,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = TextSecondary
            )
        )
    }
}

// ─── Trending Row ────────────────────────────────────────────
@Composable
fun TrendingRow(pins: List<PinCard>, onPinClick: (PinCard) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        pins.forEach { pin ->
            TrendingCard(pin = pin, onClick = { onPinClick(pin) })
        }
    }
}

@Composable
fun TrendingCard(pin: PinCard, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        // Photo
        AsyncImage(
            model = pin.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xEE0C0A07))
                    )
                )
        )
        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null,
                    tint = GemGold, modifier = Modifier.size(14.dp))
                Text(
                    text = "+${pin.votes}",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 9.sp,
                        color = GemGold
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pin.title,
                style = TextStyle(
                    fontFamily = CormorantGaramond,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pin.province,
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 8.sp,
                    letterSpacing = 1.sp,
                    color = GemGoldDim
                )
            )
        }
        // Category badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .background(GemGold.copy(alpha = 0.15f), RoundedCornerShape(50))
                .border(1.dp, GemGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = pin.category,
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 7.sp,
                    letterSpacing = 1.sp,
                    color = GemGold
                )
            )
        }
    }
}

// ─── Story Card ──────────────────────────────────────────────
@Composable
fun StoryCard(pin: PinCard, onPinClick: (PinCard) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(Color(0xFF111009), RoundedCornerShape(16.dp))
            .border(1.dp, BorderGold, RoundedCornerShape(16.dp))
            .clickable { onPinClick(pin) }
    ) {
        // Photo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
            AsyncImage(
                model = pin.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Category badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .background(GemGold.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .border(1.dp, GemGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = pin.category.uppercase(),
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 7.sp,
                        letterSpacing = 1.sp,
                        color = GemGold
                    )
                )
            }
        }

        // Content
        Column(modifier = Modifier.padding(16.dp)) {
            // Province + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = GemGoldDim, modifier = Modifier.size(11.dp))
                    Text(
                        text = pin.province,
                        style = TextStyle(
                            fontFamily = Cinzel,
                            fontSize = 8.sp,
                            letterSpacing = 1.sp,
                            color = GemGoldDim
                        )
                    )
                }
                Text(
                    text = pin.timeAgo,
                    style = TextStyle(
                        fontFamily = CormorantGaramond,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = pin.title,
                style = TextStyle(
                    fontFamily = CormorantGaramond,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    lineHeight = 26.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Story excerpt
            Text(
                text = pin.story,
                style = TextStyle(
                    fontFamily = CormorantGaramond,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextSecondary,
                    lineHeight = 20.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(GemGold.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, GemGold.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = GemGold, modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = pin.author,
                        style = TextStyle(
                            fontFamily = CormorantGaramond,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    )
                }
                // Vote count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null,
                        tint = GemGold, modifier = Modifier.size(18.dp))
                    Text(
                        text = "+${pin.votes} gems",
                        style = TextStyle(
                            fontFamily = Cinzel,
                            fontSize = 9.sp,
                            color = GemGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

// ─── Bottom Nav ──────────────────────────────────────────────
@Composable
fun NeaktaBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToRanks: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NightBase)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, GemGold.copy(alpha = 0.3f), Color.Transparent)
                ),
                shape = RoundedCornerShape(0.dp)
            )
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(Icons.Default.Home, "Home", selectedTab == 0) {
            onTabSelected(0)
        }
        BottomNavItem(Icons.Default.Map, "Explore", selectedTab == 1) {
            onTabSelected(1); onNavigateToExplore()
        }
        // Add button
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(GemGold.copy(alpha = 0.9f), GemGold.copy(alpha = 0.6f))
                    ),
                    CircleShape
                )
                .border(1.dp, GemGold, CircleShape)
                .clickable { onNavigateToAdd() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add",
                tint = NightBase, modifier = Modifier.size(24.dp))
        }
        BottomNavItem(Icons.Default.Leaderboard, "Ranks", selectedTab == 3) {
            onTabSelected(3); onNavigateToRanks()
        }
        BottomNavItem(Icons.Default.Person, "Profile", selectedTab == 4) {
            onTabSelected(4); onNavigateToProfile()
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) GemGold else GemGold.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Cinzel,
                fontSize = 8.sp,
                letterSpacing = 1.sp,
                color = if (selected) GemGold else GemGold.copy(alpha = 0.4f)
            )
        )
    }
}