package com.example.neakta.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

data class ProfileUser(
    val name: String,
    val province: String,
    val country: String,
    val level: String,
    val avatarUrl: String,
    val gems: Int,
    val upvotes: String,
    val karma: Int
)

data class ProfileGem(
    val title: String,
    val province: String,
    val country: String,
    val points: Int,
    val imageUrl: String
)

private val StaticUser = ProfileUser(
    name = "Pu Meav",
    province = "Prey Veng",
    country = "Cambodia",
    level = "Gem Hunter Lv.4",
    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=400&auto=format&fit=crop",
    gems = 12,
    upvotes = "1.2k",
    karma = 848
)

private val StaticGems = listOf(
    ProfileGem(
        title = "Angkor Wat",
        province = "Siem Reap",
        country = "Cambodia",
        points = 300,
        imageUrl = "https://images.unsplash.com/photo-1600080077819-730f6268492f?q=80&w=600&auto=format&fit=crop"
    ),
    ProfileGem(
        title = "Bayon Temple",
        province = "Siem Reap",
        country = "Cambodia",
        points = 280,
        imageUrl = "https://images.unsplash.com/photo-1556946851-9aee257e00dd?q=80&w=600&auto=format&fit=crop"
    ),
    ProfileGem(
        title = "Coastal",
        province = "Kampot",
        country = "Cambodia",
        points = 200,
        imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=600&auto=format&fit=crop"
    ),
    ProfileGem(
        title = "Phnom Penh Riverside",
        province = "Phnom Penh",
        country = "Cambodia",
        points = 160,
        imageUrl = "https://images.unsplash.com/photo-1596422846543-75c6fc197f07?q=80&w=600&auto=format&fit=crop"
    )
)

private val InkText = Color(0xFFF7FAFC)
private val MutedText = Color(0xFFB8C2CC)
private val NightBase = Color(0xFF0D1117)
private val CardBg = Color(0xE61A202C)
private val CardBgSoft = Color(0xCC111827)
private val Outline = Color(0x26FFFFFF)
private val Primary = Color(0xFF5FD3A6)
private val Accent = Color(0xFF5FD3A6)
private val GlassPanel = Color(0x991A202C)

@Composable
fun ProfileScreen(
    onNavigateHome: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToRanks: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1117),
                        Color(0xFF111827),
                        Color(0xFF0D1117),
                        NightBase
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Primary.copy(alpha = 0.18f),
                            Primary.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            ProfileTopBar(onNavigateToSettings = onNavigateToSettings)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { ProfileSummaryCard(user = StaticUser) }
                item { ProfileStatsRow(user = StaticUser) }
                item { MyGemsHeader() }
                items(StaticGems) { gem ->
                    GemRow(gem = gem)
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

@Composable
private fun ProfileTopBar(onNavigateToSettings: () -> Unit) {
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
                text = "PROFILE",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 24.sp,
                    letterSpacing = 5.sp,
                    color = InkText
                )
            )
            Text(
                text = "Your saved gems and reputation",
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
            border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
        ) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = InkText,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileSummaryCard(user: ProfileUser) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(CardBg, CardBgSoft)))
            .border(1.dp, Outline, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .border(1.dp, Primary, CircleShape)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = user.name,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(15.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "${user.province}, ${user.country}",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        color = MutedText
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Primary.copy(alpha = 0.16f))
                    .border(1.dp, Primary.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = user.level,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsRow(user: ProfileUser) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(CardBg)
            .border(1.dp, Outline, RoundedCornerShape(24.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatCell(value = user.gems.toString(), label = "Gems", modifier = Modifier.weight(1f))
        VerticalStatDivider()
        StatCell(value = user.upvotes, label = "Upvotes", modifier = Modifier.weight(1f))
        VerticalStatDivider()
        StatCell(value = user.karma.toString(), label = "Karma", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = InkText
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = MutedText
            )
        )
    }
}

@Composable
private fun VerticalStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(46.dp)
            .background(Outline)
    )
}

@Composable
private fun MyGemsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Gems",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = InkText
            )
        )
        Text(
            text = "View All",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary
            )
        )
    }
}

@Composable
private fun GemRow(gem: ProfileGem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBgSoft)
            .border(1.dp, Outline, RoundedCornerShape(22.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = gem.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(88.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = gem.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = InkText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${gem.province}, ${gem.country}",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = MutedText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = gem.points.toString(),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Primary
                )
            )
        }
    }
}
