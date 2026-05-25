package com.example.neakta.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.core.AppLanguage
import com.example.neakta.ui.core.LanguagePreference
import com.example.neakta.ui.core.LocalAppLanguage
import com.example.neakta.ui.profile.ProfileState
import com.example.neakta.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch

private val InkText    = Color(0xFFF7FAFC)
private val MutedText  = Color(0xFFB8C2CC)
private val NightBase  = Color(0xFF0D1117)
private val CardBg     = Color(0xE61A202C)
private val GlassPanel = Color(0x991A202C)
private val Outline    = Color(0x26FFFFFF)
private val Primary    = Color(0xFF5FD3A6)
private val Danger     = Color(0xFFFF6B6B)

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    // FIX: ViewModel is now passed in — no default creation here.
    // Both SettingsScreen and ProfileScreen must share the same instance
    // by receiving it from the NavHost or parent composable.
    viewModel: ProfileViewModel
) {
    val languageState = LocalAppLanguage.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isKhmer = languageState.current == AppLanguage.KHMER

    val profileState by viewModel.state.collectAsState()
    val isUpdating by viewModel.isUpdatingProfile.collectAsState()


    var profilePublic        by remember { mutableStateOf(true) }
    var showLanguageDialog   by remember { mutableStateOf(false) }
    var showLogoutDialog     by remember { mutableStateOf(false) }
    var showAboutDialog      by remember { mutableStateOf(false) }
    var showHelpDialog       by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // ── Choice Dialog ───────────────────────────────────────
    if (showLanguageDialog) {
        ChoiceDialog(
            title = if (isKhmer) "ជ្រើសភាសា" else "Choose Language",
            options = listOf(AppLanguage.ENGLISH.displayName, AppLanguage.KHMER.displayName),
            selectedOption = languageState.current.displayName,
            onOptionSelected = { selected ->
                val newLang = AppLanguage.entries.first { it.displayName == selected }
                languageState.current = newLang
                scope.launch {
                    LanguagePreference.saveLanguage(context, newLang)
                }
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // ── Edit Profile Dialog ───────────────────────────────────
    if (showEditProfileDialog) {
        val user = (profileState as? ProfileState.Success)?.user
        if (user != null) {
            EditProfileDialog(
                currentName = user.displayName ?: user.username,
                currentAvatar = user.avatarUrl,
                isKhmer = isKhmer,
                isUpdating = isUpdating,
                onSaveName = { newName -> viewModel.updateProfile(newName) },
                onUploadAvatar = { uri -> viewModel.uploadAvatar(context, uri) },
                onDismiss = { showEditProfileDialog = false }
            )
        }
    }

    // ── Logout dialog ─────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor   = Color(0xFF111827),
            titleContentColor = InkText,
            title = {
                Text(
                    text  = if (isKhmer) "ចាកចេញ?" else "Log Out?",
                    style = TextStyle(fontFamily = Cinzel, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkText)
                )
            },
            text = {
                Text(
                    text  = if (isKhmer) "តើអ្នកប្រាកដថាចង់ចាកចេញពីឧបករណ៍នេះទេ?"
                    else "Are you sure you want to sign out from this device?",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = MutedText)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text(
                        text  = if (isKhmer) "ចាកចេញ" else "Log Out",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = Danger)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text  = if (isKhmer) "បោះបង់" else "Cancel",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = Primary)
                    )
                }
            }
        )
    }

    // ── About dialog ──────────────────────────────────────────────
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor   = Color(0xFF111827),
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text  = if (isKhmer) "អំពី NeakTa" else "About NeakTa",
                    style = TextStyle(fontFamily = Cinzel, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkText)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AboutItem(label = if (isKhmer) "កំណែ" else "Version", value = "1.0.0")
                    AboutItem(label = if (isKhmer) "បង្កើតដោយ" else "Built by", value = "Team NeakTa")
                    AboutItem(label = if (isKhmer) "បច្ចេកវិទ្យា" else "Stack", value = "Kotlin · Spring Boot · MySQL")
                    AboutItem(
                        label = if (isKhmer) "គោលបំណង" else "Mission",
                        value = if (isKhmer) "រក្សាទុកកន្លែងវប្បធម៌លាក់នៅកម្ពុជា មុនពេលវាបាត់" else "Preserve Cambodia's hidden cultural places before they disappear"
                    )
                    Text(
                        text  = if (isKhmer) "NeakTa — ដាក់ឈ្មោះតាមព្រលឹងអ្នកការពារខ្មែរ — គឺជាកម្មវិធីទូរស័ព្ទដែលផ្តល់អំណាចដល់សហគមន៍ ដើម្បីស្វែងរក ចែករំលែក និងអភិរក្សសម្បត្តិវប្បធម៌លាក់របស់កម្ពុជា។"
                        else "NeakTa — named after the Khmer guardian spirits — is a community-powered app to discover, share, and preserve Cambodia's hidden cultural gems.",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, lineHeight = 18.sp, color = MutedText)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(if (isKhmer) "បិទ" else "Close", style = TextStyle(fontWeight = FontWeight.Bold, color = Primary))
                }
            }
        )
    }

    // ── Help dialog ───────────────────────────────────────────────
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor   = Color(0xFF111827),
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text  = if (isKhmer) "ជំនួយ" else "Help",
                    style = TextStyle(fontFamily = Cinzel, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkText)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    HelpItem(emoji = "📍", title = if (isKhmer) "របៀបបន្ថែម Gem" else "How to add a Gem", body = if (isKhmer) "ចុចប៊ូតុង + នៅខាងក្រោម បំពេញឈ្មោះ ប្រភេទ ខេត្ត និងរឿងរ៉ាវ រួចចុច Publish។" else "Tap the + button at the bottom, fill in the name, category, province, and story, then tap Publish.")
                    HelpItem(emoji = "⬆️", title = if (isKhmer) "ការបោះឆ្នោត" else "Voting", body = if (isKhmer) "ចុច 'I found this spot' ដើម្បី upvote gem។ ចុច 'This place still exists' ដើម្បីបញ្ជាក់ថាកន្លែងនោះនៅតែមាន។" else "Tap 'I found this spot' to upvote a gem. Tap 'This place still exists' to confirm it's still there.")
                    HelpItem(emoji = "🏆", title = if (isKhmer) "ចំណាត់ថ្នាក់ខេត្ត" else "Province Rankings", body = if (isKhmer) "ខេត្តត្រូវបានដាក់ចំណាត់ថ្នាក់តាម gems ដែលបានបន្ថែម និង upvotes ដែលទទួលបាន។ ជួយខេត្តរបស់អ្នកឡើងថ្នាក់!" else "Provinces are ranked by gems added and upvotes received. Help your province climb the leaderboard!")
                    HelpItem(emoji = "🚩", title = if (isKhmer) "ការរាយការណ៍" else "Flagging", body = if (isKhmer) "ប្រសិនបើ gem ហួសសម័យ ឬខុស ចុច 'Flag as outdated' នៅក្នុងទំព័រព័ត៌មានលម្អិត។" else "If a gem is outdated or incorrect, tap 'Flag as outdated' on the detail screen.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(if (isKhmer) "យល់ហើយ" else "Got it", style = TextStyle(fontWeight = FontWeight.Bold, color = Primary))
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF111827), NightBase)))
    ) {
        // Header glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Brush.linearGradient(listOf(Primary.copy(alpha = 0.18f), Primary.copy(alpha = 0.05f), Color.Transparent)))
        )

        LazyColumn(
            modifier        = Modifier.fillMaxSize(),
            contentPadding  = PaddingValues(bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { SettingsHeader(onBack = onBack, isKhmer = isKhmer) }

            // ── Preferences Section ──────────────────────────
            item {
                SettingsSection(title = if (isKhmer) "ចំណូលចិត្ត" else "Preferences") {
                    SettingRow(
                        icon     = Icons.Default.Language,
                        title    = if (isKhmer) "ភាសា" else "Language",
                        subtitle = if (isKhmer) "ជ្រើសរើសរវាងភាសាខ្មែរ និងអង់គ្លេស" else "Switch between Khmer and English",
                        value    = languageState.current.displayName,
                        onClick  = { showLanguageDialog = true }
                    )
                }
            }

            // ── Account Section ─────────────────────────────
            item {
                SettingsSection(title = if (isKhmer) "គណនី" else "Account") {
                    SettingRow(
                        icon     = Icons.Default.Edit,
                        title    = if (isKhmer) "កែប្រែប្រវត្តិរូប" else "Edit Profile",
                        subtitle = if (isKhmer) "ធ្វើបច្ចុប្បន្នភាពឈ្មោះ ខេត្ត និងរូបថត" else "Update your name, province, and photo",
                        value    = "",
                        onClick  = { showEditProfileDialog = true }
                    )
                    SwitchRow(
                        icon            = Icons.Default.Lock,
                        title           = if (isKhmer) "ប្រវត្តិរូបសាធារណៈ" else "Public Profile",
                        subtitle        = if (isKhmer) "អនុញ្ញាតឱ្យអ្នកប្រើផ្សេងទៀតមើល gems របស់អ្នក" else "Let other users see your gems and rank",
                        checked         = profilePublic,
                        onCheckedChange = { profilePublic = it }
                    )
                    SettingRow(
                        icon     = Icons.AutoMirrored.Filled.Logout,
                        title    = if (isKhmer) "ចាកចេញ" else "Logout",
                        subtitle = if (isKhmer) "ចាកចេញពីឧបករណ៍នេះ" else "Sign out from this device",
                        value    = "",
                        danger   = true,
                        onClick  = { showLogoutDialog = true }
                    )
                }
            }

            // ── Support Section ─────────────────────────────
            item {
                SettingsSection(title = if (isKhmer) "ជំនួយ និងព័ត៌មាន" else "Support & Info") {
                    SettingRow(
                        icon     = Icons.AutoMirrored.Filled.Help,
                        title    = if (isKhmer) "ជំនួយ" else "Help",
                        subtitle = if (isKhmer) "របៀបប្រើប្រាស់កម្មវិធី និងសំណួរដែលសួរញឹកញាប់" else "How to use the app and FAQs",
                        value    = "",
                        onClick  = { showHelpDialog = true }
                    )
                    SettingRow(
                        icon     = Icons.Default.Info,
                        title    = if (isKhmer) "អំពី NeakTa" else "About NeakTa",
                        subtitle = if (isKhmer) "ព័ត៌មានគម្រោង និងគោលបំណង" else "Project info and mission",
                        value    = "1.0.0",
                        onClick  = { showAboutDialog = true }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentAvatar: String?,
    isKhmer: Boolean,
    isUpdating: Boolean,
    onSaveName: (String) -> Unit,
    onUploadAvatar: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    // FIX: Track a local preview URI so the avatar updates immediately
    // after picking, without waiting for the network round-trip.
    var previewUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            previewUri = it        // Show new image instantly in the dialog
            onUploadAvatar(it)     // Kick off the upload in the background
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111827),
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = if (isKhmer) "កែប្រែប្រវត្តិរូប" else "Edit Profile",
                style = TextStyle(fontFamily = Cinzel, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkText)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Avatar Edit
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    AsyncImage(
                        // FIX: Show the locally-picked URI first; fall back to the
                        // server URL only when no local preview exists yet.
                        model = previewUri ?: currentAvatar,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, Primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    if (isUpdating) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Primary,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = NightBase, modifier = Modifier.size(16.dp))
                    }
                }

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isKhmer) "ឈ្មោះបង្ហាញ" else "Display Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Outline,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText,
                        cursorColor = Primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // FIX: Save name then dismiss. Because SettingsScreen and
                    // ProfileScreen now share the same ViewModel instance, the
                    // fetchProfile() call inside updateProfile() will update
                    // ProfileScreen automatically via the shared StateFlow.
                    onSaveName(name)
                    onDismiss()
                },
                enabled = !isUpdating && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(if (isKhmer) "រក្សាទុក" else "Save", color = NightBase, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isKhmer) "បោះបង់" else "Cancel", color = MutedText)
            }
        }
    )
}

