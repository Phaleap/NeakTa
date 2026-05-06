package com.example.neakta.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.neakta.R

// ✅ Color tokens defined locally
private val GemGold = Color(0xFFE8C97A)
private val NightBase = Color(0xFF0C0C0E)
private val TextPrimary = Color(0xFFF0EDE6)
private val TextSecondary = Color(0xFF9E9A92)

@Composable
fun SplashScreen(
    onExploreClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Bayon photo — full bleed
        AsyncImage(
            model = R.drawable.bayon_intro,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient — clear at top, dark at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x00000000),
                            0.45f to Color(0x00000000),
                            0.65f to Color(0xBB0C0C0E),
                            1.0f to Color(0xFF0C0C0E)
                        )
                    )
                )
        )

        // Bottom content
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Text(
                text = "NEAKTA ✦",
                color = GemGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Hook text
            Text(
                text = "Every corner of Cambodia\nhides a story.",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sub text
            Text(
                text = "Be the one who finds it.",
                color = TextSecondary,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(28.dp))

            // CTA Button
            Button(
                onClick = onExploreClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GemGold
                )
            ) {
                Text(
                    text = "Explore more →",
                    color = NightBase,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}