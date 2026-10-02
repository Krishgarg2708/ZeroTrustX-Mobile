package com.example.presentation.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.Device
import com.example.data.model.DeviceType
import com.example.presentation.components.ConfirmationDialog
import com.example.presentation.components.DeviceCard
import com.example.ui.theme.*

@Composable
fun DevicesScreen(
    devices: List<Device>,
    selectedDevice: Device?,
    onSelectDevice: (Device?) -> Unit,
    onToggleTrust: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var deviceToRevoke by remember { mutableStateOf<Device?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("devices_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Trusted Devices",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hardware endpoints enrolled in Zero Trust identity verification",
                    fontSize = 13.sp,
                    color = CyberTextSecondary
                )
            }
        }

        // Summary Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val trustedCount = devices.count { it.isTrusted }
                    Column {
                        Text(
                            text = "$trustedCount of ${devices.size} Devices Verified",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        Text(
                            text = "Continuous biometric & compliance assessment",
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyberSecureGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = CyberSecureGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        items(devices, key = { it.id }) { device ->
            DeviceCard(
                device = device,
                onClick = { onSelectDevice(device) }
            )
        }
    }

    // Device Detail Sheet / Dialog
    if (selectedDevice != null) {
        DeviceDetailDialog(
            device = selectedDevice,
            onDismiss = { onSelectDevice(null) },
            onRequestRevokeOrTrust = { dev ->
                if (dev.isTrusted) {
                    deviceToRevoke = dev
                } else {
                    onToggleTrust(dev.id)
                }
            }
        )
    }

    // Revocation Confirmation Dialog
    if (deviceToRevoke != null) {
        ConfirmationDialog(
            title = "Revoke Device Trust?",
            message = "${deviceToRevoke?.name} will be immediately restricted. It must re-authenticate and pass Zero Trust posture evaluation before accessing protected resources.",
            confirmText = "Revoke Trust",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = {
                deviceToRevoke?.let { onToggleTrust(it.id) }
                deviceToRevoke = null
            },
            onDismiss = { deviceToRevoke = null }
        )
    }
}

@Composable
fun DeviceDetailDialog(
    device: Device,
    onDismiss: () -> Unit,
    onRequestRevokeOrTrust: (Device) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = device.deviceIdTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (device.isTrusted) CyberSecureGreen.copy(alpha = 0.15f) else CyberWarningAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (device.isTrusted) "✓ TRUSTED" else "LIMITED TRUST",
                        color = if (device.isTrusted) CyberSecureGreen else CyberWarningAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Device Health Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, CyberBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Device Health", fontSize = 12.sp, color = CyberTextSecondary)
                            Text("${device.healthPercentage}%", fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { device.healthPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (device.healthPercentage >= 90) CyberSecureGreen else CyberWarningAmber,
                            trackColor = CyberBorder
                        )
                    }
                }

                DetailRow("Operating System", device.os)
                DetailRow("Location", device.location)
                DetailRow("IP Address", device.ip)
                DetailRow("Last Seen", device.lastActive)

                HorizontalDivider(color = CyberBorderSubtle)

                Text(
                    text = "HARDWARE COMPLIANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted,
                    letterSpacing = 1.sp
                )

                DetailStatusRow("Hardware Disk Encryption", device.encryptionEnabled)
                DetailStatusRow("System Firewall & Packet Filter", device.firewallEnabled)
                DetailStatusRow("Screen Lock & Inactivity Timeout", device.screenLockEnabled)
                DetailStatusRow("Biometric Hardware Key (FIDO2)", device.biometricsEnabled)
            }
        },
        confirmButton = {
            Button(
                onClick = { onRequestRevokeOrTrust(device) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (device.isTrusted) CyberCriticalRed else CyberBlue
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("toggle_device_trust_button")
            ) {
                Text(
                    text = if (device.isTrusted) "Revoke Trust" else "Grant Trust",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = CyberTextSecondary)
            }
        },
        containerColor = CyberSurfaceElevated,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = CyberTextSecondary)
        Text(text = value, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DetailStatusRow(label: String, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = CyberTextPrimary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (enabled) CyberSecureGreen else CyberCriticalRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (enabled) "Enabled" else "Disabled",
                fontSize = 12.sp,
                color = if (enabled) CyberSecureGreen else CyberCriticalRed
            )
        }
    }
}
