package com.example.neakta.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.neakta.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Font Families ────────────────────────────────────────────────────────────
private val CormorantGaramond = FontFamily(
    Font(R.font.cormorant_garamond_light,    FontWeight.Light),
    Font(R.font.cormorant_garamond_regular,  FontWeight.Normal),
    Font(R.font.cormorant_garamond_italic,   FontWeight.Normal, FontStyle.Italic),
    Font(R.font.cormorant_garamond_semibold, FontWeight.SemiBold),
)

private val Cinzel = FontFamily(
    Font(R.font.cinzel_regular,  FontWeight.Normal),
    Font(R.font.cinzel_semibold, FontWeight.SemiBold),
)

// ─── Color Tokens ─────────────────────────────────────────────────────────────
private val AntiqueGold    = Color(0xFFD4B870)
private val AntiqueGoldDim = Color(0xFFD4B870).copy(alpha = 0.5f)
private val TextPrimary    = Color(0xFFEEE6D2)
private val TextMuted      = Color(0xFFB4A88C).copy(alpha = 0.70f)
private val RuleColor      = Color(0xFFD4B870).copy(alpha = 0.35f)

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val alphaAnim by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900),
        label = "alpha"
    )

    // ── Swipe state ──────────────────────────────────────────
    var swipeDelta by remember { mutableStateOf(0f) }
    val SWIPE_THRESHOLD = 80f  // ✅ Short flick — was 200f

    val buttonOffsetY by animateFloatAsState(
        targetValue = swipeDelta.coerceIn(-SWIPE_THRESHOLD, 0f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonOffset"
    )

    val chevronAlpha by rememberInfiniteTransition(label = "chevron")
        .animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "chevronPulse"
        )

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background ───────────────────────────────────────
        AsyncImage(
            model = R.drawable.bayon_intro,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ── Dark overlay ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x330C0A07),   // top — subtle
                            Color(0x880C0A07),   // upper-mid — starts getting dark sooner
                            Color(0xCC0C0A07),   // lower — heavy dark
                            Color(0xFF0C0A07)    // bottom — fully solid
                        )

                    )
                )
        )

        // ── TOP: NEAKTA ──────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp)   // ✅ Not fully top, breathing room
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEAKTA",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 42.sp,
                    letterSpacing = 10.sp,
                    color = AntiqueGold,
                    textAlign = TextAlign.Center
                )
            )
        }

        // ── CENTER: taglines ─────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-80).dp)          // ✅ nudge up ~60dp
                .padding(horizontal = 32.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            AsyncImage(
                model = R.drawable.angkorwat,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CAMBODIA · HIDDEN GEMS",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 13.sp,
                    letterSpacing = 4.sp,
                    color = TextPrimary.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(1.dp)
                    .background(RuleColor)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Find. Share. Preserve.",
                style = TextStyle(
                    fontFamily = CormorantGaramond,
                    fontSize = 17.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            )
        }

        // ── BOTTOM: Swipe-Up Explore Button ──────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
                .offset(y = buttonOffsetY.dp)
                .alpha(alphaAnim)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (swipeDelta <= -SWIPE_THRESHOLD) {
                                scope.launch { onGetStarted() }
                            } else {
                                swipeDelta = 0f
                            }
                        },
                        onDragCancel = { swipeDelta = 0f },
                        onVerticalDrag = { _, dragAmount ->
                            if (dragAmount < 0) {
                                swipeDelta = (swipeDelta + dragAmount)
                                    .coerceAtLeast(-SWIPE_THRESHOLD)
                            }
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Pulsing chevrons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-6).dp)
            ) {
                Text("⌃", style = TextStyle(color = AntiqueGold.copy(alpha = chevronAlpha * 0.4f), fontSize = 18.sp))
                Text("⌃", style = TextStyle(color = AntiqueGold.copy(alpha = chevronAlpha * 0.7f), fontSize = 18.sp))
                Text("⌃", style = TextStyle(color = AntiqueGold.copy(alpha = chevronAlpha),        fontSize = 18.sp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Pill button
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(56.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                AntiqueGold.copy(alpha = 0.15f),
                                AntiqueGold.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .border(
                        width = 1.dp,
                        color = AntiqueGold.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EXPLORE",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 12.sp,
                        letterSpacing = 3.sp,
                        color = AntiqueGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "swipe up",
                style = TextStyle(
                    fontFamily = CormorantGaramond,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = AntiqueGoldDim
                )
            )
        }
    }
}