@Composable
private fun SettingsHeader(onBack: () -> Unit, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = GlassPanel, border = androidx.compose.foundation.BorderStroke(1.dp, Outline)) {
            IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = InkText, modifier = Modifier.size(20.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text  = if (isKhmer) "ការកំណត់" else "SETTINGS",
                style = TextStyle(fontFamily = Cinzel, fontSize = 24.sp, letterSpacing = 5.sp, color = InkText)
            )
            Text(
                text  = if (isKhmer) "កំណត់បទពិសោធន៍ NeakTa របស់អ្នក" else "Customize your NeakTa experience",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MutedText)
            )
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = title, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Primary))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBg)
                .border(1.dp, Outline, RoundedCornerShape(24.dp))
                .padding(vertical = 6.dp),
            content = content
        )
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        SettingsIcon(icon = icon, danger = danger)
        SettingsText(title = title, subtitle = subtitle, modifier = Modifier.weight(1f), danger = danger)
        if (value.isNotBlank()) {
            Text(
                text  = value,
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Icon(Icons.Default.ChevronRight, null, tint = MutedText.copy(alpha = 0.55f), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        SettingsIcon(icon = icon)
        SettingsText(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = InkText,
                checkedTrackColor   = Primary,
                uncheckedThumbColor = MutedText,
                uncheckedTrackColor = Color(0xFF28313D)
            )
        )
    }
}

