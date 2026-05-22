package com.example.neakta.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.neakta.data.SessionManager
import com.example.neakta.model.PinResponse
import com.example.neakta.model.UserResponse
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.core.AppLanguage
import com.example.neakta.ui.core.LocalAppLanguage

// ── Palette (unchanged) ──────────────────────────────────────────────────────
private val InkText    = Color(0xFFF7FAFC)
private val MutedText  = Color(0xFFB8C2CC)
private val NightBase  = Color(0xFF0D1117)
private val CardBg     = Color(0xE61A202C)
private val CardBgSoft = Color(0xCC111827)
private val Outline    = Color(0x26FFFFFF)
private val Primary    = Color(0xFF5FD3A6)
private val GlassPanel = Color(0x991A202C)
private val DangerRed  = Color(0xFFEF4444)

// ── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(SessionManager(LocalContext.current))
    )
) {
    val state       by viewModel.state.collectAsState()
    val editingPin  by viewModel.editingPin.collectAsState()
    val deletingId  by viewModel.deletingPinId.collectAsState()
    val opError     by viewModel.opError.collectAsState()

    val languageState = LocalAppLanguage.current
    val isKhmer       = languageState.current == AppLanguage.KHMER

    val snackbarHostState = remember { SnackbarHostState() }

    // Show op errors in a snackbar
    LaunchedEffect(opError) {
        if (opError != null) {
            snackbarHostState.showSnackbar(opError!!)
            viewModel.clearOpError()
        }
    }

    // Delete confirmation: which pin is pending confirmation
    var pendingDeletePin by remember { mutableStateOf<PinResponse?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0D1117), Color(0xFF111827), Color(0xFF0D1117), NightBase)
                    )
                )
        ) {
            // Header glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Primary.copy(alpha = 0.18f),
                                Primary.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(modifier = Modifier.fillMaxSize()) {
                ProfileTopBar(onNavigateToSettings = onNavigateToSettings, isKhmer = isKhmer)

                when (val s = state) {
                    is ProfileState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Primary)
                        }
                    }

                    is ProfileState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(s.message, style = TextStyle(color = MutedText, fontSize = 14.sp))
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { viewModel.fetchProfile() }) {
                                    Text(if (isKhmer) "ព្យាយាមម្ដងទៀត" else "Retry")
                                }
                            }
                        }
                    }

                    is ProfileState.Success -> {
                        LazyColumn(
                            modifier            = Modifier.weight(1f),
                            contentPadding      = PaddingValues(bottom = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item { ProfileSummaryCard(user = s.user, isKhmer = isKhmer) }
                            item { ProfileStatsRow(user = s.user, isKhmer = isKhmer) }
                            item { MyGemsHeader(count = s.pins.size, isKhmer = isKhmer) }

                            if (s.pins.isEmpty()) {
                                item { EmptyGemsState(isKhmer = isKhmer) }
                            } else {
                                items(s.pins, key = { it.id }) { pin ->
                                    GemRow(
                                        pin        = pin,
                                        isKhmer    = isKhmer,
                                        isDeleting = deletingId == pin.id,
                                                onEdit     = { viewModel.startEditing(pin) },
                                        onDelete   = { pendingDeletePin = pin }
                                    )
                                }
                            }

                            item {
                                Spacer(
                                    Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Delete confirmation dialog ────────────────────────────────────────────
    pendingDeletePin?.let { pin ->
        DeleteConfirmDialog(
            pinTitle = pin.title,
            isKhmer  = isKhmer,
            onConfirm = {
                viewModel.deletePin(pin.id)
                pendingDeletePin = null
            },
            onDismiss = { pendingDeletePin = null }
        )
    }

    // ── Edit bottom sheet ─────────────────────────────────────────────────────
    editingPin?.let { pin ->
        EditPinSheet(
            pin      = pin,
            isKhmer  = isKhmer,
            onSave   = { title, desc -> viewModel.saveEdit(pin.id, title, desc) },
            onDismiss = { viewModel.cancelEditing() }
        )
    }
}

// ── Delete dialog ─────────────────────────────────────────────────────────────
@Composable
private fun DeleteConfirmDialog(
    pinTitle: String,
    isKhmer: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color(0xFF1A202C),
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text(
                text  = if (isKhmer) "លុប Gem?" else "Delete Gem?",
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold, color = InkText)
            )
        },
        text = {
            Text(
                text  = if (isKhmer)
                    "\"$pinTitle\" នឹងត្រូវបានលុបចោលជាអចិន្ត្រៃ។"
                else
                    "\"$pinTitle\" will be permanently removed.",
                style = TextStyle(fontSize = 13.sp, color = MutedText)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text  = if (isKhmer) "លុប" else "Delete",
                    style = TextStyle(color = DangerRed, fontWeight = FontWeight.Bold)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text  = if (isKhmer) "បោះបង់" else "Cancel",
                    style = TextStyle(color = MutedText)
                )
            }
        }
    )
}

