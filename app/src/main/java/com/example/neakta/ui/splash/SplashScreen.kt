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
import androidx.compose.ui.draw.clip
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
import com.example.neakta.ui.components.NeaktaVerticalBrandMark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Font Families ────────────────────────────────────────────
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

// ─── Color Tokens ─────────────────────────────────────────────
private val ElectricBlue = Color(0xFF5FD3A6)
private val Bubblegum    = Color(0xFF5FD3A6)
private val LimePop      = Color(0xFF5FD3A6)
private val InkText      = Color(0xFFF7FAFC)
private val MutedText    = Color(0xFFB8C2CC)
private val SoftOutline  = Color(0x26FFFFFF)
private val DeepIndigo   = Color(0xFF0D1117)

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
    val SWIPE_THRESHOLD = 80f

    val buttonOffsetY by animateFloatAsState(
        targetValue = swipeDelta.coerceIn(-SWIPE_THRESHOLD, 0f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonOffset"
    )

    // Pulsing chevron
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

    // Shifting gradient animation for the glow strip
    val glowShift by rememberInfiniteTransition(label = "glow")
        .animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowShift"
        )

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background photo (kept as-is) ────────────────────
        AsyncImage(
            model = R.drawable.bayon_intro,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ── Dark-indigo gradient overlay (replaces pure black) ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x330D1117),
                            0.3f to Color(0x77111827),
                            0.6f to Color(0xBB111827),
                            1.0f to Color(0xFF0D1117)
                        )
                    )
                )
        )

        // ── Ambient color glows on top of photo ──────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            ElectricBlue.copy(alpha = 0.18f * glowShift + 0.08f),
                            Bubblegum.copy(alpha = 0.15f * (1f - glowShift) + 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(LimePop.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        // ── TOP: NEAKTA wordmark ─────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 40.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeaktaVerticalBrandMark(
                logoSize = 96.dp,
                textColor = InkText,
                fontSize = 38.sp,
                letterSpacing = 9.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Animated color underline strip
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(listOf(LimePop, LimePop.copy(alpha = 0.35f)))
                    )
            )
        }

        // ── CENTER: taglines + angkor image ──────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-70).dp)
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


            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Find. Share. Preserve.",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Cambodia, remixed for discovery",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            )
        }

        // ── BOTTOM: Swipe-Up button ───────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp)
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
            // Pulsing chevrons — electric blue
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-6).dp)
            ) {
                Text("⌃", style = TextStyle(color = ElectricBlue.copy(alpha = chevronAlpha * 0.3f), fontSize = 18.sp))
                Text("⌃", style = TextStyle(color = ElectricBlue.copy(alpha = chevronAlpha * 0.6f), fontSize = 18.sp))
                Text("⌃", style = TextStyle(color = ElectricBlue.copy(alpha = chevronAlpha),        fontSize = 18.sp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // EXPLORE pill — gradient border, glass fill
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                ElectricBlue.copy(alpha = 0.18f),
                                Bubblegum.copy(alpha = 0.12f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                ElectricBlue.copy(alpha = 0.8f),
                                Bubblegum.copy(alpha = 0.6f),
                                LimePop.copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(999.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EXPLORE",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 12.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "swipe up",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = MutedText.copy(alpha = 0.5f)
                )
            )
        }
    }
}

// ─── Pill chip ────────────────────────────────────────────────
@Composable
private fun SplashPill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0x991A202C))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.55f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = color
            )
        )
    }
}
