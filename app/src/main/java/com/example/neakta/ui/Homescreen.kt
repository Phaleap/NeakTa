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
import androidx.compose.material.icons.filled.Explore
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
    PinCard(
        1,
        "áž”áž¶áž™áž áž¶áŸ† áž›áŸ„áž€ ážáž¶ áž…áž¶áž“áŸ‹",
        "Siem Reap",
        "Food",
        128,
        "A humble stall that has served the same recipe for 40 years, hidden behind the old market where only locals know to look.",
        "https://sovrinmagazine.com/assets/uploadeditor/photo/772390d4204fd2eccb36c60223bccd04.jpg",
        "Sophea",
        "2h ago"
    ),
    PinCard(
        2,
        "ážœážáŸ’ážáž—áŸ’áž“áŸ†áž‡áž¸ážŸáž¼ážš",
        "Kampot",
        "Pagoda",
        94,
        "Perched on a limestone hill, this pagoda offers a breathtaking view at sunrise that no tourist guide has ever written about.",
        "https://sovrinmagazine.com/assets/uploadeditor/photo/772390d4204fd2eccb36c60223bccd04.jpg",
        "Dara",
        "5h ago"
    ),
    PinCard(
        3,
        "áž•áŸ’ážŸáž¶ážšáž…áž¶ážŸáŸ‹áž€áŸ’ážšáŸ„áž˜",
        "Phnom Penh",
        "Market",
        76,
        "The oldest surviving wet market in the capital, where grandmothers still sell hand-woven silk passed down through generations.",
        "https://sovrinmagazine.com/assets/uploadeditor/photo/772390d4204fd2eccb36c60223bccd04.jpg",
        "Maly",
        "1d ago"
    ),
)

val recentPins = listOf(
    PinCard(
        4,
        "áž‡áž›áž”áŸ’ážšáž‘áž¶áž“áž‡áŸ’ážšáŸ„áž™áž…áž„áŸ’ážœáž¶ážš",
        "Kandal",
        "Nature",
        43,
        "A hidden riverside spot where fishermen gather at dawn. The mist over the Mekong here is unlike anything you have seen.",
        "https://kptmedia.ap-south-1.linodeobjects.com/uploads/2025/04/487925095_1071515321676124_4446736152088688747_n-768x1024.jpg",
        "Virak",
        "3h ago"
    ),
    PinCard(
        5,
        "áž€áž»ážŠáž·áž–áŸ’ážšáŸ‡ážŸáž„áŸ’ážƒáž…áž¶ážŸáŸ‹",
        "Battambang",
        "Pagoda",
        61,
        "A crumbling monk's sanctuary hidden in bamboo forest. The carvings on the walls date back to the French colonial era.",
        "https://kptmedia.ap-south-1.linodeobjects.com/uploads/2025/04/487925095_1071515321676124_4446736152088688747_n-768x1024.jpg",
        "Chanthy",
        "6h ago"
    ),
    PinCard(
        6,
        "ážˆáŸ’áž˜áž½áž‰áž€áž¶ážáŸ‹ážŸáž¼ážáŸ’ážš áž›áŸ„áž€áž˜áŸ‰áŸ‚ ážŸáŸŠáž¸áž˜",
        "Siem Reap",
        "Craft",
        88,
        "One of the last remaining silk weavers using traditional hand looms. She weaves stories into every thread.",
        "https://kptmedia.ap-south-1.linodeobjects.com/uploads/2025/04/487925095_1071515321676124_4446736152088688747_n-768x1024.jpg",
        "Kosal",
        "12h ago"
    ),
)

val provinceSpotlight = Triple("Battambang", 247, "The Bamboo Province")

private val InkText = Color(0xFFF8F7FF)
private val MutedText = Color(0xFFD0CAE8)
private val ElectricBlue = Color(0xFF76D6FF)
private val Bubblegum = Color(0xFFFF8BC8)
private val LimePop = Color(0xFFD8FF73)
private val DeepIndigo = Color(0xFF0D1020)
private val CardStart = Color(0xFF18152F)
private val CardEnd = Color(0xFF261D46)
private val SoftOutline = Color(0x33FFFFFF)