// ── Edit bottom sheet ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPinSheet(
    pin: PinResponse,
    isKhmer: Boolean,
    onSave: (title: String, story: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember(pin.id) { mutableStateOf(pin.title) }
    var story by remember(pin.id) { mutableStateOf(pin.story) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = Color(0xFF1A202C),
        dragHandle        = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Outline)
            )
        }
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text  = if (isKhmer) "កែប្រែ Gem" else "Edit Gem",
                style = TextStyle(
                    fontFamily   = FontFamily.SansSerif,
                    fontSize     = 18.sp,
                    fontWeight   = FontWeight.Black,
                    color        = InkText
                )
            )

            // Title field
            OutlinedTextField(
                value         = title,
                onValueChange = { title = it },
                label         = {
                    Text(
                        if (isKhmer) "ចំណងជើង" else "Title",
                        style = TextStyle(color = MutedText, fontSize = 12.sp)
                    )
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    unfocusedBorderColor = Outline,
                    focusedTextColor     = InkText,
                    unfocusedTextColor   = InkText,
                    cursorColor          = Primary,
                    focusedLabelColor    = Primary,
                    unfocusedLabelColor  = MutedText
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // Description field
            OutlinedTextField(
                value         = story,
                onValueChange = { story = it },
                label = {
                    Text(
                        if (isKhmer) "រឿងរ៉ាវ" else "Story",
                        style = TextStyle(color = MutedText, fontSize = 12.sp)
                    )
                },
                minLines      = 3,
                maxLines      = 5,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    unfocusedBorderColor = Outline,
                    focusedTextColor     = InkText,
                    unfocusedTextColor   = InkText,
                    cursorColor          = Primary,
                    focusedLabelColor    = Primary,
                    unfocusedLabelColor  = MutedText
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(14.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, Outline)
                ) {
                    Text(
                        if (isKhmer) "បោះបង់" else "Cancel",
                        color = MutedText
                    )
                }
                Button(
                    onClick  = { onSave(title, story) },
                    enabled  = title.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(
                        if (isKhmer) "រក្សាទុក" else "Save",
                        color = NightBase,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── GemRow (with edit/delete) ─────────────────────────────────────────────────
@Composable
private fun GemRow(
    pin: PinResponse,
    isKhmer: Boolean,
    isDeleting: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBgSoft)
            .border(1.dp, Outline, RoundedCornerShape(22.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Thumbnail / icon
        Box(
            modifier = Modifier
                .width(88.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Primary.copy(alpha = 0.08f))
                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            val photoUrl = pin.imageUrl
            if (photoUrl != null) {
                AsyncImage(
                    model              = photoUrl,
                    contentDescription = null,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                Text(text = pin.categoryIcon ?: "📍", fontSize = 24.sp)
            }
        }

        // Info
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text     = pin.title,
                style    = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Black,
                    color      = InkText
                ),
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
            Text(
                text     = "${pin.provinceName ?: ""} · ${pin.categoryName ?: ""}",
                style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = MutedText),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text  = pin.createdAt?.take(10) ?: "",
                style = TextStyle(fontSize = 10.sp, color = MutedText.copy(alpha = 0.5f))
            )
        }

        // Upvote count
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, null, tint = Primary, modifier = Modifier.size(15.dp))
            Text(
                text  = pin.upvoteCount.toString(),
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Black, color = Primary)
            )
        }

        // Edit / Delete actions
        if (isDeleting) {
            CircularProgressIndicator(
                modifier  = Modifier.size(20.dp),
                color     = DangerRed,
                strokeWidth = 2.dp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Edit button
                IconButton(
                    onClick  = onEdit,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary.copy(alpha = 0.12f))
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = if (isKhmer) "កែប្រែ" else "Edit",
                        tint     = Primary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                // Delete button
                IconButton(
                    onClick  = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DangerRed.copy(alpha = 0.12f))
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = if (isKhmer) "លុប" else "Delete",
                        tint     = DangerRed,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

// ── Unchanged composables below ───────────────────────────────────────────────

@Composable
private fun ProfileTopBar(onNavigateToSettings: () -> Unit, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text  = if (isKhmer) "ប្រវត្តិរូប" else "PROFILE",
                style = TextStyle(
                    fontFamily    = Cinzel,
                    fontSize      = 24.sp,
                    letterSpacing = 5.sp,
                    color         = InkText
                )
            )
            Text(
                text  = if (isKhmer) "Gems និងកេរ្តិ៍ឈ្មោះដែលបានរក្សាទុករបស់អ្នក"
                else "Your saved gems and reputation",
                style = TextStyle(
                    fontFamily  = FontFamily.SansSerif,
                    fontSize    = 13.sp,
                    fontWeight  = FontWeight.Medium,
                    color       = MutedText
                )
            )
        }
        Surface(
            shape  = CircleShape,
            color  = GlassPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
        ) {
            IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = InkText, modifier = Modifier.size(19.dp))
            }
        }
    }
}

