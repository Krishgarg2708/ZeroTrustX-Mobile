package com.example.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2200)
        onTimeout()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Shield Logo Container
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceCard)
                    .border(
                        BorderStroke(
                            2.dp,
                            Brush.sweepGradient(listOf(CyberBlue, CyberCyan, CyberBlueDark, CyberBlue))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "ZeroTrustX Logo",
                    tint = CyberCyan,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "ZeroTrustX",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyberTextPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "VERIFY EVERY ACCESS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Trust Nothing by Default",
                fontSize = 13.sp,
                color = CyberTextSecondary
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Pulse loading indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyberCyan,
                trackColor = CyberBorder
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "INITIALIZING SECURE ENCLAVE...",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextMuted.copy(alpha = pulseAlpha),
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSubmit: (String, String) -> Boolean
) {
    var email by remember { mutableStateOf("admin@zerotrustx.demo") }
    var password by remember { mutableStateOf("Demo@123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(24.dp)
            .testTag("login_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Demo Environment Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurfaceElevated)
                .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(CyberCyan)
                )
                Text(
                    text = "DEMO ENVIRONMENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Brand Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(CyberSurfaceCard)
                .border(BorderStroke(1.5.dp, CyberBorder), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "ZeroTrustX Mobile",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Enterprise Zero Trust Access Portal",
            fontSize = 14.sp,
            color = CyberTextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Work Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = { Text("Work Email") },
            placeholder = { Text("user@company.com") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = CyberTextSecondary)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("email_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberSurfaceCard,
                unfocusedContainerColor = CyberSurfaceCard,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberBorder,
                focusedLabelColor = CyberCyan,
                unfocusedLabelColor = CyberTextSecondary,
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text("Password") },
            placeholder = { Text("••••••••") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = CyberTextSecondary)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = CyberTextMuted
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (onLoginSubmit(email, password)) {
                        errorMessage = null
                    } else {
                        errorMessage = "Invalid credentials. Use admin@zerotrustx.demo / Demo@123"
                    }
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("password_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberSurfaceCard,
                unfocusedContainerColor = CyberSurfaceCard,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberBorder,
                focusedLabelColor = CyberCyan,
                unfocusedLabelColor = CyberTextSecondary,
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage ?: "",
                color = CyberCriticalRed,
                fontSize = 13.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    email = "admin@zerotrustx.demo"
                    password = "Demo@123"
                    errorMessage = null
                }
            ) {
                Text(
                    text = "Auto-Fill Demo Credentials",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            TextButton(onClick = { /* Demo forgot password */ }) {
                Text(
                    text = "Forgot password?",
                    color = CyberTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sign In Button
        Button(
            onClick = {
                val ok = onLoginSubmit(email, password)
                if (!ok) {
                    errorMessage = "Invalid credentials. Use admin@zerotrustx.demo / Demo@123"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("login_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberBlue
            )
        ) {
            Text(
                text = "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SSO Button
        OutlinedButton(
            onClick = {
                // Auto sign in via SSO mock
                onLoginSubmit("alex@company.com", "Demo@123")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("sso_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = CyberTextPrimary
            ),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Continue with Corporate SSO",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Security indicators
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            border = BorderStroke(1.dp, CyberBorderSubtle)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyberSecureGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hardware-backed Enclave Active",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continuous Zero Trust Posture Evaluation",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun MfaScreen(
    onVerifyOtp: (String) -> Boolean,
    onTriggerBiometrics: (() -> Unit)? = null,
    onBackToLogin: () -> Unit
) {
    var otpDigits by remember { mutableStateOf(listOf("4", "8", "2", "1", "6", "9")) }
    var activeIndex by remember { mutableStateOf(5) }
    var countdown by remember { mutableIntStateOf(45) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("mfa_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToLogin) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberTextPrimary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "STEP 2 OF 3",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CyberSurfaceCard)
                .border(BorderStroke(1.5.dp, CyberCyan.copy(alpha = 0.5f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Verify Your Identity",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We've sent a verification request to your registered authenticator.",
            fontSize = 14.sp,
            color = CyberTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 6-digit OTP Box
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("otp_container")
        ) {
            otpDigits.forEachIndexed { index, digit ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceCard)
                        .border(
                            BorderStroke(
                                if (isError) 1.5.dp else if (digit.isNotEmpty()) 1.5.dp else 1.dp,
                                if (isError) CyberCriticalRed else if (digit.isNotEmpty()) CyberCyan else CyberBorder
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isError) CyberCriticalRed else CyberTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (countdown > 0) "Resend code in ${countdown}s" else "Didn't receive a code? Resend code",
            color = if (countdown > 0) CyberTextMuted else CyberCyan,
            fontSize = 13.sp,
            modifier = Modifier.clickable(enabled = countdown == 0) {
                countdown = 45
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Verify Button
        Button(
            onClick = {
                val fullCode = otpDigits.joinToString("")
                val success = onVerifyOtp(fullCode)
                if (!success) {
                    isError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("verify_otp_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
        ) {
            Text("Verify Authentication", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (onTriggerBiometrics != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onTriggerBiometrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("biometric_mfa_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Touch Sensor for Biometrics", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                otpDigits = listOf("4", "8", "2", "1", "6", "9")
                onVerifyOtp("482169")
            }) {
                Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Use Authenticator App", color = CyberCyan, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DeviceTrustScreen(
    onTrustSelected: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("device_trust_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "STEP 3 OF 3",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyan,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CyberSurfaceCard)
                .border(BorderStroke(1.5.dp, CyberSecureGreen.copy(alpha = 0.5f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhonelinkLock,
                contentDescription = null,
                tint = CyberSecureGreen,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Is this device trusted?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Evaluate hardware integrity and compliance before granting access.",
            fontSize = 13.sp,
            color = CyberTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Device Specification Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pixel 9 Pro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        Text(
                            text = "Android 16 • Security Patch: Recent",
                            fontSize = 13.sp,
                            color = CyberTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ZX-94A7-21",
                            fontSize = 11.sp,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CyberBorderSubtle)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SECURITY STATUS CHECKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                CheckRow(title = "Hardware Encryption enabled", isPassed = true)
                CheckRow(title = "Screen lock & Biometrics enabled", isPassed = true)
                CheckRow(title = "OS kernel patched & verified", isPassed = true)
                CheckRow(title = "EDR telemetry agent active", isPassed = true)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Button(
            onClick = { onTrustSelected(true) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("trust_device_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
        ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Trust Device", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onTrustSelected(false) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("limited_trust_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextSecondary),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Text("Continue Without Trust (Limited Access)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun CheckRow(title: String, isPassed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isPassed) CyberSecureGreen else CyberCriticalRed,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            color = CyberTextPrimary
        )
    }
}
