package com.example.neakta.ui.add

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.neakta.data.SessionManager
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.home.ElectricBlue
import com.example.neakta.ui.home.Bubblegum
import com.example.neakta.ui.home.LimePop
import com.example.neakta.ui.home.SoftOutline

// ─── Colors local to this screen ─────────────────────────────
private val InkText   = Color(0xFFF7FAFC)
private val MutedText = Color(0xFFB8C2CC)
private val CardBg    = Color(0xE61A202C)
private val FieldBg   = Color(0x991A202C)
private val FieldBorder = Color(0x26FFFFFF)

// ─── Cambodian Provinces ─────────────────────────────────────
val cambodianProvinces = listOf(
    "Banteay Meanchey", "Battambang", "Kampong Cham", "Kampong Chhnang",
    "Kampong Speu", "Kampong Thom", "Kampot", "Kandal", "Kep",
    "Koh Kong", "Kratié", "Mondulkiri", "Oddar Meanchey", "Pailin",
    "Phnom Penh", "Preah Sihanouk", "Preah Vihear", "Prey Veng",
    "Pursat", "Ratanakiri", "Siem Reap", "Stung Treng", "Svay Rieng",
    "Takéo", "Tboung Khmum"
)

// ─── Categories ───────────────────────────────────────────────
val gemCategories = listOf("Food", "Pagoda", "Nature", "Market", "Craft", "Art", "History")

// ─── Available Tags ───────────────────────────────────────────
val availableTags = listOf(
    "LOCALS ONLY", "HIDDEN", "BREAKFAST", "SUNRISE", "SUNSET",
    "FREE", "SACRED", "TRADITIONAL", "STREET FOOD", "NATURE",
    "HERITAGE", "PEACEFUL", "SEASONAL"
)

