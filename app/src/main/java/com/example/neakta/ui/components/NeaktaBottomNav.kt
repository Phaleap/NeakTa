package com.example.neakta.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Primary = Color(0xFF5FD3A6)
private val SurfaceGlass = Color(0x991A202C)
private val MutedText = Color(0xFFB8C2CC)
private val Outline = Color(0x26FFFFFF)

enum class NavTab { HOME, MAP, ADD, RANKS, SAVED }

@Composable
fun NeaktaBottomNav(
    activeTab: NavTab,
    onNavigateHome: () -> Unit = {},
    onNavigateMap: () -> Unit = {},
    onNavigateAdd: () -> Unit = {},
    onNavigateRanks: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.horizontalGradient(listOf(SurfaceGlass, SurfaceGlass))
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Primary.copy(alpha = 0.65f), Primary.copy(alpha = 0.18f))
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = activeTab == NavTab.HOME,
            onClick = onNavigateHome
        )
        NavItem(
            icon = Icons.Default.Map,
            label = "Map",
            selected = activeTab == NavTab.MAP,
            onClick = onNavigateMap
        )

        // Center FAB
        Surface(
            onClick = onNavigateAdd,
            shape = CircleShape,
            color = SurfaceGlass,
            border = BorderStroke(1.dp, Primary.copy(alpha = 0.70f)),
            modifier = Modifier.size(58.dp),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Gem",
                    tint = Primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        NavItem(
            icon = Icons.Default.Leaderboard,  // ✅ consistent icon
            label = "Ranks",
            selected = activeTab == NavTab.RANKS,
            onClick = onNavigateRanks
        )
        NavItem(
            icon = Icons.Default.Bookmark,   // was Icons.Default.Person
            label = "Saved",                  // was "Profile"
            selected = activeTab == NavTab.SAVED,
            onClick = onNavigateProfile       // rename param below
        )
    }
}

@Composable
private fun NavItem(
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
            tint = if (selected) Primary else MutedText.copy(alpha = 0.65f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp,
                color = if (selected) Primary else MutedText.copy(alpha = 0.65f)
            )
        )
    }
}