@Composable
private fun ProfileSummaryCard(user: UserResponse, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(CardBg, CardBgSoft)))
            .border(1.dp, Outline, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        AsyncImage(
            model              = user.avatarUrl,
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.size(72.dp).clip(CircleShape).border(1.dp, Primary, CircleShape)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = user.displayName?.takeIf { it.isNotBlank() } ?: user.username,
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = InkText)
                )
                Icon(Icons.Default.Star, null, tint = Primary, modifier = Modifier.size(15.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(13.dp))
                Text(
                    text     = "@${user.username}",
                    style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = MutedText),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            val level = when {
                user.totalPins >= 20 -> if (isKhmer) "អ្នកជំនាញ Gem កម្រិត ៥" else "Gem Master Lv.5"
                user.totalPins >= 10 -> if (isKhmer) "អ្នក狩猎 Gem កម្រិត ៤" else "Gem Hunter Lv.4"
                user.totalPins >= 5  -> if (isKhmer) "អ្នករុករក កម្រិត ៣" else "Explorer Lv.3"
                user.totalPins >= 2  -> if (isKhmer) "អ្នកស្វែងរក កម្រិត ២" else "Scout Lv.2"
                else                 -> if (isKhmer) "អ្នកថ្មី កម្រិត ១" else "Newcomer Lv.1"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Primary.copy(alpha = 0.16f))
                    .border(1.dp, Primary.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = level, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary))
            }
        }
    }
}

@Composable
private fun ProfileStatsRow(user: UserResponse, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(CardBg)
            .border(1.dp, Outline, RoundedCornerShape(24.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatCell(value = user.totalPins.toString(), label = if (isKhmer) "Gems" else "Gems", modifier = Modifier.weight(1f))
        VerticalStatDivider()
        StatCell(value = user.karmaPoints.toString(), label = if (isKhmer) "កាម៉ា" else "Karma", modifier = Modifier.weight(1f))
        VerticalStatDivider()
        val year = user.createdAt?.take(4) ?: "—"
        StatCell(value = year, label = if (isKhmer) "ចាប់ពី" else "Since", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(text = value, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp, fontWeight = FontWeight.Black, color = InkText))
        Text(text = label, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = MutedText))
    }
}

@Composable
private fun VerticalStatDivider() {
    Box(modifier = Modifier.width(1.dp).height(46.dp).background(Outline))
}

@Composable
private fun MyGemsHeader(count: Int, isKhmer: Boolean) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = if (isKhmer) "Gems របស់ខ្ញុំ" else "My Gems",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, fontWeight = FontWeight.Black, color = InkText)
        )
        Text(
            text  = if (isKhmer) "សរុប $count" else "$count total",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
        )
    }
}

@Composable
private fun EmptyGemsState(isKhmer: Boolean) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "💎", fontSize = 32.sp)
        Text(
            text  = if (isKhmer) "មិនទាន់មាន Gems នៅឡើយ" else "No gems yet",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkText)
        )
        Text(
            text  = if (isKhmer) "ចុះឈ្មោះ Gem លាក់ដំបូងរបស់អ្នក!" else "Drop your first hidden gem!",
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = MutedText)
        )
    }
}