package com.example.neakta.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.home.PinCard

private val InkText = Color(0xFFF8F7FF)
private val MutedText = Color(0xFFD0CAE8)
private val ElectricBlue = Color(0xFF76D6FF)
private val Bubblegum = Color(0xFFFF8BC8)
private val LimePop = Color(0xFFD8FF73)
private val DeepIndigo = Color(0xFF0D1020)
private val CardStart = Color(0xFF18152F)
private val CardEnd = Color(0xFF261D46)
private val SoftOutline = Color(0x33FFFFFF)
private val GlassPanel = Color(0xD5191731)

@Composable
fun PinDetailScreen(
    pin: PinCard,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val pageScroll = rememberScrollState()
    val tagScroll = rememberScrollState()
    var foundPressed by remember { mutableStateOf(false) }
    var localVotes by remember { mutableIntStateOf(pin.votes) }

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
                            ElectricBlue.copy(alpha = 0.24f),
                            Bubblegum.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(pageScroll)
                .padding(bottom = 118.dp)
        ) {
            HeroSection(
                pin = pin,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                PinHeader(pin = pin)

                StatsRow(
                    pin = pin,
                    localVotes = localVotes
                )

                if (pin.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tagScroll),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pin.tags.forEach { tag ->
                            NeonCapsule(
                                text = tag,
                                accent = Bubblegum
                            )
                        }
                    }
                }

                DetailPanel(
                    eyebrow = "THE STORY",
                    title = "Why locals keep saving this",
                    accent = ElectricBlue
                ) {
                    Text(
                        text = pin.story,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText
                        )
                    )
                }

                LocationPanel(
                    pin = pin,
                    onOpenMaps = {
                        openInMaps(context, pin.lat, pin.lng, pin.title)
                    },
                    onCopyCoords = {
                        clipboardManager.setText(AnnotatedString("${pin.lat}, ${pin.lng}"))
                    }
                )

                TextButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MutedText.copy(alpha = 0.58f),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Flag as outdated or incorrect",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText.copy(alpha = 0.58f)
                        )
                    )
                }
            }
        }

        BottomFoundBar(
            foundPressed = foundPressed,
            localVotes = localVotes,
            onFoundClick = {
                if (!foundPressed) {
                    foundPressed = true
                    localVotes += 1
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HeroSection(
    pin: PinCard,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(390.dp)
    ) {
        AsyncImage(
            model = pin.imageUrl,
            contentDescription = pin.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x7710172D),
                            Color(0x22331755),
                            Color(0xEE0C0D16)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack
            )

            GlassIconButton(
                icon = Icons.Default.BookmarkBorder,
                contentDescription = "Save",
                onClick = { }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeonCapsule(
                    text = pin.category.uppercase(),
                    accent = ElectricBlue
                )
                NeonCapsule(
                    text = "#${pin.id} rising",
                    accent = LimePop
                )
            }

            Text(
                text = pin.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 38.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = InkText
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = GlassPanel,
        border = BorderStroke(1.dp, SoftOutline)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = InkText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PinHeader(pin: PinCard) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Bubblegum,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = pin.province,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
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
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, SoftOutline)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = LimePop,
                    modifier = Modifier
                        .padding(7.dp)
                        .size(15.dp)
                )
            }

            Text(
                text = "Pinned by ${pin.author}",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            )
        }
    }
}

@Composable
private fun StatsRow(
    pin: PinCard,
    localVotes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            value = "$localVotes",
            label = "saves",
            accent = LimePop,
            icon = Icons.Default.KeyboardArrowUp,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${pin.stillExistsPct}%",
            label = "still there",
            accent = if (pin.stillExistsPct >= 80) ElectricBlue else Bubblegum,
            icon = Icons.Default.TravelExplore,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = pin.yearDiscovered,
            label = "found",
            accent = Bubblegum,
            icon = Icons.Default.LocationOn,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    accent: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(CardStart, CardEnd)))
            .border(1.dp, SoftOutline, RoundedCornerShape(22.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(17.dp)
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = InkText
            ),
            maxLines = 1
        )
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun DetailPanel(
    eyebrow: String,
    title: String,
    accent: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(CardStart, CardEnd)))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.48f), SoftOutline, Color.Transparent)
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = eyebrow,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                color = accent
            )
        )
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Black,
                color = InkText
            )
        )
        content()
    }
}

@Composable
private fun LocationPanel(
    pin: PinCard,
    onOpenMaps: () -> Unit,
    onCopyCoords: () -> Unit
) {
    DetailPanel(
        eyebrow = "FIND IT",
        title = "Coordinates plus the local hint",
        accent = LimePop
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF10223F),
                            Color(0xFF263059),
                            Color(0xFF1A1533)
                        )
                    )
                )
                .border(1.dp, SoftOutline, RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(LimePop.copy(alpha = 0.12f))
                    .border(1.dp, LimePop.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LimePop,
                    modifier = Modifier.size(34.dp)
                )
            }

            Text(
                text = "${pin.lat}, ${pin.lng}",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkText
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailActionButton(
                text = "Open maps",
                icon = Icons.Default.Directions,
                accent = LimePop,
                filled = true,
                onClick = onOpenMaps,
                modifier = Modifier.weight(1f)
            )
            DetailActionButton(
                text = "Copy coords",
                icon = Icons.Default.ContentCopy,
                accent = ElectricBlue,
                filled = false,
                onClick = onCopyCoords,
                modifier = Modifier.weight(1f)
            )
        }

        if (pin.localDirections.isNotBlank()) {
            Text(
                text = pin.localDirections,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            )
        }
    }
}

@Composable
private fun DetailActionButton(
    text: String,
    icon: ImageVector,
    accent: Color,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (filled) accent else Color.Transparent,
        border = if (filled) null else BorderStroke(1.dp, accent.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (filled) Color(0xFF17122A) else accent,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (filled) Color(0xFF17122A) else accent
                )
            )
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
        border = BorderStroke(1.dp, accent.copy(alpha = 0.58f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.7.sp,
                color = InkText
            )
        )
    }
}

@Composable
private fun BottomFoundBar(
    foundPressed: Boolean,
    localVotes: Int,
    onFoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0xEE0D1020),
                            DeepIndigo
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onFoundClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (foundPressed) ElectricBlue else LimePop,
                    contentColor = Color(0xFF17122A)
                ),
                elevation = null
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color(0xFF17122A),
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (foundPressed) "Saved to the hype list" else "I found this spot",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF17122A)
                    )
                )
            }

            Text(
                text = "$localVotes people saved this find",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
            )
        }
    }
}

fun openInMaps(context: Context, lat: Double, lng: Double, label: String) {
    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val browserUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=$lat,$lng"
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}