@Composable
private fun SettingsIcon(icon: ImageVector, danger: Boolean = false) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (danger) Danger.copy(alpha = 0.12f) else Primary.copy(alpha = 0.12f))
            .border(1.dp, if (danger) Danger.copy(alpha = 0.35f) else Primary.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = if (danger) Danger else Primary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsText(title: String, subtitle: String, modifier: Modifier = Modifier, danger: Boolean = false) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text  = title,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (danger) Danger else InkText)
        )
        Text(
            text     = subtitle,
            style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, lineHeight = 15.sp, color = MutedText),
            maxLines = 2, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ChoiceDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest  = onDismiss,
        containerColor    = Color(0xFF111827),
        titleContentColor = InkText,
        title = {
            Text(text = title, style = TextStyle(fontFamily = Cinzel, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkText))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 8.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick  = { onOptionSelected(option) },
                            colors   = RadioButtonDefaults.colors(selectedColor = Primary)
                        )
                        Text(text = option, style = TextStyle(fontWeight = FontWeight.Bold, color = InkText))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (title == "ជ្រើសភាសា") "បោះបង់" else "Cancel",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = Primary)
                )
            }
        }
    )
}

@Composable
private fun AboutItem(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Top
    ) {
        Text(
            text  = label,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText)
        )
        Text(
            text     = value,
            style    = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkText),
            maxLines = 3,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun HelpItem(emoji: String, title: String, body: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text  = title,
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Black, color = InkText)
            )
            Text(
                text  = body,
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, lineHeight = 17.sp, color = MutedText)
            )
        }
    }
}