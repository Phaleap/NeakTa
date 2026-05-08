package com.example.neakta.ui.onboarding


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.neakta.R
import com.example.neakta.ui.theme.Cinzel
import com.example.neakta.ui.theme.CormorantGaramond
import com.example.neakta.ui.theme.GemGold
import kotlinx.coroutines.launch

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val subtitle: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding_1,
            title = "Discover Sacred Places",
            subtitle = "Explore ancient temples, hidden shrines, and sacred landmarks across Cambodia"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_2,
            title = "Uncover Their Stories",
            subtitle = "Every place has a legend. Read the history and myths behind each sacred site"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_3,
            title = "Share Your Journey",
            subtitle = "Pin locations, save your favorites, and share discoveries with fellow explorers"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        // Full-bleed pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageContent(page = pages[pageIndex])
        }

        // Bottom overlay: dots + buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000)),
                        startY = 0f
                    )
                )
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Page dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) GemGold else Color(0x66F5D78E)
                            )
                    )
                }
            }

            // Next / Get Started button
            val isLastPage = pagerState.currentPage == pages.size - 1

            Button(
                onClick = {
                    if (isLastPage) {
                        onFinished()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, GemGold),
                elevation = null
            ) {
                AnimatedContent(
                    targetState = isLastPage,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    label = "button_text"
                ) { last ->
                    Text(
                        text = if (last) "GET STARTED" else "NEXT",
                        style = TextStyle(
                            fontFamily = Cinzel,
                            fontSize = 12.sp,
                            letterSpacing = 3.sp,
                            color = GemGold
                        )
                    )
                }
            }

            // Skip — hidden on last page
            if (!isLastPage) {
                TextButton(onClick = onFinished) {
                    Text(
                        text = "SKIP",
                        style = TextStyle(
                            fontFamily = Cinzel,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            color = Color(0x99F5D78E)
                        )
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Full-bleed background image
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark scrim so text reads clearly
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x55000000))
        )

        // Title + subtitle anchored above the button area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp)
                .padding(bottom = 200.dp),   // leaves room for dots + buttons
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Decorative divider
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(GemGold)
            )

            Text(
                text = page.title.uppercase(),
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 2.sp,
                    color = GemGold,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = page.subtitle,
                style = TextStyle(
                    fontFamily = CormorantGaramond,
                    fontSize = 16.sp,
                    color = Color(0xCCF5ECD7),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            )
        }
    }
}