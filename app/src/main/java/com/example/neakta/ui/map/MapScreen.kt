package com.example.neakta.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.neakta.data.SessionManager
import com.example.neakta.network.RetrofitClient
import com.example.neakta.network.normalizeImageUrl
import com.example.neakta.network.normalizeImageUrls
import com.example.neakta.util.getBestLastKnownLocation
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.components.NeaktaBrandMark
import com.example.neakta.ui.core.AppLanguage
import com.example.neakta.ui.core.LocalAppLanguage
import com.example.neakta.ui.home.PinCard
import com.example.neakta.ui.home.PinViewModel
import com.example.neakta.ui.home.PinsState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private val InkText       = Color(0xFFF7FAFC)
private val MutedText     = Color(0xFFB8C2CC)
private val Primary       = Color(0xFF5FD3A6)
private val DeepBase      = Color(0xFF0D1117)
private val SurfaceGlass  = Color(0x991A202C)
private val SurfaceStrong = Color(0xE61A202C)
private val Outline       = Color(0x26FFFFFF)

@Composable
fun MapScreen(
    onPinClick: (PinCard) -> Unit,
    viewModel: PinViewModel
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val pinsState by viewModel.pinsState.collectAsState()
    val languageState = LocalAppLanguage.current
    val isKhmer = languageState.current == AppLanguage.KHMER

    var query            by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var nearbyPins       by remember { mutableStateOf<List<PinCard>>(emptyList()) }
    var isLoadingNearby  by remember { mutableStateOf(false) }
    var userLat          by remember { mutableStateOf<Double?>(null) }
    var userLng          by remember { mutableStateOf<Double?>(null) }

    val categories   = listOf("All", "Food", "Pagoda", "Nature", "Market", "Craft")
    val categoriesKh = listOf("ទាំងអស់", "អាហារ", "វត្ត", "ធម្មជាតិ", "ផ្សារ", "សិប្បកម្ម")

    // — Location permission launcher —
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getBestLastKnownLocation(context)?.let {
                userLat = it.latitude
                userLng = it.longitude
            }
        }
    }

    // — Fetch nearby pins whenever location becomes available —
    LaunchedEffect(userLat, userLng) {
        val lat = userLat ?: return@LaunchedEffect
        val lng = userLng ?: return@LaunchedEffect
        isLoadingNearby = true
        try {
            val token = "Bearer ${session.getToken()}"
            val response = RetrofitClient.instance.getNearbyPins(token, lat, lng, 10.0)
            if (response.isSuccessful) {
                nearbyPins = response.body().orEmpty().map { pin ->
                    PinCard(
                        id              = pin.id,
                        title           = pin.title,
                        province        = pin.provinceName ?: "",
                        category        = pin.categoryName ?: "",
                        votes           = pin.upvoteCount,
                        story           = pin.story,
                        imageUrl        = normalizeImageUrl(pin.imageUrl),
                        author          = pin.authorUsername ?: "",
                        timeAgo         = pin.createdAt?.take(10) ?: "",
                        lat             = pin.lat?.toDouble() ?: 11.5564,
                        lng             = pin.lng?.toDouble() ?: 104.9282,
                        tags            = pin.tags ?: emptyList(),
                        mediaUrls       = normalizeImageUrls(pin.mediaUrls),
                        localDirections = pin.localDirections ?: "",
                        stillExistsPct  = pin.score.coerceIn(0, 100).takeIf { it > 0 } ?: 97,
                        yearDiscovered  = pin.createdAt?.take(4) ?: "2024"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MapScreen", "Nearby fetch failed: ${e.message}")
        }
        isLoadingNearby = false
    }

    val allPins = when (val state = pinsState) {
        is PinsState.Success -> state.pins.map { pin ->
            PinCard(
                id              = pin.id,
                title           = pin.title,
                province        = pin.provinceName ?: "",
                category        = pin.categoryName ?: "",
                votes           = pin.upvoteCount,
                story           = pin.story,
                imageUrl        = normalizeImageUrl(pin.imageUrl),
                author          = pin.authorUsername ?: "",
                timeAgo         = pin.createdAt?.take(10) ?: "",
                lat             = pin.lat?.toDouble() ?: 11.5564,
                lng             = pin.lng?.toDouble() ?: 104.9282,
                tags            = pin.tags ?: emptyList(),
                mediaUrls       = normalizeImageUrls(pin.mediaUrls),
                localDirections = pin.localDirections ?: "",
                stillExistsPct  = pin.score.coerceIn(0, 100).takeIf { it > 0 } ?: 97,
                yearDiscovered  = pin.createdAt?.take(4) ?: "2024"
            )
        }
        else -> emptyList()
    }

    val filteredPins = allPins.filter { pin ->
        val matchesCategory = selectedCategory == "All" || pin.category == selectedCategory
        val matchesQuery    = query.isBlank() ||
                pin.title.contains(query, ignoreCase = true) ||
                pin.province.contains(query, ignoreCase = true) ||
                pin.category.contains(query, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    // Use nearbyPins if available, otherwise fall back to filteredPins
    val displayPins = if (nearbyPins.isNotEmpty()) nearbyPins else filteredPins

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF111827), DeepBase)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    Brush.linearGradient(listOf(Primary.copy(alpha = 0.18f), Primary.copy(alpha = 0.04f), Color.Transparent))
                )
        )

        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding      = androidx.compose.foundation.layout.PaddingValues(bottom = 116.dp)
        ) {
            item { MapHeader(isKhmer = isKhmer) }

            item {
                SearchField(
                    query         = query,
                    onQueryChange = { query = it },
                    isKhmer       = isKhmer
                )
            }

            item {
                CategoryRow(
                    categories         = categories,
                    categoriesKh       = categoriesKh,
                    selectedCategory   = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    isKhmer            = isKhmer
                )
            }

            item {
                RealMapPreview(
                    pins       = filteredPins,
                    onPinClick = onPinClick,
                    isKhmer    = isKhmer
                )
            }

            // — Section title row with "Near me" button —
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    SectionTitle(
                        title    = if (isKhmer) "Gems នៅជិត" else "Nearby gems",
                        subtitle = if (nearbyPins.isNotEmpty())
                            if (isKhmer) "${nearbyPins.size} gems ក្នុង 10km"
                            else "${nearbyPins.size} gems within 10km"
                        else
                            if (isKhmer) "អនុញ្ញាតទីតាំងដើម្បីរកឃើញ gems ជិតបំផុត"
                            else "Allow location to find nearest gems"
                    )

                    Surface(
                        onClick = {
                            val perm = Manifest.permission.ACCESS_FINE_LOCATION
                            if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
                                getBestLastKnownLocation(context)?.let {
                                    userLat = it.latitude
                                    userLng = it.longitude
                                }
                            } else {
                                locationPermissionLauncher.launch(perm)
                            }
                        },
                        shape  = RoundedCornerShape(999.dp),
                        color  = SurfaceGlass,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.55f))
                    ) {
                        Row(
                            modifier              = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, null, tint = Primary, modifier = Modifier.size(15.dp))
                            Text(
                                text  = if (isKhmer) "ទីតាំងខ្ញុំ" else "Near me",
                                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                            )
                        }
                    }
                }
            }

            // — Pin list —
            when (pinsState) {
                is PinsState.Loading -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is PinsState.Error -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text  = if (isKhmer) "ផ្ទុកទិន្នន័យបរាជ័យ 😕" else "Failed to load gems 😕",
                            style = TextStyle(color = MutedText, fontSize = 14.sp)
                        )
                    }
                }
                is PinsState.Success -> {
                    if (isLoadingNearby) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                    items(displayPins) { pin ->
                        MapGemCard(pin = pin, onClick = { onPinClick(pin) }, isKhmer = isKhmer)
                    }
                    if (displayPins.isEmpty() && !isLoadingNearby) {
                        item { EmptyMapState(query = query, category = selectedCategory, isKhmer = isKhmer) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RealMapPreview(
    pins: List<PinCard>,
    onPinClick: (PinCard) -> Unit,
    isKhmer: Boolean
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceStrong)
            .border(1.dp, Outline, RoundedCornerShape(28.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                Configuration.getInstance().userAgentValue = context.packageName
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(7.0)
                    controller.setCenter(GeoPoint(12.5657, 104.9910))
                }
            },
            update = { mapView ->
                mapView.overlays.removeAll { it is Marker }

                pins.forEach { pin ->
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(pin.lat, pin.lng)
                        title = pin.title
                        subDescription = "${pin.province} | ${pin.category}"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            onPinClick(pin)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }

                pins.firstOrNull()?.let { first ->
                    mapView.controller.setCenter(GeoPoint(first.lat, first.lng))
                }
                mapView.invalidate()
            }
        )

        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp),
            shape = RoundedCornerShape(999.dp),
            color = SurfaceGlass,
            border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.55f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Icon(Icons.Default.MyLocation, null, tint = Primary, modifier = Modifier.size(16.dp))
                Text(
                    text = if (isKhmer) "${pins.size} gems លើផែនទី" else "${pins.size} gems on map",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                )
            }
        }
    }
}

