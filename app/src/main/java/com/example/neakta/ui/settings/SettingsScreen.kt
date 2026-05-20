package com.example.neakta.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.core.AppLanguage
import com.example.neakta.ui.core.LocalAppLanguage

private val InkText = Color(0xFFF7FAFC)
private val MutedText = Color(0xFFB8C2CC)
private val NightBase = Color(0xFF0D1117)
private val CardBg = Color(0xE61A202C)
private val GlassPanel = Color(0x991A202C)
private val Outline = Color(0x26FFFFFF)
private val Primary = Color(0xFF5FD3A6)
private val Danger = Color(0xFFFF6B6B)

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // Read the app-wide language state
    val languageState = LocalAppLanguage.current
    val isKhmer = languageState.current == AppLanguage.KHMER

    // Local UI state
    var theme by remember { mutableStateOf("Dark") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var profilePublic by remember { mutableStateOf(true) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // — Language dialog
    if (showLanguageDialog) {
        ChoiceDialog(
            title = if (isKhmer) "ជ្រើសភាសា" else "Choose Language",
            options = listOf(AppLanguage.ENGLISH.displayName, AppLanguage.KHMER.displayName),
            selectedOption = languageState.current.displayName,
            onOptionSelected = { selected ->
                languageState.current = AppLanguage.entries.first { it.displayName == selected }
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // — Theme dialog
    if (showThemeDialog) {
        ChoiceDialog(
            title = if (isKhmer) "ជ្រើសរចនាប័ទ្ម" else "Choose Theme",
            options = listOf("Dark", "Light", "System Default"),
            selectedOption = theme,
            onOptionSelected = {
                theme = it
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // — Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color(0xFF111827),
            titleContentColor = InkText,
            title = {
                Text(
                    text = if (isKhmer) "ចាកចេញ?" else "Log Out?",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                )
            },
            text = {
                Text(
                    text = if (isKhmer) "តើអ្នកប្រាកដថាចង់ចាកចេញពីឧបករណ៍នេះទេ?"
                    else "Are you sure you want to sign out from this device?",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = MutedText
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text(
                        text = if (isKhmer) "ចាកចេញ" else "Log Out",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = Danger
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = if (isKhmer) "បោះបង់" else "Cancel",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1117), Color(0xFF111827), NightBase)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { SettingsHeader(onBack = onBack, isKhmer = isKhmer) }

            item {
                SettingsSection(title = if (isKhmer) "ចំណូលចិត្ត" else "Preferences") {
                    SettingRow(
                        icon = Icons.Default.Language,
                        title = if (isKhmer) "ភាសា" else "Language",
                        subtitle = if (isKhmer) "ប្តូររវាងភាសាអង់គ្លេស និងខ្មែរ"
                        else "Change app text between English and Khmer",
                        value = languageState.current.displayName,
                        onClick = { showLanguageDialog = true }
                    )
                    SettingRow(
                        icon = Icons.Default.DarkMode,
                        title = if (isKhmer) "រចនាប័ទ្ម" else "Theme",
                        subtitle = if (isKhmer) "ជ្រើសរូបរាង NeakTa" else "Choose how NeakTa looks",
                        value = theme,
                        onClick = { showThemeDialog = true }
                    )
                    SwitchRow(
                        icon = Icons.Default.Notifications,
                        title = if (isKhmer) "ការជូនដំណឹង" else "Notifications",
                        subtitle = if (isKhmer) "ទទួលការអាប់ដេតអំពី gems ដែលបានរក្សាទុក"
                        else "Receive updates about saved gems",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            }

            item {
                SettingsSection(title = if (isKhmer) "គណនី" else "Account") {
                    SettingRow(
                        icon = Icons.Default.Edit,
                        title = if (isKhmer) "កែប្រែប្រវត្តិរូប" else "Edit Profile",
                        subtitle = if (isKhmer) "ធ្វើបច្ចុប្បន្នភាពឈ្មោះ ខេត្ត និងរូបថត"
                        else "Update your name, province, and photo",
                        value = "",
                        onClick = {}
                    )
                    SwitchRow(
                        icon = Icons.Default.Lock,
                        title = if (isKhmer) "ប្រវត្តិរូបសាធារណៈ" else "Public Profile",
                        subtitle = if (isKhmer) "អនុញ្ញាតឱ្យអ្នកប្រើប្រាស់ផ្សេងទៀតមើល gems និងចំណាត់ថ្នាក់របស់អ្នក"
                        else "Let other users see your gems and rank",
                        checked = profilePublic,
                        onCheckedChange = { profilePublic = it }
                    )
                    SettingRow(
                        icon = Icons.Default.Logout,
                        title = if (isKhmer) "ចាកចេញ" else "Logout",
                        subtitle = if (isKhmer) "ចាកចេញពីឧបករណ៍នេះ" else "Sign out from this device",
                        value = "",
                        danger = true,
                        onClick = { showLogoutDialog = true }
                    )
                }
            }

            item {
                SettingsSection(title = if (isKhmer) "ជំនួយ" else "Support") {
                    SettingRow(
                        icon = Icons.Default.Info,
                        title = if (isKhmer) "អំពី NeakTa" else "About NeakTa",
                        subtitle = if (isKhmer) "កំណែ ព័ត៌មានគម្រោង និងគោលបំណង"
                        else "App version, project info, and purpose",
                        value = "1.0",
                        onClick = {}
                    )
                    SettingRow(
                        icon = Icons.Default.Help,
                        title = if (isKhmer) "ជំនួយ" else "Help",
                        subtitle = if (isKhmer) "ទទួលការគាំទ្រ ឬស្វែងយល់ពីរបៀបប្រើកម្មវិធី"
                        else "Get support or learn how to use the app",
                        value = "",
                        onClick = {}
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
private fun SettingsHeader(onBack: () -> Unit, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = GlassPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = InkText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (isKhmer) "ការកំណត់" else "SETTINGS",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 24.sp,
                    letterSpacing = 5.sp,
                    color = InkText
                )
            )
            Text(
                text = if (isKhmer) "កំណត់បទពិសោធន៍ NeakTa របស់អ្នក"
                else "Customize your NeakTa experience",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            )
        }
    }
}

// — Everything below is unchanged from your original —

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Primary
            )
        )
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIcon(icon = icon, danger = danger)
        SettingsText(title = title, subtitle = subtitle, danger = danger, modifier = Modifier.weight(1f))
        if (value.isNotBlank()) {
            Text(
                text = value,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MutedText.copy(alpha = 0.55f),
            modifier = Modifier.size(20.dp)
        )
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIcon(icon = icon)
        SettingsText(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = InkText,
                checkedTrackColor = Primary,
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
            .border(
                1.dp,
                if (danger) Danger.copy(alpha = 0.35f) else Primary.copy(alpha = 0.35f),
                RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (danger) Danger else Primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsText(
    title: String,
    subtitle: String,
    danger: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (danger) Danger else InkText
            )
        )
        Text(
            text = subtitle,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MutedText
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
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
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111827),
        titleContentColor = InkText,
        textContentColor = MutedText,
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkText
                )
            )
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Primary,
                                unselectedColor = MutedText
                            )
                        )
                        Text(
                            text = option,
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkText
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
            }
        }
    )
}