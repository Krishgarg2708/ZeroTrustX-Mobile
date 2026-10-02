package com.example.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSecuritySettings
import com.example.presentation.components.ConfirmationDialog
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    settings: UserSecuritySettings,
    trustedDevicesCount: Int,
    activeSessionsCount: Int,
    onUpdateSettings: (UserSecuritySettings) -> Unit,
    onOpenInstallDialog: () -> Unit,
    onOpenVercelDialog: () -> Unit = {},
    onResetDemoData: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card (Section 23)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(CyberSurfaceElevated)
                            .border(BorderStroke(1.5.dp, CyberCyan), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AM",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Alex Morgan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Security Administrator",
                            fontSize = 13.sp,
                            color = CyberCyan,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "alex@company.com",
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )
                    }
                }
            }
        }

        // Quick Security Status Numbers (Section 23)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MFA", fontSize = 11.sp, color = CyberTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Enabled", fontSize = 14.sp, color = CyberSecureGreen, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(CyberBorderSubtle))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BIOMETRICS", fontSize = 11.sp, color = CyberTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Active", fontSize = 14.sp, color = CyberSecureGreen, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(CyberBorderSubtle))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DEVICES", fontSize = 11.sp, color = CyberTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$trustedDevicesCount", fontSize = 14.sp, color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(CyberBorderSubtle))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SESSIONS", fontSize = 11.sp, color = CyberTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$activeSessionsCount", fontSize = 14.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Security Settings (Section 24)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SECURITY SETTINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        title = "Multi-Factor Authentication (MFA)",
                        subtitle = "Hardware FIDO2 & TOTP step-up verification",
                        checked = settings.mfaEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(mfaEnabled = it)) }
                    )

                    HorizontalDivider(color = CyberBorderSubtle)

                    SettingToggleRow(
                        title = "Biometric Authentication",
                        subtitle = "Local fingerprint / face scan credential unlock",
                        checked = settings.biometricLoginEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(biometricLoginEnabled = it)) }
                    )

                    HorizontalDivider(color = CyberBorderSubtle)

                    SettingToggleRow(
                        title = "Real-time Access Notifications",
                        subtitle = "Push alerts on suspicious attempts & pending approvals",
                        checked = settings.loginNotificationsEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(loginNotificationsEnabled = it)) }
                    )

                    HorizontalDivider(color = CyberBorderSubtle)

                    SettingToggleRow(
                        title = "New Device Verification Gate",
                        subtitle = "Require administrator attestation before enrollment",
                        checked = settings.newDeviceVerificationEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(newDeviceVerificationEnabled = it)) }
                    )

                    HorizontalDivider(color = CyberBorderSubtle)

                    // Session Timeout Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Session Token Lifetime", fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
                            Text("Inactivity automatic invalidation", fontSize = 11.sp, color = CyberTextSecondary)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceElevated)
                                .clickable {
                                    val nextTimeout = when (settings.sessionTimeoutMinutes) {
                                        30 -> 60
                                        60 -> 120
                                        else -> 30
                                    }
                                    onUpdateSettings(settings.copy(sessionTimeoutMinutes = nextTimeout))
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${settings.sessionTimeoutMinutes} min",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Prototype Demo Tools & Reset (Section 35)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROTOTYPE TOOLS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenInstallDialog() }
                            .padding(vertical = 8.dp)
                            .testTag("profile_install_mobile_button"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.InstallMobile, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Install on Mobile Phone", fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
                                Text("Generate APK & deploy to real Android device", fontSize = 11.sp, color = CyberCyan)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberTextMuted)
                    }

                    HorizontalDivider(color = CyberBorderSubtle)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenVercelDialog() }
                            .padding(vertical = 8.dp)
                            .testTag("profile_deploy_vercel_button"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("▲", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Deploy on Vercel", fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
                                Text("Zero-config global web deployment (vercel.json)", fontSize = 11.sp, color = Color.White)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberTextMuted)
                    }

                    HorizontalDivider(color = CyberBorderSubtle)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPasswordDialog = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LockReset, contentDescription = null, tint = CyberTextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Change Security Passcode", fontSize = 13.sp, color = CyberTextPrimary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberTextMuted)
                    }

                    HorizontalDivider(color = CyberBorderSubtle)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(vertical = 8.dp)
                            .testTag("reset_demo_data_button"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = CyberWarningAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Reset Demo Data", fontSize = 13.sp, color = CyberWarningAmber, fontWeight = FontWeight.Medium)
                                Text("Restore baseline devices, requests & alerts", fontSize = 11.sp, color = CyberTextSecondary)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberTextMuted)
                    }
                }
            }
        }

        // Sign Out Button
        item {
            OutlinedButton(
                onClick = { showSignOutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sign_out_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCriticalRed),
                border = BorderStroke(1.dp, CyberCriticalRed.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of ZeroTrustX", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Reset Confirmation
    if (showResetDialog) {
        ConfirmationDialog(
            title = "Reset Demo Data?",
            message = "This will reset all access requests, alerts, devices, and sessions back to their initial factory demonstration state.",
            confirmText = "Reset Baseline",
            cancelText = "Cancel",
            isDestructive = false,
            onConfirm = {
                onResetDemoData()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }

    // Sign Out Confirmation
    if (showSignOutDialog) {
        ConfirmationDialog(
            title = "Sign Out?",
            message = "Your active Zero Trust session will be securely invalidated on this device. You will need to re-authenticate with work credentials and MFA.",
            confirmText = "Sign Out",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = {
                showSignOutDialog = false
                onSignOut()
            },
            onDismiss = { showSignOutDialog = false }
        )
    }

    // Change Password Demo Dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Password", color = CyberTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Enterprise Zero Trust policy enforces hardware SSO and biometric token authentication. Password changes are orchestrated via your identity provider (Azure AD / Okta).",
                    color = CyberTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                ) {
                    Text("Understood")
                }
            },
            containerColor = CyberSurfaceElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 11.sp, color = CyberTextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CyberBlue,
                uncheckedThumbColor = CyberTextSecondary,
                uncheckedTrackColor = CyberSurfaceElevated
            )
        )
    }
}
