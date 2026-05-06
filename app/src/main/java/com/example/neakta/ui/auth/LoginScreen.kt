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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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

// ─── Font Families ─────────────────────────────────────────
private val CormorantGaramond = FontFamily(
    Font(R.font.cormorant_garamond_light,    FontWeight.Light),
    Font(R.font.cormorant_garamond_regular,  FontWeight.Normal),
    Font(R.font.cormorant_garamond_italic,   FontWeight.Normal, FontStyle.Italic),
    Font(R.font.cormorant_garamond_semibold, FontWeight.SemiBold),
)

private val Cinzel = FontFamily(
    Font(R.font.cinzel_regular,  FontWeight.Normal),
    Font(R.font.cinzel_semibold, FontWeight.SemiBold),
)

// ─── Color Tokens ───────────────────────────────────────────
val GemGold       = Color(0xFFD4B870)
val GemGoldDim    = Color(0xFFD4B870).copy(alpha = 0.5f)
val NightBase     = Color(0xFF0C0A07)
val TextPrimary   = Color(0xFFEEE6D2)
val TextSecondary = Color(0xFFB4A88C).copy(alpha = 0.70f)
val BorderGold    = Color(0xFFD4B870).copy(alpha = 0.30f)
val RuleColor     = Color(0xFFD4B870).copy(alpha = 0.35f)

private val CardShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background photo ────────────────────────────────
        AsyncImage(
            model = R.drawable.bayon_intro,
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

        // ── TOP: NEAKTA ─────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp),
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
                    .padding(horizontal = 28.dp)
                    .padding(top = 28.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // SIGN IN label
                Text(
                    text = "SIGN IN",
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

                // Forgot password
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.CenterEnd),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Forgot password?",
                            style = TextStyle(
                                fontFamily = CormorantGaramond,
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = GemGoldDim
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Error message
                if (loginState is LoginState.Error) {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = Color(0xFFE87A7A),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // LOG IN button
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
                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(
                            color = GemGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Button(
                            onClick = { viewModel.login(email, password) },
                            enabled = loginState !is LoginState.Loading,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            elevation = null
                        ) {
                            Text(
                                text = "LOG IN",
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

                // Register link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "No account? ",
                        style = TextStyle(
                            fontFamily = CormorantGaramond,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    )
                    TextButton(
                        onClick = onNavigateToRegister,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Register now",
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

// ─── Neakta Text Field ──────────────────────────────────────
@Composable
fun NeaktaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Cinzel,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                color = TextSecondary
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
                        fontFamily = CormorantGaramond,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextSecondary
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
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = GemGoldDim
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = GemGold,
                unfocusedBorderColor    = BorderGold,
                focusedTextColor        = TextPrimary,
                unfocusedTextColor      = TextPrimary,
                cursorColor             = GemGold,
                focusedContainerColor   = Color(0x1A0C0A07),
                unfocusedContainerColor = Color(0x1A0C0A07)
            ),
            textStyle = TextStyle(
                fontFamily = CormorantGaramond,
                fontSize = 15.sp,
                color = TextPrimary
            ),
            singleLine = true
        )
    }
}