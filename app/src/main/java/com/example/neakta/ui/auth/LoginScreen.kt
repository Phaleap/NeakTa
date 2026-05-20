package com.example.neakta.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Color Tokens (shared with HomeScreen) ───────────────────
val ElectricBlue  = Color(0xFF5FD3A6)
val Bubblegum     = Color(0xFF5FD3A6)
val LimePop       = Color(0xFF5FD3A6)
val InkText       = Color(0xFFF7FAFC)
val MutedText     = Color(0xFFB8C2CC)
val MutedDim      = Color(0x99B8C2CC)
val DeepIndigo    = Color(0xFF0D1117)
val CardStart     = Color(0xE61A202C)
val CardEnd       = Color(0xCC111827)
val SoftOutline   = Color(0x26FFFFFF)
val FieldBorder   = Color(0x26FFFFFF)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1117),
                        Color(0xFF111827),
                        Color(0xFF0D1117),
                        DeepIndigo
                    )
                )
            )
    ) {
        // ── Ambient top glow ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            LimePop.copy(alpha = 0.16f),
                            LimePop.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(LimePop.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top wordmark ─────────────────────────────────
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "NEAKTA",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 28.sp,
                        letterSpacing = 6.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cambodia, remixed for discovery",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Auth card ────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(CardStart, CardEnd)
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = ElectricBlue.copy(alpha = 0.28f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 22.dp, bottom = 28.dp)
                ) {

                    // Tag pill
                    StatusPill(text = "Welcome back", dotColor = LimePop)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SIGN IN",
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = ElectricBlue
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your hidden\ngems await",
                        style = TextStyle(
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = InkText
                        )
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Email
                    NeaktaField(
                        value = email,
                        onValueChange = { email = it },
                        label = "EMAIL",
                        placeholder = "Email address",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password
                    NeaktaField(
                        value = password,
                        onValueChange = { password = it },
                        label = "PASSWORD",
                        placeholder = "Password",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible }
                    )

                    // Forgot password
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { /* TODO */ },
                            modifier = Modifier.align(Alignment.CenterEnd),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Forgot password?",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = ElectricBlue.copy(alpha = 0.55f)
                                )
                            )
                        }
                    }

                    // Error
                    if (loginState is LoginState.Error) {
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = Bubblegum,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // LOG IN button
                    NeaktaButton(
                        text = "LOG IN",
                        isLoading = loginState is LoginState.Loading,
                        accentColor = ElectricBlue,
                        onClick = { viewModel.login(email, password) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Register link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "No account? ",
                            style = TextStyle(fontSize = 12.sp, color = MutedDim)
                        )
                        TextButton(
                            onClick = onNavigateToRegister,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Register now",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Bubblegum
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Shared field component ───────────────────────────────────
@Composable
fun NeaktaField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = MutedDim
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = MutedDim
                    )
                )
            },
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onPasswordToggle?.invoke() }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = ElectricBlue.copy(alpha = 0.5f)
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = ElectricBlue,
                unfocusedBorderColor    = FieldBorder,
                focusedTextColor        = InkText,
                unfocusedTextColor      = InkText,
                cursorColor             = ElectricBlue,
                focusedContainerColor   = Color.White.copy(alpha = 0.04f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
            ),
            textStyle = TextStyle(fontSize = 14.sp, color = InkText),
            singleLine = true
        )
    }
}

// ─── Shared CTA button ────────────────────────────────────────
@Composable
fun NeaktaButton(
    text: String,
    isLoading: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        accentColor.copy(alpha = 0.18f),
                        accentColor.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.55f),
                shape = RoundedCornerShape(999.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = accentColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                elevation = null
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 11.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
            }
        }
    }
}

// ─── Status pill ─────────────────────────────────────────────
@Composable
fun StatusPill(text: String, dotColor: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0x991A202C))
            .border(1.dp, SoftOutline, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(dotColor)
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp,
                color = InkText
            )
        )
    }
}
