package com.example.presentation.flow

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ZeroTrustFlowNode(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val status: String,
    val telemetryDetail: String,
    val verificationRule: String
)

@Composable
fun SignatureZeroTrustFlowScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nodes = remember {
        listOf(
            ZeroTrustFlowNode(
                "node_identity", "1. IDENTITY", "User & Context Token",
                Icons.Default.Person, "Verified (Alex Morgan)",
                "Cryptographic JWT issued by Azure AD / Okta SSO enclave. Nonce valid for 30m.",
                "Zero Trust Rule: Verify identity explicitly with phishing-resistant credentials."
            ),
            ZeroTrustFlowNode(
                "node_mfa", "2. MULTI-FACTOR AUTH", "FIDO2 / WebAuthn Hardware",
                Icons.Default.VpnKey, "Hardware Key Approved",
                "Hardware enclave biometric touch validated via WebAuthn. SMS/voice OTP blocked.",
                "Zero Trust Rule: Always demand step-up challenges on high-value asset requests."
            ),
            ZeroTrustFlowNode(
                "node_device", "3. DEVICE INTEGRITY", "Hardware TPM 2.0 & Health",
                Icons.Default.PhonelinkLock, "Compliant (Pixel 9 Pro)",
                "Full disk encryption enabled (FBE). OS patched to latest CVE baseline. No root.",
                "Zero Trust Rule: Assume device breach until verified by continuous EDR telemetry."
            ),
            ZeroTrustFlowNode(
                "node_location", "4. LOCATION CONTEXT", "Geofence & Travel Velocity",
                Icons.Default.LocationOn, "Geo Delta Checked",
                "Coordinates correlated with corporate branch Delhi vs Mumbai. Velocity verified.",
                "Zero Trust Rule: Impossible physical velocity (>800km/h) triggers instant isolation."
            ),
            ZeroTrustFlowNode(
                "node_network", "5. NETWORK TRUST", "Microsegmentation & mTLS",
                Icons.Default.Lan, "Mutual TLS 1.3 Active",
                "Zero Trust tunnel established over mTLS. Direct perimeter IP routing prohibited.",
                "Zero Trust Rule: Treat all networks (internal Wi-Fi, SD-WAN, cellular) as hostile."
            ),
            ZeroTrustFlowNode(
                "node_resource", "6. RESOURCE SENSITIVITY", "Asset Classification",
                Icons.Default.Lock, "CONFIDENTIAL (PostgreSQL)",
                "Target sensitivity: CONFIDENTIAL. Contains transactional customer records.",
                "Zero Trust Rule: Access granted per resource, not to the entire network subnet."
            ),
            ZeroTrustFlowNode(
                "node_risk", "7. RISK ENGINE", "Adaptive Risk Aggregator",
                Icons.Default.Speed, "Score: 28 / 100 (LOW)",
                "Composite score dynamically aggregated from identity, device, geo, and asset metrics.",
                "Zero Trust Rule: Risk dynamically re-calculated continuously on every request."
            ),
            ZeroTrustFlowNode(
                "node_policy", "8. POLICY ENGINE", "RBAC & JIT Entitlements",
                Icons.Default.Policy, "Rule #ZT-402 Evaluated",
                "SecOps tier 3 entitlement matched. 30-minute Just-In-Time access window applied.",
                "Zero Trust Rule: Enforce Least Privilege. No permanent standing administrative roles."
            ),
            ZeroTrustFlowNode(
                "node_decision", "9. ACCESS DECISION", "Final Verdict",
                Icons.Default.VerifiedUser, "ALLOW (LEAST PRIVILEGE)",
                "Access token minted with bounded 30-minute TTL and read-only query scope.",
                "Zero Trust Philosophy: 'Never trust. Always verify.'"
            )
        )
    }

    var selectedNode by remember { mutableStateOf<ZeroTrustFlowNode?>(nodes.first()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("signature_zero_trust_flow_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberTextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Zero Trust Architecture",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "Continuous Verification Pipeline",
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        // Philosophy Motto Banner (Requirement 12)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "THE ZERO TRUST CREED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"Never trust. Always verify.\"",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Every transaction is evaluated across 9 orthogonal vectors before minting short-lived session tokens.",
                        fontSize = 12.sp,
                        color = CyberTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Inspection Detail Card (when a node is selected)
        if (selectedNode != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                    border = BorderStroke(1.5.dp, CyberCyan)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedNode!!.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberSecureGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = selectedNode!!.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberSecureGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedNode!!.telemetryDetail,
                            fontSize = 12.sp,
                            color = CyberTextPrimary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = CyberBorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = selectedNode!!.verificationRule,
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "INTERACTIVE VERIFICATION STAGES (TAP TO INSPECT)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 9 Nodes Flow
        items(nodes.size) { index ->
            val node = nodes[index]
            val isSelected = selectedNode?.id == node.id
            val isLast = index == nodes.size - 1

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedNode = node }
                    .testTag("node_${node.id}"),
                verticalAlignment = Alignment.Top
            ) {
                // Number / Icon Circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) CyberCyan else CyberSurfaceElevated)
                            .border(BorderStroke(1.dp, if (isSelected) CyberCyan else CyberBorder), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = node.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.Black else CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(38.dp)
                                .background(if (isSelected) CyberCyan else CyberBorder)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Node summary card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (isLast) 0.dp else 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) CyberSurfaceElevated else CyberSurfaceCard),
                    border = BorderStroke(1.dp, if (isSelected) CyberCyan.copy(alpha = 0.6f) else CyberBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = node.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyberCyan else CyberTextPrimary
                            )
                            Text(
                                text = node.subtitle,
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Inspect",
                            tint = if (isSelected) CyberCyan else CyberTextMuted
                        )
                    }
                }
            }
        }
    }
}
