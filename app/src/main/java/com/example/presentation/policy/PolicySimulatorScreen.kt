package com.example.presentation.policy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.AccessClassification
import com.example.data.model.RiskLevel
import com.example.presentation.components.RiskBadge
import com.example.ui.theme.*

@Composable
fun PolicySimulatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedUser by remember { mutableStateOf("Alex Morgan (Admin)") }
    var selectedDevice by remember { mutableStateOf("Corporate MacBook Pro") }
    var selectedLocation by remember { mutableStateOf("Corporate HQ (Delhi)") }
    var selectedNetwork by remember { mutableStateOf("Zero Trust mTLS Tunnel") }
    var selectedResource by remember { mutableStateOf("Production DB (PostgreSQL)") }
    var selectedMfaState by remember { mutableStateOf("FIDO2 Hardware Key") }
    var selectedCompliance by remember { mutableStateOf("100% Compliant") }

    // Dynamic Mock Risk & Decision Engine Calculation
    val (computedRiskScore, decisionVerdict, passedPolicies, failedPolicies, riskFactors) = remember(
        selectedUser, selectedDevice, selectedLocation, selectedNetwork, selectedResource, selectedMfaState, selectedCompliance
    ) {
        var score = 15 // baseline

        // User modifier
        if (selectedUser.contains("Contractor")) score += 15

        // Device modifier
        when {
            selectedDevice.contains("Untrusted") -> score += 35
            selectedDevice.contains("Personal") -> score += 20
        }

        // Location modifier
        when {
            selectedLocation.contains("Overseas") -> score += 30
            selectedLocation.contains("Cafe") -> score += 25
            selectedLocation.contains("Mumbai") -> score += 12
        }

        // Network modifier
        when {
            selectedNetwork.contains("Public") -> score += 25
            selectedNetwork.contains("SD-WAN") -> score += 8
        }

        // Resource sensitivity modifier
        when {
            selectedResource.contains("AWS Root") -> score += 30
            selectedResource.contains("Production DB") -> score += 20
            selectedResource.contains("Salesforce") -> score += 5
            selectedResource.contains("Status Page") -> score -= 10
        }

        // MFA mitigation
        when {
            selectedMfaState.contains("FIDO2") -> score -= 20
            selectedMfaState.contains("Push") -> score -= 10
            selectedMfaState.contains("Disabled") -> score += 35
        }

        // Compliance modifier
        when {
            selectedCompliance.contains("Jailbroken") || selectedCompliance.contains("Rooted") -> score += 45
            selectedCompliance.contains("Unencrypted") -> score += 30
            selectedCompliance.contains("Minor Patch") -> score += 10
            selectedCompliance.contains("100%") -> score -= 5
        }

        val clampedScore = score.coerceIn(5, 98)

        val verdict = when {
            clampedScore <= 32 -> "ALLOW (LEAST PRIVILEGE TOKEN ISSUED)"
            clampedScore <= 65 -> "STEP-UP AUTHENTICATION REQUIRED"
            else -> "ACCESS DENIED (POLICY RESTRICTION)"
        }

        val passed = mutableListOf<String>()
        val failed = mutableListOf<String>()
        val factors = mutableListOf<String>()

        if (!selectedMfaState.contains("Disabled")) {
            passed.add("Policy #ZT-101: Phishing-resistant MFA satisfied")
        } else {
            failed.add("Policy #ZT-101: MFA requirement violated")
            factors.add("Critical: Authentication lacking multi-factor verification")
        }

        if (selectedCompliance.contains("100%")) {
            passed.add("Policy #ZT-204: Hardware TPM and disk encryption compliant")
        } else {
            failed.add("Policy #ZT-204: Endpoint compliance baseline failed")
            factors.add("High: Device compliance ($selectedCompliance)")
        }

        if (selectedNetwork.contains("mTLS")) {
            passed.add("Policy #ZT-305: Mutual TLS cryptographic session verified")
        } else {
            failed.add("Policy #ZT-305: Insecure transport path detected")
            factors.add("Medium: Unencrypted/untrusted network path")
        }

        if (selectedLocation.contains("Delhi")) {
            passed.add("Policy #ZT-401: Geographic boundary within trusted envelope")
        } else {
            failed.add("Policy #ZT-401: Geographic boundary alert triggered")
            factors.add("Medium: Location variance ($selectedLocation)")
        }

        Pent(clampedScore, verdict, passed, failed, factors)
    }

    val riskLevel = when {
        computedRiskScore <= 30 -> RiskLevel.LOW
        computedRiskScore <= 60 -> RiskLevel.MEDIUM
        computedRiskScore <= 80 -> RiskLevel.HIGH
        else -> RiskLevel.CRITICAL
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("policy_simulator_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "Policy Simulator",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "DEMO POLICY ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }
        }

        // Demo Engine Disclaimer (Requirement 3)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEMO POLICY ENGINE • Adjust context vectors below to simulate real-time risk scores and policy adjudication.",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        // Decision Result Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(
                    1.5.dp,
                    when (riskLevel) {
                        RiskLevel.LOW -> CyberSecureGreen
                        RiskLevel.MEDIUM -> CyberWarningAmber
                        RiskLevel.HIGH, RiskLevel.CRITICAL -> CyberCriticalRed
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SIMULATED VERDICT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                        RiskBadge(riskLevel = riskLevel)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = decisionVerdict,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (riskLevel) {
                            RiskLevel.LOW -> CyberSecureGreen
                            RiskLevel.MEDIUM -> CyberWarningAmber
                            RiskLevel.HIGH, RiskLevel.CRITICAL -> CyberCriticalRed
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CyberBorderSubtle)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Computed Risk Score:", fontSize = 12.sp, color = CyberTextSecondary)
                        Text(
                            text = "$computedRiskScore / 100",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                    }
                }
            }
        }

        // Simulator Inputs Group
        item {
            Text(
                text = "CONTEXT INPUT VECTORS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted,
                letterSpacing = 1.sp
            )
        }

        item {
            SelectorGroup(
                title = "Subject / User",
                options = listOf("Alex Morgan (Admin)", "Sarah Chen (IT Ops)", "Contractor Dave"),
                selected = selectedUser,
                onSelect = { selectedUser = it }
            )
        }

        item {
            SelectorGroup(
                title = "Device Endpoint",
                options = listOf("Corporate MacBook Pro", "Managed Pixel 9 Pro", "Personal iPhone", "Untrusted Windows"),
                selected = selectedDevice,
                onSelect = { selectedDevice = it }
            )
        }

        item {
            SelectorGroup(
                title = "Location Context",
                options = listOf("Corporate HQ (Delhi)", "Branch Office (Mumbai)", "Public Cafe", "Overseas"),
                selected = selectedLocation,
                onSelect = { selectedLocation = it }
            )
        }

        item {
            SelectorGroup(
                title = "Network Transport",
                options = listOf("Zero Trust mTLS Tunnel", "Corporate SD-WAN", "Public Unencrypted Wi-Fi"),
                selected = selectedNetwork,
                onSelect = { selectedNetwork = it }
            )
        }

        item {
            SelectorGroup(
                title = "Target Resource",
                options = listOf("AWS Root Console", "Production DB", "Salesforce", "Status Page"),
                selected = selectedResource,
                onSelect = { selectedResource = it }
            )
        }

        item {
            SelectorGroup(
                title = "Multi-Factor Authentication (MFA)",
                options = listOf("FIDO2 Hardware Key", "Mobile Push TOTP", "Disabled"),
                selected = selectedMfaState,
                onSelect = { selectedMfaState = it }
            )
        }

        item {
            SelectorGroup(
                title = "Device Health & Compliance",
                options = listOf("100% Compliant", "Minor Patch Missing", "Jailbroken / Rooted"),
                selected = selectedCompliance,
                onSelect = { selectedCompliance = it }
            )
        }

        // Passed vs Failed Policies
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "POLICY ADJUDICATION BREAKDOWN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Passed Policies (${passedPolicies.size})", fontSize = 12.sp, color = CyberSecureGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    passedPolicies.forEach { policy ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = CyberSecureGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(policy, fontSize = 11.sp, color = CyberTextPrimary)
                        }
                    }

                    if (failedPolicies.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Failed Policies (${failedPolicies.size})", fontSize = 12.sp, color = CyberCriticalRed, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        failedPolicies.forEach { policy ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = CyberCriticalRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(policy, fontSize = 11.sp, color = CyberTextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorGroup(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(text = title, fontSize = 12.sp, color = CyberTextSecondary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { option ->
                FilterChip(
                    selected = selected.contains(option.split(" ")[0]),
                    onClick = { onSelect(option) },
                    label = { Text(option, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberBlue,
                        selectedLabelColor = Color.White,
                        containerColor = CyberSurfaceCard,
                        labelColor = CyberTextSecondary
                    ),
                    border = BorderStroke(1.dp, CyberBorder)
                )
            }
        }
    }
}

private data class Pent(
    val score: Int,
    val verdict: String,
    val passed: List<String>,
    val failed: List<String>,
    val factors: List<String>
)
