package com.example.presentation.decision

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.presentation.components.RiskBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AccessDecisionScreen(
    decisionResult: AccessDecisionResult?,
    isEvaluating: Boolean,
    onRecalculate: (String, String, Boolean, Boolean) -> Unit,
    onTriggerBiometricStepUp: (() -> Unit)? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedResource by remember { mutableStateOf("Production Database (PostgreSQL)") }
    var selectedLocation by remember { mutableStateOf("Mumbai, IN") }
    var isKnownDevice by remember { mutableStateOf(true) }
    var isMfaVerified by remember { mutableStateOf(true) }

    var isLiveEvaluating by remember { mutableStateOf(false) }
    var highlightedStepIndex by remember { mutableIntStateOf(-1) }
    var telemetryLogs by remember {
        mutableStateOf(
            listOf(
                "[INIT] Zero Trust Policy Engine online • Verifying context",
                "[READY] Waiting for telemetry evaluation trigger"
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("decision_visualizer_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Access Decision Engine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "Zero Trust Multi-Vector Policy Evaluation",
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        // Demo Engine Disclaimer Banner (Section 26)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEMO RISK ENGINE • Continuous context calculation: Identity + Device + Location + Network + Resource - MFA Protection",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        // Interactive Simulator Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SIMULATE ACCESS CONTEXT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Resource Selector
                    Text("Target Resource", fontSize = 12.sp, color = CyberTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Production Database", "Admin Console", "Salesforce").forEach { res ->
                            FilterChip(
                                selected = selectedResource.contains(res.split(" ")[0]),
                                onClick = {
                                    selectedResource = res
                                    onRecalculate(selectedResource, selectedLocation, isKnownDevice, isMfaVerified)
                                },
                                label = { Text(res.split(" ")[0], fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggles
                    SimulationToggleRow(
                        label = "Known & Managed Device",
                        checked = isKnownDevice,
                        onCheckedChange = {
                            isKnownDevice = it
                            onRecalculate(selectedResource, selectedLocation, isKnownDevice, isMfaVerified)
                        }
                    )

                    SimulationToggleRow(
                        label = "Corporate Branch Location (Delhi)",
                        checked = selectedLocation.contains("Delhi"),
                        onCheckedChange = {
                            selectedLocation = if (it) "Delhi, IN" else "Mumbai, IN"
                            onRecalculate(selectedResource, selectedLocation, isKnownDevice, isMfaVerified)
                        }
                    )

                    SimulationToggleRow(
                        label = "FIDO2 Hardware MFA Verified",
                        checked = isMfaVerified,
                        onCheckedChange = {
                            isMfaVerified = it
                            onRecalculate(selectedResource, selectedLocation, isKnownDevice, isMfaVerified)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Run Live Evaluation Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLiveEvaluating = true
                                telemetryLogs = listOf("[START] Initiating Zero Trust multi-vector evaluation...")
                                val logs = listOf(
                                    "[STAGE 1: IDENTITY] Context token parsed • FIDO2 token verified",
                                    "[STAGE 2: DEVICE] Hardware TPM PCR verified • EDR signature fresh",
                                    "[STAGE 3: LOCATION] Geo-coordinate delta computed • Variance check",
                                    "[STAGE 4: NETWORK] mTLS session cipher: TLS_AES_256_GCM_SHA384",
                                    "[STAGE 5: RESOURCE] Classification: CONFIDENTIAL • Role Admin validated",
                                    "[STAGE 6: RISK ENGINE] Composite score determined"
                                )
                                for (i in logs.indices) {
                                    highlightedStepIndex = i
                                    delay(400)
                                    telemetryLogs = telemetryLogs + logs[i]
                                }
                                delay(300)
                                onRecalculate(selectedResource, selectedLocation, isKnownDevice, isMfaVerified)
                                isLiveEvaluating = false
                                highlightedStepIndex = -1
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("run_live_evaluation_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLiveEvaluating) "Evaluating Pipeline..." else "Run Live Policy Pipeline",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Decision Result Card
        if (decisionResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                    border = BorderStroke(
                        1.5.dp,
                        when (decisionResult.calculatedRiskLevel) {
                            RiskLevel.LOW -> CyberSecureGreen
                            RiskLevel.MEDIUM -> CyberWarningAmber
                            RiskLevel.HIGH, RiskLevel.CRITICAL -> CyberCriticalRed
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ZERO TRUST VERDICT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                letterSpacing = 1.sp
                            )
                            RiskBadge(riskLevel = decisionResult.calculatedRiskLevel)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = decisionResult.finalVerdict,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (decisionResult.calculatedRiskLevel) {
                                RiskLevel.LOW -> CyberSecureGreen
                                RiskLevel.MEDIUM -> CyberWarningAmber
                                RiskLevel.HIGH, RiskLevel.CRITICAL -> CyberCriticalRed
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = decisionResult.verdictSubtitle,
                            fontSize = 13.sp,
                            color = CyberTextSecondary
                        )

                        if (decisionResult.requiresStepUpAuth && onTriggerBiometricStepUp != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onTriggerBiometricStepUp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberWarningAmber)
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Complete Biometric Step-Up", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = CyberBorderSubtle)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Risk Score:", fontSize = 13.sp, color = CyberTextSecondary)
                            Text(
                                text = "${decisionResult.calculatedRiskScore} / 100",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )
                        }
                    }
                }
            }

            // SOC Telemetry Console
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF070B12)),
                    border = BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyberCyan))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SOC TELEMETRY TERMINAL", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                            }
                            Text("mTLS: ACTIVE", fontSize = 9.sp, color = CyberSecureGreen, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        telemetryLogs.takeLast(4).forEach { log ->
                            Text(
                                text = log,
                                fontSize = 11.sp,
                                color = Color(0xFF93C5FD),
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Pipeline Step-by-Step Visualization (Section 27)
            item {
                Text(
                    text = "DECISION EVALUATION PIPELINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            itemsIndexed(decisionResult.steps) { index, step ->
                PipelineStepItem(
                    step = step,
                    isHighlighted = highlightedStepIndex == index,
                    isLast = index == decisionResult.steps.size - 1
                )
            }
        }
    }
}

@Composable
private fun PipelineStepItem(
    step: DecisionPipelineStep,
    isHighlighted: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            val (icon, color) = when (step.status) {
                StepStatus.PASSED -> Pair(Icons.Default.Check, CyberSecureGreen)
                StepStatus.WARNING -> Pair(Icons.Default.PriorityHigh, CyberWarningAmber)
                StepStatus.FAILED -> Pair(Icons.Default.Close, CyberCriticalRed)
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isHighlighted) CyberCyan.copy(alpha = 0.4f) else color.copy(alpha = 0.2f))
                    .border(BorderStroke(if (isHighlighted) 2.dp else 1.dp, if (isHighlighted) CyberCyan else color), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isHighlighted) CyberCyan else color,
                    modifier = Modifier.size(16.dp)
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(if (isHighlighted) CyberCyan else CyberBorder)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Step details card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = if (isHighlighted) CyberSurfaceElevated else CyberSurfaceCard),
            border = BorderStroke(1.dp, if (isHighlighted) CyberCyan else CyberBorderSubtle)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = step.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isHighlighted) CyberCyan else CyberTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = step.weightDescription,
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.evaluationDetail,
                    fontSize = 12.sp,
                    color = CyberTextSecondary
                )
            }
        }
    }
}

@Composable
private fun SimulationToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = CyberTextPrimary)
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