@Composable
fun HomeScreen(
    onNavigateToExplore: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToRanks: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onPinClick: (PinCard) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A1020),
                        Color(0xFF15112E),
                        Color(0xFF25113C),
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
                            ElectricBlue.copy(alpha = 0.28f),
                            Bubblegum.copy(alpha = 0.25f),
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
                        colors = listOf(LimePop.copy(alpha = 0.15f), Color.Transparent)
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
                        onExploreClick = onNavigateToExplore
                    )
                }

                item {
                    SectionHeader(
                        eyebrow = "TRENDING NOW",
                        title = "Spots everyone wants on their feed",
                        subtitle = "Swipe through local gems with brighter energy and faster visual payoff."
                    )
                }

                item {
                    TrendingRow(pins = trendingPins, onPinClick = onPinClick)
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
                        title = "Fresh finds with real local energy",
                        subtitle = "Cleaner hierarchy, bolder cards, and a vibe that feels more social-first."
                    )
                }

                items(recentPins) { pin ->
                    EditorialStoryCard(pin = pin, onPinClick = onPinClick)
                }

                item {
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }

            FloatingBottomNav(
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
            color = Color(0xB2201C3B),
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
                    listOf(Color(0xFF1B1A3D), Color(0xFF3A235E), Color(0xFF171A36))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(ElectricBlue.copy(alpha = 0.55f), Bubblegum.copy(alpha = 0.48f), Color.Transparent)
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
                    color = Color.White.copy(alpha = 0.08f),
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

            Text(
                text = "A brighter hero with stronger contrast makes the first screen feel more alive, more shareable, and way more current.",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MutedText
                )
            )

            Surface(
                onClick = onExploreClick,
                shape = RoundedCornerShape(18.dp),
                color = LimePop,
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
                        tint = Color(0xFF17122A),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "See the hype",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF17122A)
                        )
                    )
                }
            }
        }
    }
}

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

@Composable
private fun TrendingRow(pins: List<PinCard>, onPinClick: (PinCard) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        pins.forEachIndexed { index, pin ->
            TrendingCard(
                pin = pin,
                rank = index + 1,
                onClick = { onPinClick(pin) }
            )
        }
    }
}

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
                            Color(0x55401958),
                            Color(0xC2171630),
                            Color(0xF00C0D16)
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

@Composable
private fun CapsuleLabel(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xD5191731), RoundedCornerShape(999.dp))
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

@Composable
private fun FloatingBottomNav(
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
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xF0141730), Color(0xF0221940), Color(0xF0152035))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        ElectricBlue.copy(alpha = 0.35f),
                        Bubblegum.copy(alpha = 0.5f),
                        LimePop.copy(alpha = 0.25f)
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
        ) {
            onTabSelected(0)
        }
        BottomNavItem(
            icon = Icons.Default.Explore,
            label = "Explore",
            selected = selectedTab == 1
        ) {
            onTabSelected(1)
            onNavigateToExplore()
        }

        Surface(
            onClick = onNavigateToAdd,
            shape = CircleShape,
            color = LimePop,
            modifier = Modifier.size(58.dp),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF17122A),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        BottomNavItem(
            icon = Icons.Default.Leaderboard,
            label = "Ranks",
            selected = selectedTab == 3
        ) {
            onTabSelected(3)
            onNavigateToRanks()
        }
        BottomNavItem(
            icon = Icons.Default.Person,
            label = "Profile",
            selected = selectedTab == 4
        ) {
            onTabSelected(4)
            onNavigateToProfile()
        }
    }
}

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
            tint = if (selected) InkText else MutedText.copy(alpha = 0.65f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp,
                color = if (selected) InkText else MutedText.copy(alpha = 0.65f)
            )
        )
    }
}