@Composable
private fun MapHeader(isKhmer: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NeaktaBrandMark(logoSize = 42.dp, textColor = InkText, fontSize = 24.sp, letterSpacing = 5.sp)
        Text(
            text  = if (isKhmer) "រុករកកម្ពុជាតាមកន្លែង ខេត្ត និងស្មារតីក្នុងស្រុក"
            else "Explore Cambodia by place, province, and local memory",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MutedText)
        )
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, isKhmer: Boolean) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        modifier      = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        placeholder   = {
            Text(
                text  = if (isKhmer) "ស្វែងរក gems ឬខេត្ត" else "Search hidden gems or provinces",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, color = MutedText)
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Search, null, tint = Primary, modifier = Modifier.size(19.dp))
        },
        singleLine = true,
        shape      = RoundedCornerShape(20.dp),
        colors     = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = Primary,
            unfocusedBorderColor    = Outline,
            focusedTextColor        = InkText,
            unfocusedTextColor      = InkText,
            cursorColor             = Primary,
            focusedContainerColor   = SurfaceGlass,
            unfocusedContainerColor = SurfaceGlass
        )
    )
}

@Composable
private fun CategoryRow(
    categories: List<String>,
    categoriesKh: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    isKhmer: Boolean
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEachIndexed { index, category ->
            val selected     = category == selectedCategory
            val displayLabel = if (isKhmer) categoriesKh[index] else category
            Surface(
                onClick = { onCategorySelected(category) },
                shape   = RoundedCornerShape(999.dp),
                color   = SurfaceGlass,
                border  = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Primary.copy(alpha = 0.72f) else Outline)
            ) {
                Text(
                    text     = displayLabel,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style    = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = if (selected) Primary else MutedText
                    )
                )
            }
        }
    }
}

