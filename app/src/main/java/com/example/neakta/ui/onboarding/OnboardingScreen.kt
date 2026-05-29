package com.example.neakta.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.neakta.R
import com.example.neakta.ui.components.NeaktaBrandMark
import com.example.neakta.ui.theme.Cinzel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.example.neakta.data.SessionManager

data class OnboardingPage(
    val imageRes: Int,
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val stat: String,
    val accent: Color
)

private val InkText = Color(0xFFF7FAFC)
private val MutedText = Color(0xFFB8C2CC)
private val ElectricBlue = Color(0xFF5FD3A6)
private val Bubblegum = Color(0xFF5FD3A6)
private val LimePop = Color(0xFF5FD3A6)
private val DeepIndigo = Color(0xFF0D1117)
private val SoftOutline = Color(0x26FFFFFF)
private val GlassPanel = Color(0x991A202C)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current                          // ADD
    val session = remember { SessionManager(context) }
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding_1,
            eyebrow = "FIND THE HYPE",
            title = "Cambodia's hidden spots, ready for your feed",
            subtitle = "Swipe into temples, food corners, shrines, and local stories with a brighter NeakTa energy.",
            stat = "247 pins rising",
            accent = ElectricBlue
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_2,
            eyebrow = "LOCAL LORE",
            title = "Stories that make every place hit different",
            subtitle = "Catch the myths, memories, and tiny details that turn a location into something worth saving.",
            stat = "Fresh finds daily",
            accent = Bubblegum
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_3,
            eyebrow = "DROP A PIN",
            title = "Save, rank, and share your own discoveries",
            subtitle = "Build your map, boost your favorite places, and help other explorers find the good stuff.",
            stat = "Your map starts now",
            accent = LimePop
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

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
                            LimePop.copy(alpha = 0.16f),
                            LimePop.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageContent(
                page = pages[pageIndex],
                pageNumber = pageIndex + 1,
                pageCount = pages.size
            )
        }

        OnboardingTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            onSkip = {
                session.setOnboardingSeen()   // ADD
                onFinished()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC111827),
                            DeepIndigo
                        )
                    )
                )
                .padding(horizontal = 20.dp)
                .padding(bottom = 34.dp, top = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            val isLastPage = pagerState.currentPage == pages.size - 1

            PagerDots(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                accent = pages[pagerState.currentPage].accent
            )

            OnboardingActionButton(
                isLastPage = isLastPage,
                accent = pages[pagerState.currentPage].accent,
                onClick = {
                    if (isLastPage) {
                        session.setOnboardingSeen()   // ADD
                        onFinished()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun OnboardingTopBar(
    modifier: Modifier = Modifier,
    onSkip: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            NeaktaBrandMark(logoSize = 38.dp, textColor = InkText, fontSize = 22.sp, letterSpacing = 5.sp)
            Text(
                text = "Cambodia, remixed for discovery",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            )
        }

        Surface(
            onClick = onSkip,
            shape = RoundedCornerShape(999.dp),
            color = GlassPanel,
            border = BorderStroke(1.dp, SoftOutline)
        ) {
            Text(
                text = "Skip",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkText
                )
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageNumber: Int,
    pageCount: Int
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x66111827),
                            Color(0x77111827),
                            Color(0xF00D1117)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(bottom = 174.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonCapsule(
                    text = page.eyebrow,
                    accent = page.accent
                )

                Text(
                    text = "0$pageNumber / 0$pageCount",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MutedText
                    )
                )
            }

            Text(
                text = page.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    lineHeight = 40.sp,
                    color = InkText
                )
            )

            Text(
                text = page.subtitle,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText,
                    lineHeight = 22.sp
                )
            )

            StatStrip(page = page)
        }
    }
}

@Composable
private fun NeonCapsule(
    text: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.65f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                color = accent
            )
        )
    }
}

@Composable
private fun StatStrip(page: OnboardingPage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(GlassPanel)
            .border(1.dp, SoftOutline, RoundedCornerShape(22.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = page.accent
            ) {
                Icon(
                    imageVector = Icons.Default.TravelExplore,
                    contentDescription = null,
                    tint = Color(0xFF0D1117),
                    modifier = Modifier
                        .padding(9.dp)
                        .size(18.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = page.stat,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = InkText
                    )
                )
                Text(
                    text = "Explore like it is already on your list",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = null,
            tint = page.accent,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PagerDots(
    pageCount: Int,
    currentPage: Int,
    accent: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = currentPage == index
            val width by animateDpAsState(
                targetValue = if (isSelected) 28.dp else 8.dp,
                animationSpec = tween(durationMillis = 300),
                label = "dot_width"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(if (isSelected) accent else Color.White.copy(alpha = 0.22f))
            )
        }
    }
}

@Composable
private fun OnboardingActionButton(
    isLastPage: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GlassPanel,
            contentColor = accent
        ),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.70f)),
        elevation = null
    ) {
        AnimatedContent(
            targetState = isLastPage,
            transitionSpec = {
                fadeIn(tween(220)) togetherWith fadeOut(tween(220))
            },
            label = "button_content"
        ) { last ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (last) Icons.Default.LocationOn else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (last) "Start exploring" else "Next drop",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accent
                    )
                )
            }
        }
    }
}
