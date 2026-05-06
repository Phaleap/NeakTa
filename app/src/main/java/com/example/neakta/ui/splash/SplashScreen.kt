package com.example.neakta.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

// ─── Font Families ────────────────────────────────────────────────────────────
private val CormorantGaramond = FontFamily(
    Font(R.font.cormorant_garamond_light,    FontWeight.Light),
    Font(R.font.cormorant_garamond_regular,  FontWeight.Normal),
    Font(R.font.cormorant_garamond_italic,   FontWeight.Normal,  FontStyle.Italic),
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

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(
    onGetStarted: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background (Bayon image) ─────────────────────────
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
                            Color.Transparent,
                            Color(0xCC0C0A07),
                            Color(0xFF0C0A07)
                        )
                    )
                )
        )

        // ── Center Content ───────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .alpha(alphaAnim),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🟡 Angkor Wat silhouette
            AsyncImage(
                model = R.drawable.angkorwat,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🟡 Circular logo placeholder
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(1.dp, AntiqueGold, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "ANGKOR\nWAT LOGO",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = AntiqueGold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 🟡 Title
            Text(
                text = "CAMBODIA",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 26.sp,
                    letterSpacing = 6.sp,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "HIDDEN GEMS",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 14.sp,
                    letterSpacing = 4.sp,
                    color = AntiqueGold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(RuleColor)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Find. Share. Preserve.",
                style = TextStyle(
                    fontFamily = CormorantGaramond,
                    fontSize = 14.sp,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            )
        }

        // ── Bottom Button ────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedButton(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AntiqueGold),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = AntiqueGold
                )
            ) {
                Text(
                    "GET STARTED",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 12.sp,
                        letterSpacing = 3.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "EXPLORE CAMBODIA",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = AntiqueGoldDim
                )
            )
        }
    }
}
