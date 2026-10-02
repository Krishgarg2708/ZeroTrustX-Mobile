package com.example.presentation.components

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun InstallMobileDialog(
    onDismiss: () -> Unit,
    onEnrollCurrentDevice: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) }
    var copiedFeedback by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberBlue.copy(alpha = 0.2f))
                            .border(BorderStroke(1.dp, CyberCyan), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.InstallMobile,
                            contentDescription = "Install on Phone",
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Install on Your Phone",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        Text(
                            text = "Native Android APK Deployment",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Sub-tabs: Guide vs Telemetry & QR
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = CyberSurface,
                    contentColor = CyberCyan,
                    divider = {},
                    indicator = {}
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Text(
                                "Install Guide (APK)",
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == 0) CyberCyan else CyberTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Text(
                                "Device QR & Specs",
                                fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == 1) CyberCyan else CyberTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (activeTab == 0) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                                border = BorderStroke(1.dp, CyberBorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "HOW TO GET THE APK ON YOUR MOBILE PHONE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCyan,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    StepItem(
                                        stepNum = "1",
                                        title = "Open AI Studio Settings",
                                        description = "Click the Settings menu (⚙️ or ⋮ icon) in the top-right toolbar of Google AI Studio."
                                    )
                                    StepItem(
                                        stepNum = "2",
                                        title = "Generate APK / Export ZIP",
                                        description = "Select 'Generate APK' (or 'Export as ZIP' to compile in Android Studio)."
                                    )
                                    StepItem(
                                        stepNum = "3",
                                        title = "Send to Your Android Phone",
                                        description = "Download the APK file and send it to your phone via Google Drive, WhatsApp, USB cable, or direct download."
                                    )
                                    StepItem(
                                        stepNum = "4",
                                        title = "Install & Launch",
                                        description = "Tap the APK on your phone, allow 'Install unknown apps' if prompted by Android, and tap Install!"
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = {
                                    val instructions = """
                                        ZeroTrustX Mobile — Installation Instructions:
                                        1. Open Google AI Studio toolbar settings (top right).
                                        2. Click 'Generate APK'.
                                        3. Transfer the downloaded .apk file to your Android phone.
                                        4. Tap the APK file and select 'Install'.
                                        5. Open ZeroTrustX Mobile on your phone and log in with admin@zerotrustx.demo / Demo@123!
                                    """.trimIndent()
                                    clipboardManager.setText(AnnotatedString(instructions))
                                    copiedFeedback = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (copiedFeedback) "✓ Copied to Clipboard!" else "Copy Step-by-Step Instructions", fontSize = 12.sp)
                            }
                        }
                    } else {
                        // QR Code & Device Telemetry
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Simulated Cybersecurity QR Code
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .border(BorderStroke(2.dp, CyberCyan), RoundedCornerShape(16.dp))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    QrCodeVisualizer()
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "ENROLLMENT CODE: ZX-MOB-9942",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = CyberCyan
                                )
                                Text(
                                    text = "Valid for 24h • Auto-associates with ZeroTrustX",
                                    fontSize = 11.sp,
                                    color = CyberTextMuted
                                )
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                                border = BorderStroke(1.dp, CyberBorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "HOST DEVICE COMPATIBILITY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCyan,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
                                    val model = Build.MODEL
                                    val osRelease = Build.VERSION.RELEASE
                                    val sdkInt = Build.VERSION.SDK_INT

                                    CompatibilityRow("Device Hardware", "$manufacturer $model")
                                    CompatibilityRow("Operating System", "Android $osRelease (API $sdkInt)")
                                    CompatibilityRow("Biometric Enclave", "Supported (Hardware Keystore)")
                                    CompatibilityRow("Zero Trust Status", "✓ 100% Compatible")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEnrollCurrentDevice()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Enroll Device")
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
private fun StepItem(stepNum: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CyberBlue)
                .padding(top = 1.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNum,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 11.sp, color = CyberTextSecondary, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun CompatibilityRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = CyberTextSecondary)
        Text(text = value, fontSize = 12.sp, color = CyberTextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QrCodeVisualizer() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellCount = 15
        val cellSize = size.width / cellCount

        // Draw outer locator squares
        // Top-left
        drawRect(color = Color.Black, topLeft = Offset(0f, 0f), size = Size(cellSize * 4, cellSize * 4))
        drawRect(color = Color.White, topLeft = Offset(cellSize, cellSize), size = Size(cellSize * 2, cellSize * 2))
        drawRect(color = Color.Black, topLeft = Offset(cellSize * 1.3f, cellSize * 1.3f), size = Size(cellSize * 1.4f, cellSize * 1.4f))

        // Top-right
        drawRect(color = Color.Black, topLeft = Offset(size.width - cellSize * 4, 0f), size = Size(cellSize * 4, cellSize * 4))
        drawRect(color = Color.White, topLeft = Offset(size.width - cellSize * 3, cellSize), size = Size(cellSize * 2, cellSize * 2))
        drawRect(color = Color.Black, topLeft = Offset(size.width - cellSize * 2.7f, cellSize * 1.3f), size = Size(cellSize * 1.4f, cellSize * 1.4f))

        // Bottom-left
        drawRect(color = Color.Black, topLeft = Offset(0f, size.height - cellSize * 4), size = Size(cellSize * 4, cellSize * 4))
        drawRect(color = Color.White, topLeft = Offset(cellSize, size.height - cellSize * 3), size = Size(cellSize * 2, cellSize * 2))
        drawRect(color = Color.Black, topLeft = Offset(cellSize * 1.3f, size.height - cellSize * 2.7f), size = Size(cellSize * 1.4f, cellSize * 1.4f))

        // Deterministic QR pattern pixels
        val pattern = listOf(
            Pair(5, 1), Pair(6, 2), Pair(7, 1), Pair(8, 3), Pair(9, 2),
            Pair(1, 5), Pair(2, 6), Pair(3, 7), Pair(5, 5), Pair(6, 6), Pair(7, 7),
            Pair(8, 5), Pair(9, 6), Pair(10, 5), Pair(12, 6), Pair(13, 7),
            Pair(5, 8), Pair(6, 9), Pair(8, 8), Pair(9, 9), Pair(11, 8),
            Pair(1, 10), Pair(2, 11), Pair(5, 11), Pair(6, 12), Pair(8, 11),
            Pair(10, 11), Pair(12, 10), Pair(13, 12), Pair(14, 11),
            Pair(5, 13), Pair(7, 14), Pair(8, 13), Pair(10, 14), Pair(11, 13)
        )

        pattern.forEach { (col, row) ->
            drawRect(
                color = Color.Black,
                topLeft = Offset(col * cellSize, row * cellSize),
                size = Size(cellSize * 0.95f, cellSize * 0.95f)
            )
        }
    }
}
