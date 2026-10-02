package com.example.presentation.posture

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SecurityPosture
import com.example.ui.theme.*

@Composable
fun PostureDetailDialog(
    posture: SecurityPosture,
    onDismiss: () -> Unit,
    onOpenDecisionEngine: () -> Unit
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
                        text = "Zero Trust Posture",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "Continuous Compliance Audit",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (posture.score >= 80) CyberSecureGreen.copy(alpha = 0.15f) else CyberWarningAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = posture.overallPosture,
                        color = if (posture.score >= 80) CyberSecureGreen else CyberWarningAmber,
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, CyberBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Trust Score", fontSize = 12.sp, color = CyberTextSecondary)
                            Text(
                                text = "${posture.score} / 100",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = if (posture.score >= 80) CyberSecureGreen else CyberWarningAmber,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "POLICY AUDIT MATRIX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted,
                    letterSpacing = 1.sp
                )

                AuditRow("Identity Context Verification", "FIDO2 + TOTP Active", true)
                AuditRow("Endpoint Compliance Baseline", "Hardware FBE & OS patched", posture.deviceCompliant)
                AuditRow("Microsegmentation Perimeter", "mTLS overlay active", posture.networkMonitored)
                AuditRow("Continuous Adaptive Auth", "Risk score dynamic triggers", true)
                AuditRow("Least Privilege Access Enforcement", "Role-based time-bound tokens", true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onOpenDecisionEngine()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Open Decision Engine", fontWeight = FontWeight.Bold)
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
private fun AuditRow(title: String, subtitle: String, isPassed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isPassed) CyberSecureGreen else CyberCriticalRed,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 11.sp, color = CyberTextSecondary)
        }
    }
}
