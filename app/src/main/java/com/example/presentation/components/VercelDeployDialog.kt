package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
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
fun VercelDeployDialog(
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedCli by remember { mutableStateOf(false) }

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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▲",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Deploy on Vercel",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        Text(
                            text = "Global Web Command Center",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberSecureGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("vercel.json Pre-Configured", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                                Text("This repository includes a production-ready Web Command Center in /public.", fontSize = 11.sp, color = CyberTextSecondary)
                            }
                        }
                    }
                }

                item {
                    Text("3-STEP DEPLOYMENT WORKFLOW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan, letterSpacing = 1.sp)
                }

                item {
                    StepItem(
                        step = "1",
                        title = "Push to GitHub",
                        desc = "Push your project repository to GitHub from the top AI Studio menu or git CLI."
                    )
                }

                item {
                    StepItem(
                        step = "2",
                        title = "Import Project into Vercel",
                        desc = "Log into vercel.com, click 'Add New Project', and select your GitHub repository."
                    )
                }

                item {
                    StepItem(
                        step = "3",
                        title = "Instant Global Deploy",
                        desc = "Click Deploy. Vercel automatically detects vercel.json and serves your live ZeroTrustX Web Portal at a *.vercel.app domain!"
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("COMMAND LINE DEPLOYMENT (VERCEL CLI)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberTextMuted, letterSpacing = 0.8.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                        border = BorderStroke(1.dp, CyberBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "vercel deploy --prod",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = CyberCyan
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("npm i -g vercel && vercel deploy --prod"))
                                    copiedCli = true
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (copiedCli) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy command",
                                    tint = if (copiedCli) CyberSecureGreen else CyberTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    if (copiedCli) {
                        Text("Copied CLI command to clipboard!", fontSize = 10.sp, color = CyberSecureGreen)
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                        border = BorderStroke(1.dp, CyberBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Hybrid Enterprise Architecture:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                            Text("• Web Portal: Hosted on Vercel for SOC analysts and live monitoring.\n• Mobile Endpoint: Native Android APK deployed to phones for continuous biometric attestation.", fontSize = 10.sp, color = CyberTextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Got It", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CyberSurfaceElevated,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun StepItem(step: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CyberSurface)
                .border(BorderStroke(1.dp, CyberCyan), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
            Text(desc, fontSize = 11.sp, color = CyberTextSecondary)
        }
    }
}
