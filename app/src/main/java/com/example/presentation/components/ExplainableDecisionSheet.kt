package com.example.presentation.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExplainableDecision
import com.example.ui.theme.*

@Composable
fun ExplainableDecisionSheet(
    decision: ExplainableDecision,
    onDismiss: () -> Unit,
    onApprove: (() -> Unit)? = null,
    onDeny: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Access Decision Audit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberSurface)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = decision.resourceSensitivity.label,
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = decision.resourceName,
                    style = MaterialTheme.typography.titleMedium,
                    color = CyberCyan
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Final Decision Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = BorderStroke(1.dp, CyberBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DECISION VERDICT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextMuted,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Risk: ${decision.currentRiskScore}/100",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (decision.currentRiskScore > 60) CyberCriticalRed else CyberWarningAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = decision.finalDecision,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (decision.finalDecision.contains("ALLOW")) CyberSecureGreen else CyberWarningAmber
                            )
                        }
                    }
                }

                // "WHY?" EXPLANATION CARD (Requirement 2)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "WHY WAS THIS DECIDED?",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = decision.whyExplanation,
                                fontSize = 12.sp,
                                color = CyberTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // 8 Zero Trust Factor Breakdown
                item {
                    Text(
                        text = "ZERO TRUST VERIFICATION FACTORS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextMuted,
                        letterSpacing = 0.5.sp
                    )
                }

                item { FactorRow("Identity Verification", if (decision.identityVerified) "Verified (Alex Morgan)" else "Unverified", decision.identityVerified) }
                item { FactorRow("MFA Attestation", decision.mfaStatus, !decision.mfaStatus.contains("Disabled")) }
                item { FactorRow("Device Trust", decision.deviceTrust, !decision.deviceTrust.contains("Untrusted")) }
                item { FactorRow("Device Posture", decision.devicePosture, !decision.devicePosture.contains("Non-compliant")) }
                item { FactorRow("Location Familiarity", decision.locationFamiliarity, !decision.locationFamiliarity.contains("Unfamiliar")) }
                item { FactorRow("Network Trust", decision.networkTrust, !decision.networkTrust.contains("Public")) }
                item { FactorRow("Resource Sensitivity", decision.resourceSensitivity.label, true) }

                // Applied Policies Matrix
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "APPLIED POLICY EVALUATION MATRIX",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextMuted,
                        letterSpacing = 0.5.sp
                    )
                }

                items(decision.appliedPolicies) { policy ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = if (policy.isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (policy.isPassed) CyberSecureGreen else CyberCriticalRed,
                            modifier = Modifier.size(14.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = policy.policyName, fontSize = 12.sp, color = CyberTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(text = policy.reason, fontSize = 11.sp, color = CyberTextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDeny != null) {
                    OutlinedButton(
                        onClick = onDeny,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCriticalRed),
                        border = BorderStroke(1.dp, CyberCriticalRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Deny")
                    }
                }
                if (onApprove != null) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Approve JIT")
                    }
                }
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
private fun FactorRow(label: String, value: String, isPositive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = CyberTextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isPositive) CyberSecureGreen else CyberWarningAmber)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                fontSize = 11.sp,
                color = if (isPositive) CyberTextPrimary else CyberWarningAmber,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
