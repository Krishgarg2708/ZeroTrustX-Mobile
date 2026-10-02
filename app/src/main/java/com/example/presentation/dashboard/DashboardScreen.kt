package com.example.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.*
import com.example.presentation.NavigationTab
import com.example.presentation.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    posture: SecurityPosture,
    devices: List<Device>,
    accessRequests: List<AccessRequest>,
    alerts: List<SecurityAlert>,
    sessions: List<ActiveSession>,
    lockdownState: LockdownState,
    radarNodes: List<RadarThreatNode>,
    recommendations: List<SecurityRecommendation>,
    continuousTrust: ContinuousTrustState,
    auditEvents: List<AuditEvent>,
    selectedRadarNode: RadarThreatNode?,
    onSelectRadarNode: (RadarThreatNode?) -> Unit,
    onToggleLockdown: () -> Unit,
    onOpenAuditLogs: () -> Unit,
    onOpenInstallDialog: () -> Unit,
    onSelectTab: (NavigationTab) -> Unit,
    onOpenPostureDetail: () -> Unit,
    onOpenDecisionVisualizer: (String) -> Unit,
    onOpenExplainableDecision: (String) -> Unit,
    onOpenSignatureFlow: () -> Unit,
    onOpenPolicySimulator: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenAnalyst: () -> Unit,
    onOpenRbac: () -> Unit,
    onOpenScenario: () -> Unit,
    onSimulateContextShift: (String, Int, Boolean) -> Unit,
    onSatisfyContinuousChallenge: () -> Unit,
    onResolveRecommendation: (String, RecommendationTarget) -> Unit,
    onSelectRequest: (AccessRequest) -> Unit,
    onApproveRequest: (String) -> Unit,
    onDenyRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLockdownConfirmDialog by remember { mutableStateOf(false) }

    val pendingRequests = accessRequests.filter { it.status == RequestStatus.PENDING }
    val activeAlertCount = alerts.count { !it.isDismissed }
    val trustedDeviceCount = devices.count { it.isTrusted }
    val unresolvedRecs = recommendations.filter { !it.isResolved }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emergency Lockdown Banner (when active)
        if (lockdownState.isActive) {
            item {
                LockdownBanner(
                    lockdownState = lockdownState,
                    onDisengage = onToggleLockdown
                )
            }
        }

        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good morning, Alex",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Security Administrator • SOC Tier 3",
                        fontSize = 13.sp,
                        color = CyberTextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { showLockdownConfirmDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (lockdownState.isActive) CyberCriticalRed.copy(alpha = 0.2f) else CyberSurfaceElevated)
                            .border(BorderStroke(1.dp, if (lockdownState.isActive) CyberCriticalRed else CyberBorder), CircleShape)
                            .testTag("lockdown_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShieldMoon,
                            contentDescription = "Lockdown",
                            tint = if (lockdownState.isActive) CyberCriticalRed else CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Demo Mode Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberSurfaceElevated)
                            .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DEMO MODE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // SECTION 1: Zero Trust Core Answers (Am I Secure? What Requires Attention? Why Risky?)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zero_trust_status_answers_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ZERO TRUST COMMAND CENTER",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSecureGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("CONTINUOUS ATTESTATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberSecureGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Q1: Am I Secure?
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(CyberSecureGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = CyberSecureGreen, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "1. Am I secure?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                            Text(
                                text = "YES • Posture Score is 87/100 (Protected). Hardware TPM, continuous telemetry, and FIDO2 MFA are actively verified across 4 enclaves.",
                                fontSize = 12.sp,
                                color = CyberTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CyberBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Q2: What requires my attention?
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(CyberWarningAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = CyberWarningAmber, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "2. What requires my attention?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberWarningAmber
                            )
                            Text(
                                text = "${pendingRequests.size} Pending Access Decision • ${activeAlertCount} Security Alerts • ${unresolvedRecs.size} Recommendations to review.",
                                fontSize = 12.sp,
                                color = CyberTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CyberBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Q3: Why is something considered risky?
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenExplainableDecision("req_1") },
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(CyberCriticalRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QuestionMark, contentDescription = null, tint = CyberCriticalRed, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "3. Why is something considered risky?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF87171)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Inspect Why →", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "Production DB request from Mumbai detected unverified geolocation delta (>500km from Delhi HQ) on a CONFIDENTIAL resource. Tap to view full explainable audit.",
                                fontSize = 12.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Overall Zero Trust Posture & 4 Domain Security Scores
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenPostureDetail)
                    .testTag("posture_scores_overview_card"),
                shape = RoundedCornerShape(20.dp),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberSecureGreen))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("OVERALL ZERO TRUST POSTURE", style = MaterialTheme.typography.labelSmall, color = CyberCyan, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(text = "${posture.score}", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = CyberTextPrimary)
                                Text(text = " / 100", fontSize = 16.sp, color = CyberTextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceElevated)
                                .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = posture.overallPosture, color = CyberSecureGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CyberBorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("DOMAIN SECURITY SCORES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberTextMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Domain Scores
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DomainScorePill("Identity", posture.domainScores.identityScore, CyberBlue, Modifier.weight(1f))
                        DomainScorePill("Device", posture.domainScores.deviceScore, CyberCyan, Modifier.weight(1f))
                        DomainScorePill("Network", posture.domainScores.networkScore, CyberIndigo, Modifier.weight(1f))
                        DomainScorePill("Application", posture.domainScores.applicationScore, CyberSecureGreen, Modifier.weight(1f))
                    }
                }
            }
        }

        // CONTINUOUS TRUST VERIFICATION MODULE (Section 5)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("continuous_trust_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, if (continuousTrust.isChallenged) CyberCriticalRed else CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = if (continuousTrust.isChallenged) CyberCriticalRed else CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONTINUOUS TRUST ENGINE",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (continuousTrust.isChallenged) CyberCriticalRed else CyberCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "DYNAMIC VERIFICATION",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic score transition: Initial -> Current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("INITIAL SCORE", fontSize = 10.sp, color = CyberTextMuted, fontWeight = FontWeight.Bold)
                            Text("${continuousTrust.initialScore}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = CyberTextSecondary)
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Changes to",
                            tint = if (continuousTrust.isChallenged) CyberCriticalRed else CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text("CURRENT SCORE", fontSize = 10.sp, color = CyberTextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${continuousTrust.currentScore}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (continuousTrust.isChallenged) CyberCriticalRed else CyberSecureGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Condition: ${continuousTrust.contextShiftReason}",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (continuousTrust.isChallenged) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CyberCriticalRed.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, CyberCriticalRed)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("STEP-UP AUTHENTICATION REQUIRED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Trust dropped below safe threshold (80).", fontSize = 10.sp, color = Color(0xFFFFD1D1))
                                }
                                Button(
                                    onClick = onSatisfyContinuousChallenge,
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Verify", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Quick Simulation Controls
                        Text("Simulate contextual drift:", fontSize = 11.sp, color = CyberTextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onSimulateContextShift("Device switched to Unsecured Public Wi-Fi", 74, true) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp),
                                border = BorderStroke(1.dp, CyberBorder)
                            ) {
                                Text("Wi-Fi Shift (92→74)", fontSize = 10.sp, color = CyberWarningAmber)
                            }
                            OutlinedButton(
                                onClick = { onSimulateContextShift("Location Delta: Delhi to Mumbai (1,150km)", 58, true) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp),
                                border = BorderStroke(1.dp, CyberBorder)
                            ) {
                                Text("Geo Anomaly (92→58)", fontSize = 10.sp, color = CyberCriticalRed)
                            }
                        }
                    }
                }
            }
        }

        // ENTERPRISE ZERO TRUST MODULES HUB (Direct Access to all 13 features)
        item {
            SectionHeader(
                title = "Zero Trust Enterprise Hub",
                subtitle = "Security Modules & Decision Systems"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Signature Flow Hero Launcher (Section 12)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenSignatureFlow)
                        .testTag("launch_signature_flow"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                    border = BorderStroke(1.5.dp, CyberCyan.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountTree, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Signature Zero Trust Flow", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("CENTERPIECE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Interactive 9-node verification: Identity → MFA → Device → Decision", fontSize = 11.sp, color = CyberTextSecondary)
                            Text("\"Never trust. Always verify.\"", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.SemiBold)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan)
                    }
                }

                // Row 1: AI Analyst & Policy Simulator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleLauncherCard(
                        title = "AI Security Analyst",
                        subtitle = "DEMO SOC Intelligence",
                        icon = Icons.Default.SmartToy,
                        color = CyberBlue,
                        onClick = onOpenAnalyst,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleLauncherCard(
                        title = "Policy Simulator",
                        subtitle = "DEMO Policy Engine",
                        icon = Icons.Default.Tune,
                        color = CyberCyan,
                        onClick = onOpenPolicySimulator,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Analytics & RBAC
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleLauncherCard(
                        title = "Security Analytics",
                        subtitle = "Traffic & Request Trends",
                        icon = Icons.Default.Insights,
                        color = CyberIndigo,
                        onClick = onOpenAnalytics,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleLauncherCard(
                        title = "RBAC & Least Privilege",
                        subtitle = "5 Enterprise Roles Matrix",
                        icon = Icons.Default.AdminPanelSettings,
                        color = CyberSecureGreen,
                        onClick = onOpenRbac,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Scenario Demo & Install on Phone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleLauncherCard(
                        title = "Scenario Demo",
                        subtitle = "7-Stage Attack Simulation",
                        icon = Icons.Default.PlayCircle,
                        color = CyberWarningAmber,
                        onClick = onOpenScenario,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleLauncherCard(
                        title = "Install on Phone",
                        subtitle = "Direct Mobile APK Setup",
                        icon = Icons.Default.InstallMobile,
                        color = CyberCyan,
                        onClick = onOpenInstallDialog,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ACTIONABLE SECURITY RECOMMENDATIONS (Section 11)
        item {
            SectionHeader(
                title = "Security Recommendations",
                subtitle = "${unresolvedRecs.size} actionable posture optimizations",
                actionText = "Audit Trail",
                onActionClick = onOpenAuditLogs
            )
        }

        items(unresolvedRecs.take(3), key = { it.id }) { rec ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rec_card_${rec.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val priorityColor = when (rec.priority) {
                                RecommendationPriority.CRITICAL -> CyberCriticalRed
                                RecommendationPriority.HIGH -> Color(0xFFF87171)
                                RecommendationPriority.MEDIUM -> CyberWarningAmber
                                RecommendationPriority.LOW -> CyberBlue
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(priorityColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(rec.priority.name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = priorityColor)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(rec.impactScore, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberSecureGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(rec.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                        Text(rec.description, fontSize = 11.sp, color = CyberTextSecondary, maxLines = 2)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { onResolveRecommendation(rec.id, rec.target) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Act", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // RECENT SECURITY TIMELINE (Section 7)
        item {
            SectionHeader(
                title = "Recent Threat Timeline",
                subtitle = "Chronological SOC Telemetry Events",
                actionText = "View All",
                onActionClick = onOpenAuditLogs
            )
        }

        items(auditEvents.take(4), key = { it.id }) { event ->
            AuditEventCard(event = event)
        }

        // Live Threat Radar Scope
        item {
            ThreatRadarView(
                nodes = radarNodes,
                selectedNode = selectedRadarNode,
                onSelectNode = onSelectRadarNode
            )
        }

        // Active Access Requests Section (Section 2 & 14)
        item {
            SectionHeader(
                title = "Pending Access Decisions",
                subtitle = "${pendingRequests.size} requests awaiting verification",
                actionText = if (pendingRequests.isNotEmpty()) "Review All" else null,
                onActionClick = { onSelectTab(NavigationTab.ACCESS) }
            )
        }

        if (pendingRequests.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.CheckCircle,
                    title = "No Pending Access Requests",
                    message = "All enterprise resource access requests have been evaluated."
                )
            }
        } else {
            items(pendingRequests, key = { it.id }) { req ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("request_card_${req.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                    border = BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = req.resourceName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${req.deviceName} • ${req.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyberTextSecondary
                                )
                            }
                            RiskBadge(riskLevel = req.riskLevel)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Explainable Reason Button
                        OutlinedButton(
                            onClick = { onOpenExplainableDecision(req.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inspect Why? (Explainable Decision)", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onDenyRequest(req.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCriticalRed),
                                border = BorderStroke(1.dp, CyberCriticalRed.copy(alpha = 0.5f))
                            ) {
                                Text("DENY", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onApproveRequest(req.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                            ) {
                                Text("APPROVE", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // Lockdown Confirmation Dialog
    if (showLockdownConfirmDialog) {
        ConfirmationDialog(
            title = if (lockdownState.isActive) "Disengage Lockdown?" else "ENGAGE ZERO TRUST LOCKDOWN?",
            message = if (lockdownState.isActive)
                "This will restore standard Zero Trust policy thresholds and re-admit standard access flows."
            else
                "CRITICAL ACTION: This immediately quarantines all non-essential sessions, requires dual-custody verification for all access requests, and restricts privileged access tokens across enterprise enclaves.",
            confirmText = if (lockdownState.isActive) "Disengage" else "ENGAGE LOCKDOWN",
            cancelText = "Cancel",
            isDestructive = !lockdownState.isActive,
            onConfirm = {
                onToggleLockdown()
                showLockdownConfirmDialog = false
            },
            onDismiss = { showLockdownConfirmDialog = false }
        )
    }
}

@Composable
private fun DomainScorePill(title: String, score: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceElevated)
            .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 10.sp, color = CyberTextMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text("$score", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun ModuleLauncherCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("launch_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = CyberTextMuted, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = CyberTextMuted, maxLines = 1)
        }
    }
}
