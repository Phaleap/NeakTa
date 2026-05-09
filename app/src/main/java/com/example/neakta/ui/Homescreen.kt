package com.example.neakta.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.auth.CormorantGaramond
import com.example.neakta.ui.auth.GemGold
import com.example.neakta.ui.auth.GemGoldDim
import com.example.neakta.ui.auth.NightBase
import com.example.neakta.ui.auth.TextPrimary
import com.example.neakta.ui.auth.TextSecondary
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.File

// ─── Data ────────────────────────────────────────────────────
data class Category(val label: String, val icon: ImageVector)

val categories = listOf(
    Category("All",      Icons.Default.Apps),
    Category("Food",     Icons.Default.Restaurant),
    Category("Pagoda",   Icons.Default.AccountBalance),
    Category("Nature",   Icons.Default.Park),
    Category("Craft",    Icons.Default.Handyman),
    Category("Market",   Icons.Default.Store),
)

// ─── Mock pins for UI preview ────────────────────────────────
data class PinPreview(
    val title: String,
    val province: String,
    val category: String,
    val votes: Int,
    val story: String
)

val mockPins = listOf(
    PinPreview("បាយហាំ លោក តា ចាន់", "Siem Reap", "Food", 128,
        "A humble stall that has served the same recipe for 40 years, hidden behind the old market..."),
    PinPreview("វត្តភ្នំជីសូរ", "Kampot", "Pagoda", 94,
        "Perched on a limestone hill, this pagoda offers a breathtaking view at sunrise..."),
    PinPreview("ផ្សារចាស់ក្រោម", "Phnom Penh", "Market", 76,
        "The oldest surviving wet market in the capital, where grandmothers still sell hand-woven silk..."),
)

// ─── Home Screen ─────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToDiscover: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToRanks: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPin by remember { mutableStateOf<PinPreview?>(mockPins[0]) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Full screen map ───────────────────────────────────
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                Configuration.getInstance().apply {
                    userAgentValue = ctx.packageName
                    osmdroidBasePath = File(ctx.cacheDir, "osmdroid")
                    osmdroidTileCache = File(ctx.cacheDir, "osmdroid/tiles")
                }
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(7.0)
                    controller.setCenter(GeoPoint(12.5657, 104.9910))
                }
            }
        )

        // ── Dark overlay at top for readability ───────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xDD0C0A07), Color.Transparent)
                    )
                )
        )

        // ── Dark overlay at bottom for card ──────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xF20C0A07))
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "NEAKTA",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 20.sp,
                        letterSpacing = 6.sp,
                        color = GemGold
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xAA0C0A07), CircleShape)
                            .border(1.dp, GemGold.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search",
                            tint = GemGold, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xAA0C0A07), CircleShape)
                            .border(1.dp, GemGold.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications",
                            tint = GemGold, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // ── CATEGORY CHIPS ────────────────────────────────
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat.label
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isSelected) GemGold
                                else Color(0xCC0C0A07)
                            )
                            .border(
                                1.dp,
                                if (isSelected) GemGold else GemGold.copy(alpha = 0.3f),
                                RoundedCornerShape(50)
                            )
                            .clickable { selectedCategory = cat.label }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = null,
                            tint = if (isSelected) NightBase else GemGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = cat.label,
                            style = TextStyle(
                                fontFamily = Cinzel,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = if (isSelected) NightBase else GemGold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── PIN CARD ──────────────────────────────────────
            selectedPin?.let { pin ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .background(
                            color = Color(0xF00C0A07),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(GemGold.copy(alpha = 0.5f), Color.Transparent,
                                    GemGold.copy(alpha = 0.2f))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { }
                        .padding(16.dp)
                ) {
                    Column {
                        // Province + category
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null,
                                    tint = GemGold, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "${pin.province} · ${pin.category}",
                                    style = TextStyle(
                                        fontFamily = Cinzel,
                                        fontSize = 9.sp,
                                        letterSpacing = 1.sp,
                                        color = GemGoldDim
                                    )
                                )
                            }
                            // Votes
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null,
                                    tint = GemGold, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "+${pin.votes}",
                                    style = TextStyle(
                                        fontFamily = Cinzel,
                                        fontSize = 11.sp,
                                        color = GemGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title
                        Text(
                            text = pin.title,
                            style = TextStyle(
                                fontFamily = CormorantGaramond,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Story excerpt
                        Text(
                            text = pin.story,
                            style = TextStyle(
                                fontFamily = CormorantGaramond,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Read more
                        Text(
                            text = "Read the story →",
                            style = TextStyle(
                                fontFamily = Cinzel,
                                fontSize = 9.sp,
                                letterSpacing = 2.sp,
                                color = GemGold
                            )
                        )
                    }
                }
            }

            // ── BOTTOM NAV ────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C0A07))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, GemGold.copy(alpha = 0.3f),
                                Color.Transparent)
                        ),
                        shape = RoundedCornerShape(0.dp)
                    )
                    .navigationBarsPadding()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(icon = Icons.Default.Map,       label = "Explore",  selected = selectedTab == 0) { selectedTab = 0 }
                BottomNavItem(icon = Icons.Default.Explore,   label = "Discover", selected = selectedTab == 1) { selectedTab = 1; onNavigateToDiscover() }
                // Add button — center, gold
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
                BottomNavItem(icon = Icons.Default.Leaderboard, label = "Ranks",   selected = selectedTab == 3) { selectedTab = 3; onNavigateToRanks() }
                BottomNavItem(icon = Icons.Default.Person,       label = "Profile", selected = selectedTab == 4) { selectedTab = 4; onNavigateToProfile() }
            }
        }
    }
}

// ─── Bottom Nav Item ─────────────────────────────────────────
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