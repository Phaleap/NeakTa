package com.example.neakta.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.neakta.R

// Re-uses all color tokens and font families defined in LoginScreen.kt
// (GemGold, GemGoldDim, NightBase, TextPrimary, TextSecondary, BorderGold, RuleColor,
//  CormorantGaramond, Cinzel, CardShape — all already declared there)

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    var username        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var province        by remember { mutableStateOf("") }
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background photo ────────────────────────────────
        AsyncImage(
            model = R.drawable.bayon_intro,        // same hero image as login
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ── Gradient overlay ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x330C0A07),
                            0.3f to Color(0x660C0A07),
                            0.6f to Color(0xAA0C0A07),
                            1.0f to Color(0xFF0C0A07)
                        )
                    )
                )
        )

        // ── TOP: NEAKTA wordmark ────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 65.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEAKTA",
                style = TextStyle(
                    fontFamily = Cinzel,
                    fontSize = 38.sp,
                    letterSpacing = 10.sp,
                    color = GemGold,
                    textAlign = TextAlign.Center
                )
            )
        }

        // ── BOTTOM: dark card + form ─────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = Color(0xF00C0A07),
                    shape = CardShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            GemGold.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CardShape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
                    .padding(top = 28.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // CREATE ACCOUNT label
                Text(
                    text = "CREATE ACCOUNT",
                    style = TextStyle(
                        fontFamily = Cinzel,
                        fontSize = 11.sp,
                        letterSpacing = 4.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Thin gold rule
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(1.dp)
                        .background(RuleColor)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Username field
                NeaktaTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Username",
                    label = "USERNAME",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Email field
                NeaktaTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email address",
                    label = "EMAIL",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password field
                NeaktaTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    label = "PASSWORD",
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordToggle = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm password field
                NeaktaTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm password",
                    label = "CONFIRM PASSWORD",
                    isPassword = true,
                    passwordVisible = confirmPasswordVisible,
                    onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Province field
                NeaktaTextField(
                    value = province,
                    onValueChange = { province = it },
                    placeholder = "Choose your province",
                    label = "PROVINCE",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error message
                if (registerState is RegisterState.Error) {
                    Text(
                        text = (registerState as RegisterState.Error).message,
                        color = Color(0xFFE87A7A),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // REGISTER button — identical shell to LOG IN button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    GemGold.copy(alpha = 0.15f),
                                    GemGold.copy(alpha = 0.05f)
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .border(
                            width = 1.dp,
                            color = GemGold.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (registerState is RegisterState.Loading) {
                        CircularProgressIndicator(
                            color = GemGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Button(
                            onClick = {
                                viewModel.register(
                                    username = username,
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    province = province
                                )
                            },
                            enabled = registerState !is RegisterState.Loading,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            elevation = null
                        ) {
                            Text(
                                text = "REGISTER",
                                style = TextStyle(
                                    fontFamily = Cinzel,
                                    fontSize = 12.sp,
                                    letterSpacing = 3.sp,
                                    color = GemGold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Back to login link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = TextStyle(
                            fontFamily = CormorantGaramond,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    )
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Log in now",
                            style = TextStyle(
                                fontFamily = CormorantGaramond,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = GemGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}