@Composable
private fun MapPreview(pinCount: Int, isKhmer: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(210.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF1A202C), Color(0xFF0D1117))))
            .border(1.dp, Outline, RoundedCornerShape(28.dp))
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .align(when (index) { 0 -> Alignment.TopStart; 1 -> Alignment.TopEnd; 2 -> Alignment.Center; else -> Alignment.BottomStart })
                    .padding(
                        start  = if (index == 0 || index == 3) 34.dp else 0.dp,
                        top    = if (index == 0) 36.dp else 0.dp,
                        end    = if (index == 1) 52.dp else 0.dp,
                        bottom = if (index == 3) 34.dp else 0.dp
                    )
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.12f))
                    .border(1.dp, Primary.copy(alpha = 0.60f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(22.dp))
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp),
            shape    = RoundedCornerShape(999.dp),
            color    = SurfaceGlass,
            border   = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.55f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Icon(Icons.Default.MyLocation, null, tint = Primary, modifier = Modifier.size(16.dp))
                Text(
                    text  = if (isKhmer) "$pinCount gems ត្រៀមផែនទី" else "$pinCount map-ready gems",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title,    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp, fontWeight = FontWeight.Black, color = InkText))
        Text(text = subtitle, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, lineHeight = 19.sp, color = MutedText))
    }
}

@Composable
private fun MapGemCard(pin: PinCard, onClick: () -> Unit, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceStrong)
            .border(1.dp, Outline, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        AsyncImage(
            model            = pin.imageUrl.ifBlank { null },
            contentDescription = null,
            contentScale     = ContentScale.Crop,
            modifier         = Modifier.size(82.dp).clip(RoundedCornerShape(18.dp))
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(14.dp))
                Text(text = pin.province, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary))
            }
            Text(
                text     = pin.title,
                style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, lineHeight = 21.sp, fontWeight = FontWeight.Black, color = InkText),
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.KeyboardArrowUp, null, tint = Primary, modifier = Modifier.size(15.dp))
                Text(
                    text  = if (isKhmer) "${pin.votes} រក្សាទុក | ${pin.category}"
                    else "${pin.votes} saves | ${pin.category}",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                )
            }
        }
    }
}

@Composable
private fun EmptyMapState(query: String, category: String, isKhmer: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp, vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.Map, null, tint = Primary, modifier = Modifier.size(34.dp))
        Text(
            text  = if (isKhmer) "រកមិនឃើញ gems" else "No gems found",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, fontWeight = FontWeight.Black, color = InkText)
        )
        Text(
            text  = if (isKhmer) "សាកល្បងខេត្ត ប្រភេទ ឬពាក្យស្វែងរកផ្សេង"
            else "Try another province, category, or search term.",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, lineHeight = 19.sp, color = MutedText)
        )
    }
}
