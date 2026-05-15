package com.example.neakta.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    var username               by remember { mutableStateOf("") }
    var email                  by remember { mutableStateOf("") }
    var password               by remember { mutableStateOf("") }
    var confirmPassword        by remember { mutableStateOf("") }
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            onRegisterSuccess()
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
                        color = Bubblegum.copy(alpha = 0.28f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 22.dp, bottom = 28.dp)
                ) {

                    // Tag pill
                    StatusPill(text = "New explorer", dotColor = Bubblegum)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CREATE ACCOUNT",
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = Bubblegum
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start pinning\nhidden gems",
                        style = TextStyle(
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = InkText
                        )
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Username
                    NeaktaField(
                        value = username,
                        onValueChange = { username = it },
                        label = "USERNAME",
                        placeholder = "Username"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email
                    NeaktaField(
                        value = email,
                        onValueChange = { email = it },
                        label = "EMAIL",
                        placeholder = "Email address",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password row — side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            NeaktaField(
                                value = password,
                                onValueChange = { password = it },
                                label = "PASSWORD",
                                placeholder = "Password",
                                isPassword = true,
                                passwordVisible = passwordVisible,
                                onPasswordToggle = { passwordVisible = !passwordVisible }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            NeaktaField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = "CONFIRM",
                                placeholder = "Repeat",
                                isPassword = true,
                                passwordVisible = confirmPasswordVisible,
                                onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error
                    if (registerState is RegisterState.Error) {
                        Text(
                            text = (registerState as RegisterState.Error).message,
                            color = Bubblegum,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // REGISTER button — lime accent to differentiate from Login
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        LimePop.copy(alpha = 0.18f),
                                        LimePop.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = LimePop.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(999.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (registerState is RegisterState.Loading) {
                            CircularProgressIndicator(
                                color = LimePop,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Button(
                                onClick = {
                                    viewModel.register(
                                        username        = username,
                                        email           = email,
                                        password        = password,
                                        confirmPassword = confirmPassword
                                    )
                                },
                                enabled = registerState !is RegisterState.Loading,
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent
                                ),
                                elevation = null
                            ) {
                                Text(
                                    text = "CREATE ACCOUNT",
                                    style = TextStyle(
                                        fontFamily = Cinzel,
                                        fontSize = 11.sp,
                                        letterSpacing = 3.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LimePop
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            style = TextStyle(fontSize = 12.sp, color = MutedDim)
                        )
                        TextButton(
                            onClick = onNavigateToLogin,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Log in now",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