// ─── Add Gem Screen ───────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGemScreen(
    onBack: () -> Unit,
    onPublished: () -> Unit = {},
    viewModel: AddGemViewModel = viewModel(
        factory = AddGemViewModel.Factory(SessionManager(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val addGemState by viewModel.state.collectAsState()

    // ── Derive loading/success from ViewModel state ───────────
    val isSubmitting = addGemState is AddGemState.Loading
    val showSuccess  = addGemState is AddGemState.Success

    // ── Form state ────────────────────────────────────────────
    var gemName                  by remember { mutableStateOf("") }
    var selectedCategory         by remember { mutableStateOf("") }
    var selectedProvince         by remember { mutableStateOf("") }
    var story                    by remember { mutableStateOf("") }
    var localDirections          by remember { mutableStateOf("") }
    var selectedTags             by remember { mutableStateOf(setOf<String>()) }
    var photoUris                by remember { mutableStateOf(listOf<Uri>()) }
    var provinceDropdownExpanded by remember { mutableStateOf(false) }
    var currentStep              by remember { mutableIntStateOf(0) }
    var selectedLat              by remember { mutableStateOf(11.5564) }
    var selectedLng              by remember { mutableStateOf(104.9282) }

    // ── Validation ────────────────────────────────────────────
    val isStep1Valid = gemName.isNotBlank() && selectedCategory.isNotEmpty() && selectedProvince.isNotEmpty()
    val isStep2Valid = story.length >= 20
    val canPublish   = isStep1Valid && isStep2Valid

    // ── Launchers ─────────────────────────────────────────────
    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val remaining = 5 - photoUris.size
        photoUris = photoUris + uris.take(remaining)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getBestLastKnownLocation(context)?.let { location ->
                selectedLat = location.latitude
                selectedLng = location.longitude
            }
        }
    }

    // ── Snackbar for errors ───────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(addGemState) {
        if (addGemState is AddGemState.Error) {
            snackbarHostState.showSnackbar(
                message = (addGemState as AddGemState.Error).message,
                duration = SnackbarDuration.Short
            )
            viewModel.resetState()
        }
    }

    // ── Success screen ────────────────────────────────────────
    if (showSuccess) {
        GemPublishedSuccess(gemName = gemName, onDone = onPublished)
        return
    }

    // ── Submit helper (called from both Publish buttons) ──────
    val onSubmit = {
        viewModel.submitPin(
            title        = gemName,
            story        = story,
            address      = localDirections.ifBlank { selectedProvince },
            provinceName = selectedProvince,
            categoryName = selectedCategory,
            lat          = selectedLat,
            lng          = selectedLng,
            photoUris    = photoUris,   // ✅ pass selected photos
            context      = context      // ✅ pass context for URI reading
        )
    }

    Scaffold(
        snackbarHost    = { SnackbarHost(snackbarHostState) },
        containerColor  = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A1020), Color(0xFF15112E), Color(0xFF0A1020))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.statusBarsPadding())

                // ── TOP BAR ───────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(FieldBg, CircleShape)
                            .border(1.dp, FieldBorder, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint     = InkText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "Drop a Gem ✦",
                            style = TextStyle(
                                fontFamily = Cinzel,
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color      = InkText
                            )
                        )
                        Text(
                            text  = "Share a hidden place with Cambodia",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize   = 9.sp,
                                color      = MutedText
                            )
                        )
                    }

                    // Top-bar Publish button
                    Box(
                        modifier = Modifier
                            .background(
                                if (canPublish) LimePop else LimePop.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = canPublish && !isSubmitting) { onSubmit() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier  = Modifier.size(14.dp),
                                color     = Color(0xFF17122A),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text  = "Publish",
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color      = Color(0xFF17122A)
                                )
                            )
                        }
                    }
                }

                // ── STEP INDICATOR ────────────────────────────
                StepIndicator(currentStep = currentStep, totalSteps = 4)

                Spacer(modifier = Modifier.height(16.dp))

                // ── PHOTO UPLOAD ──────────────────────────────
                PhotoUploadSection(
                    photoUris    = photoUris,
                    onAddPhotos  = { photoPickerLauncher.launch("image/*") },
                    onRemovePhoto = { uri -> photoUris = photoUris.filter { it != uri } }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── FORM FIELDS ───────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {

                    // Gem Name
                    GemFormField(label = "GEM NAME") {
                        OutlinedTextField(
                            value         = gemName,
                            onValueChange = {
                                if (it.length <= 80) gemName = it
                                currentStep = if (gemName.isNotBlank()) 1 else 0
                            },
                            placeholder = {
                                Text(
                                    "e.g. Baphnom Market, Wat Phnom Kulen...",
                                    style = TextStyle(fontSize = 12.sp, color = MutedText.copy(alpha = 0.5f))
                                )
                            },
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(14.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor     = ElectricBlue,
                                unfocusedBorderColor   = FieldBorder,
                                focusedTextColor       = InkText,
                                unfocusedTextColor     = InkText,
                                cursorColor            = ElectricBlue,
                                focusedContainerColor  = FieldBg,
                                unfocusedContainerColor = FieldBg
                            ),
                            singleLine    = true,
                            supportingText = {
                                Text(
                                    "${gemName.length}/80",
                                    style = TextStyle(fontSize = 9.sp, color = MutedText.copy(alpha = 0.4f))
                                )
                            }
                        )
                    }

                    // Category chips
                    GemFormField(label = "CATEGORY") {
                        Row(
                            modifier            = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            gemCategories.forEach { category ->
                                CategoryChip(
                                    text       = category,
                                    isSelected = selectedCategory == category,
                                    onClick    = {
                                        selectedCategory = category
                                        currentStep = if (selectedProvince.isNotEmpty()) 2 else 1
                                    }
                                )
                            }
                        }
                    }

                    // Province dropdown
                    GemFormField(label = "PROVINCE") {
                        ExposedDropdownMenuBox(
                            expanded        = provinceDropdownExpanded,
                            onExpandedChange = { provinceDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value         = selectedProvince.ifEmpty { "Select your province" },
                                onValueChange = {},
                                readOnly      = true,
                                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                                shape         = RoundedCornerShape(14.dp),
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor      = ElectricBlue,
                                    unfocusedBorderColor    = FieldBorder,
                                    focusedTextColor        = if (selectedProvince.isEmpty()) MutedText.copy(alpha = 0.5f) else InkText,
                                    unfocusedTextColor      = if (selectedProvince.isEmpty()) MutedText.copy(alpha = 0.5f) else InkText,
                                    focusedContainerColor   = FieldBg,
                                    unfocusedContainerColor = FieldBg
                                ),
                                trailingIcon  = {
                                    Icon(Icons.Default.ArrowDropDown, null, tint = MutedText)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded        = provinceDropdownExpanded,
                                onDismissRequest = { provinceDropdownExpanded = false },
                                modifier        = Modifier.background(Color(0xFF1B1A3D))
                            ) {
                                cambodianProvinces.forEach { province ->
                                    DropdownMenuItem(
                                        text    = {
                                            Text(
                                                province,
                                                style = TextStyle(
                                                    fontSize = 13.sp,
                                                    color    = if (province == selectedProvince) LimePop else InkText
                                                )
                                            )
                                        },
                                        onClick = {
                                            selectedProvince         = province
                                            provinceDropdownExpanded = false
                                            currentStep = if (selectedCategory.isNotEmpty()) 2 else 1
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, ElectricBlue.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                    )

                    // Story
                    GemFormField(
                        label    = "THE STORY",
                        sublabel = "What makes this place special? Min 20 characters."
                    ) {
                        OutlinedTextField(
                            value         = story,
                            onValueChange = {
                                story = it
                                if (it.length >= 20) currentStep = maxOf(currentStep, 3)
                            },
                            placeholder = {
                                Text(
                                    "This place has been here since my grandmother's time...",
                                    style = TextStyle(fontSize = 12.sp, fontStyle = FontStyle.Italic, color = MutedText.copy(alpha = 0.4f))
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor      = ElectricBlue,
                                unfocusedBorderColor    = FieldBorder,
                                focusedTextColor        = InkText,
                                unfocusedTextColor      = InkText,
                                cursorColor             = ElectricBlue,
                                focusedContainerColor   = FieldBg,
                                unfocusedContainerColor = FieldBg
                            ),
                            supportingText = {
                                Text(
                                    "${story.length} chars ${if (story.length < 20) "· need ${20 - story.length} more" else "✓"}",
                                    style = TextStyle(
                                        fontSize = 9.sp,
                                        color    = if (story.length >= 20) LimePop else MutedText.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        )
                    }

                    // Location
                    GemFormField(label = "LOCATION") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FieldBg, RoundedCornerShape(14.dp))
                                .border(1.dp, FieldBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(
                                        text  = if (selectedProvince.isEmpty()) "Tap to use your location" else "$selectedProvince Province",
                                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = InkText)
                                    )
                                    Text(
                                        text  = "GPS coordinates will be captured",
                                        style = TextStyle(fontSize = 9.sp, color = MutedText.copy(alpha = 0.6f))
                                    )
                                }
                            }
                            Text(
                                text  = "Use GPS",
                                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ElectricBlue),
                                modifier = Modifier.clickable {
                                    val perm = Manifest.permission.ACCESS_FINE_LOCATION
                                    if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
                                        getBestLastKnownLocation(context)?.let { location ->
                                            selectedLat = location.latitude
                                            selectedLng = location.longitude
                                        }
                                    } else {
                                        locationPermissionLauncher.launch(perm)
                                    }
                                }
                            )
                        }
                    }

                    // Local directions
                    GemFormField(
                        label    = "LOCAL DIRECTIONS",
                        sublabel = "Optional — how would a local describe getting here?"
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ElectricBlue.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
                                .border(1.dp, ElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .fillMaxHeight()
                                        .background(ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                )
                                OutlinedTextField(
                                    value         = localDirections,
                                    onValueChange = { localDirections = it },
                                    placeholder = {
                                        Text(
                                            "e.g. Turn left at the big mango tree, ask for Ta Chan's stall...",
                                            style = TextStyle(fontSize = 11.sp, fontStyle = FontStyle.Italic, color = MutedText.copy(alpha = 0.4f))
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(90.dp),
                                    colors   = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor      = Color.Transparent,
                                        unfocusedBorderColor    = Color.Transparent,
                                        focusedTextColor        = InkText,
                                        unfocusedTextColor      = InkText,
                                        cursorColor             = ElectricBlue,
                                        focusedContainerColor   = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }

                    // Tags
                    GemFormField(label = "TAGS", sublabel = "Select all that apply") {
                        Row(
                            modifier            = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                TagChip(
                                    text       = tag,
                                    isSelected = isSelected,
                                    onClick    = {
                                        selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── BOTTOM SUBMIT BUTTON ──────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (canPublish)
                                    Brush.horizontalGradient(listOf(ElectricBlue, Bubblegum))
                                else
                                    Brush.horizontalGradient(listOf(ElectricBlue.copy(alpha = 0.3f), Bubblegum.copy(alpha = 0.3f)))
                            )
                            .clickable(enabled = canPublish && !isSubmitting) { onSubmit() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(22.dp),
                                color       = Color(0xFF0A1020),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text  = "✦ Share This Gem with Cambodia",
                                style = TextStyle(
                                    fontFamily  = Cinzel,
                                    fontSize    = 12.sp,
                                    fontWeight  = FontWeight.Bold,
                                    color       = if (canPublish) Color(0xFF0A1020) else MutedText,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }

                    // Validation hint
                    if (!canPublish) {
                        Text(
                            text = buildString {
                                val missing = mutableListOf<String>()
                                if (gemName.isBlank()) missing.add("gem name")
                                if (selectedCategory.isEmpty()) missing.add("category")
                                if (selectedProvince.isEmpty()) missing.add("province")
                                if (story.length < 20) missing.add("story (min 20 chars)")
                                append("Still needed: ${missing.joinToString(" · ")}")
                            },
                            style    = TextStyle(fontSize = 10.sp, color = MutedText.copy(alpha = 0.5f), textAlign = TextAlign.Center),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.navigationBarsPadding())
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ─── Step Indicator ───────────────────────────────────────────
@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(totalSteps) { index ->
            val color by animateColorAsState(
                targetValue = when {
                    index < currentStep  -> LimePop
                    index == currentStep -> ElectricBlue
                    else                 -> Color(0x1AFFFFFF)
                },
                animationSpec = tween(300),
                label = "step_color"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

// ─── Photo Upload Section ─────────────────────────────────────
@Composable
private fun PhotoUploadSection(
    photoUris: List<Uri>,
    onAddPhotos: () -> Unit,
    onRemovePhoto: (Uri) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        if (photoUris.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ElectricBlue.copy(alpha = 0.04f))
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(ElectricBlue.copy(alpha = 0.4f), Bubblegum.copy(alpha = 0.4f))),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { onAddPhotos() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = ElectricBlue, modifier = Modifier.size(32.dp))
                    Text("Add Photos", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ElectricBlue))
                    Text("Show the world what makes this place special", style = TextStyle(fontSize = 10.sp, color = MutedText.copy(alpha = 0.6f), textAlign = TextAlign.Center))
                    Text("0 / 5 photos", style = TextStyle(fontSize = 9.sp, color = MutedText.copy(alpha = 0.3f)))
                }
            }
        } else {
            Row(
                modifier              = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                photoUris.forEach { uri ->
                    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(14.dp))) {
                        AsyncImage(model = uri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(20.dp)
                                .background(Color(0xCC0A1020), CircleShape)
                                .clickable { onRemovePhoto(uri) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, "Remove", tint = InkText, modifier = Modifier.size(12.dp))
                        }
                    }
                }
                if (photoUris.size < 5) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElectricBlue.copy(alpha = 0.08f))
                            .border(1.dp, ElectricBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .clickable { onAddPhotos() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CameraAlt, null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
                            Text("${photoUris.size}/5", style = TextStyle(fontSize = 9.sp, color = ElectricBlue, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

// ─── Form Field Wrapper ───────────────────────────────────────
@Composable
private fun GemFormField(label: String, sublabel: String = "", content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp, color = ElectricBlue))
            if (sublabel.isNotBlank()) Text(sublabel, style = TextStyle(fontSize = 9.sp, color = MutedText.copy(alpha = 0.4f)))
        }
        content()
    }
}

// ─── Category Chip ────────────────────────────────────────────
@Composable
private fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor     by animateColorAsState(if (isSelected) LimePop.copy(alpha = 0.2f) else FieldBg, label = "chip_bg")
    val borderColor by animateColorAsState(if (isSelected) LimePop else FieldBorder, label = "chip_border")
    val textColor   by animateColorAsState(if (isSelected) LimePop else MutedText, label = "chip_text")
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textColor))
    }
}

// ─── Tag Chip ─────────────────────────────────────────────────
@Composable
private fun TagChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor     by animateColorAsState(if (isSelected) Bubblegum.copy(alpha = 0.15f) else FieldBg, label = "tag_bg")
    val borderColor by animateColorAsState(if (isSelected) Bubblegum else FieldBorder, label = "tag_border")
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            style = TextStyle(
                fontFamily    = FontFamily.SansSerif,
                fontSize      = 9.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                color         = if (isSelected) Bubblegum else MutedText.copy(alpha = 0.6f)
            )
        )
    }
}

// ─── Success Screen ───────────────────────────────────────────
@Composable
private fun GemPublishedSuccess(gemName: String, onDone: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A1020), Color(0xFF15112E), Color(0xFF0A1020)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.padding(40.dp)
        ) {
            Text("✦", fontSize = 56.sp, color = LimePop)
            Text("Gem Dropped!", style = TextStyle(fontFamily = Cinzel, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = InkText))
            Text(
                "\"$gemName\" is now part of Cambodia's living archive. Thank you for preserving what matters.",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, color = MutedText, lineHeight = 22.sp, textAlign = TextAlign.Center)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.horizontalGradient(listOf(ElectricBlue, Bubblegum)))
                    .clickable { onDone() },
                contentAlignment = Alignment.Center
            ) {
                Text("Back to Home", style = TextStyle(fontFamily = Cinzel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1020), letterSpacing = 0.5.sp))
            }
        }
    }
}

private fun getBestLastKnownLocation(context: Context): Location? {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!fineGranted && !coarseGranted) return null

    return try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .mapNotNull { provider ->
                runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            }
            .maxByOrNull { it.time }
    } catch (e: Exception) {
        null
    }
}
