package com.example.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.example.data.model.AuditCategory
import com.example.data.model.RequestStatus
import com.example.presentation.MainViewModel
import com.example.presentation.NavigationTab
import com.example.presentation.access.AccessScreen
import com.example.presentation.alerts.AlertsScreen
import com.example.presentation.analyst.AiSecurityAnalystScreen
import com.example.presentation.analytics.SecurityAnalyticsScreen
import com.example.presentation.components.AuditEventCard
import com.example.presentation.components.BiometricScanDialog
import com.example.presentation.components.ExplainableDecisionSheet
import com.example.presentation.components.InstallMobileDialog
import com.example.presentation.components.VercelDeployDialog
import com.example.presentation.dashboard.DashboardScreen
import com.example.presentation.decision.AccessDecisionScreen
import com.example.presentation.devices.DevicesScreen
import com.example.presentation.flow.SignatureZeroTrustFlowScreen
import com.example.presentation.policy.PolicySimulatorScreen
import com.example.presentation.posture.PostureDetailDialog
import com.example.presentation.profile.ProfileScreen
import com.example.presentation.rbac.RbacScreen
import com.example.presentation.scenario.SecurityScenarioScreen
import com.example.presentation.sessions.SessionsScreen
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val posture by viewModel.posture.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val accessRequests by viewModel.accessRequests.collectAsState()
    val resources by viewModel.resources.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val lockdownState by viewModel.lockdownState.collectAsState()
    val auditEvents by viewModel.auditEvents.collectAsState()
    val radarNodes by viewModel.radarNodes.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val rbacRoles by viewModel.rbacRoles.collectAsState()
    val continuousTrustState by viewModel.continuousTrustState.collectAsState()
    val analyticsData by viewModel.analyticsData.collectAsState()
    val scenarioStages = viewModel.scenarioStages
    val analystMessages by viewModel.analystMessages.collectAsState()

    val pendingRequestsCount = accessRequests.count { it.status == RequestStatus.PENDING }
    val activeAlertsCount = alerts.count { !it.isDismissed }

    // Auto-dismiss snackbar after 3.5s
    LaunchedEffect(uiState.snackbarMessage) {
        if (uiState.snackbarMessage != null) {
            delay(3500)
            viewModel.dismissSnackbar()
        }
    }

    val isAnySubscreenOpen = uiState.showDecisionVisualizer ||
            uiState.showPostureDetail ||
            uiState.showAuditLogsDialog ||
            uiState.showInstallDialog ||
            uiState.showVercelDialog ||
            uiState.showFlowScreen ||
            uiState.showPolicySimulator ||
            uiState.showAnalyticsScreen ||
            uiState.showAnalystScreen ||
            uiState.showRbacScreen ||
            uiState.showScenarioScreen ||
            uiState.selectedExplainableDecision != null

    // Handle back button on sub-screens
    BackHandler(enabled = isAnySubscreenOpen || uiState.selectedTab != NavigationTab.DASHBOARD) {
        when {
            uiState.showFlowScreen -> viewModel.closeSignatureFlow()
            uiState.showPolicySimulator -> viewModel.closePolicySimulator()
            uiState.showAnalyticsScreen -> viewModel.closeAnalytics()
            uiState.showAnalystScreen -> viewModel.closeAnalyst()
            uiState.showRbacScreen -> viewModel.closeRbac()
            uiState.showScenarioScreen -> viewModel.closeScenario()
            uiState.showDecisionVisualizer -> viewModel.closeDecisionVisualizer()
            uiState.selectedExplainableDecision != null -> viewModel.closeExplainableDecision()
            uiState.showPostureDetail -> viewModel.closePostureDetail()
            uiState.showAuditLogsDialog -> viewModel.closeAuditLogs()
            uiState.showInstallDialog -> viewModel.closeInstallDialog()
            uiState.showVercelDialog -> viewModel.closeVercelDialog()
            else -> viewModel.selectTab(NavigationTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_scaffold"),
        containerColor = CyberBackground,
        bottomBar = {
            if (!isAnySubscreenOpen) {
                NavigationBar(
                    containerColor = CyberSurfaceElevated,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = uiState.selectedTab == NavigationTab.DASHBOARD,
                        onClick = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                        icon = {
                            Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")
                        },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        colors = navigationBarColors()
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == NavigationTab.DEVICES,
                        onClick = { viewModel.selectTab(NavigationTab.DEVICES) },
                        icon = {
                            Icon(Icons.Default.PhonelinkLock, contentDescription = "Devices")
                        },
                        label = { Text("Devices", fontSize = 11.sp) },
                        colors = navigationBarColors()
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == NavigationTab.ACCESS,
                        onClick = { viewModel.selectTab(NavigationTab.ACCESS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingRequestsCount > 0) {
                                        Badge(
                                            containerColor = CyberWarningAmber,
                                            contentColor = Color.Black
                                        ) {
                                            Text("$pendingRequestsCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.VpnKey, contentDescription = "Access")
                            }
                        },
                        label = { Text("Access", fontSize = 11.sp) },
                        colors = navigationBarColors()
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == NavigationTab.ALERTS,
                        onClick = { viewModel.selectTab(NavigationTab.ALERTS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (activeAlertsCount > 0) {
                                        Badge(
                                            containerColor = CyberCriticalRed,
                                            contentColor = Color.White
                                        ) {
                                            Text("$activeAlertsCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                            }
                        },
                        label = { Text("Alerts", fontSize = 11.sp) },
                        colors = navigationBarColors()
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == NavigationTab.PROFILE,
                        onClick = { viewModel.selectTab(NavigationTab.PROFILE) },
                        icon = {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = navigationBarColors()
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Section 12: Signature Zero Trust Flow
                uiState.showFlowScreen -> {
                    SignatureZeroTrustFlowScreen(
                        onBack = { viewModel.closeSignatureFlow() }
                    )
                }

                // Section 3: Policy Simulator
                uiState.showPolicySimulator -> {
                    PolicySimulatorScreen(
                        onBack = { viewModel.closePolicySimulator() }
                    )
                }

                // Section 4: Security Analytics
                uiState.showAnalyticsScreen -> {
                    SecurityAnalyticsScreen(
                        analyticsData = analyticsData,
                        onBack = { viewModel.closeAnalytics() }
                    )
                }

                // Section 6: AI Security Analyst
                uiState.showAnalystScreen -> {
                    AiSecurityAnalystScreen(
                        messages = analystMessages,
                        onSendMessage = { viewModel.sendAnalystMessage(it) },
                        onBack = { viewModel.closeAnalyst() }
                    )
                }

                // Section 8: RBAC & Least Privilege
                uiState.showRbacScreen -> {
                    RbacScreen(
                        roles = rbacRoles,
                        onBack = { viewModel.closeRbac() }
                    )
                }

                // Section 10: Security Scenario Demo
                uiState.showScenarioScreen -> {
                    SecurityScenarioScreen(
                        stages = scenarioStages,
                        onBack = { viewModel.closeScenario() }
                    )
                }

                // Explainable Decision Visualizer
                uiState.showDecisionVisualizer -> {
                    AccessDecisionScreen(
                        decisionResult = uiState.decisionResult,
                        isEvaluating = uiState.isEvaluatingDecision,
                        onRecalculate = { res, loc, dev, mfa ->
                            viewModel.calculateDecision(res, loc, dev, mfa)
                        },
                        onTriggerBiometricStepUp = {
                            viewModel.startBiometricVerification {
                                viewModel.calculateDecision("Production Database (PostgreSQL)", isMfaVerified = true)
                            }
                        },
                        onClose = { viewModel.closeDecisionVisualizer() }
                    )
                }

                // Primary Bottom-Nav Tabs
                else -> {
                    when (uiState.selectedTab) {
                        NavigationTab.DASHBOARD -> {
                            DashboardScreen(
                                posture = posture,
                                devices = devices,
                                accessRequests = accessRequests,
                                alerts = alerts,
                                sessions = sessions,
                                lockdownState = lockdownState,
                                radarNodes = radarNodes,
                                recommendations = recommendations,
                                continuousTrust = continuousTrustState,
                                auditEvents = auditEvents,
                                selectedRadarNode = uiState.selectedRadarNode,
                                onSelectRadarNode = { viewModel.selectRadarNode(it) },
                                onToggleLockdown = { viewModel.toggleLockdown() },
                                onOpenAuditLogs = { viewModel.openAuditLogs() },
                                onOpenInstallDialog = { viewModel.openInstallDialog() },
                                onOpenVercelDialog = { viewModel.openVercelDialog() },
                                onSelectTab = { viewModel.selectTab(it) },
                                onOpenPostureDetail = { viewModel.openPostureDetail() },
                                onOpenDecisionVisualizer = { viewModel.openDecisionVisualizer(it) },
                                onOpenExplainableDecision = { viewModel.openExplainableDecision(it) },
                                onOpenSignatureFlow = { viewModel.openSignatureFlow() },
                                onOpenPolicySimulator = { viewModel.openPolicySimulator() },
                                onOpenAnalytics = { viewModel.openAnalytics() },
                                onOpenAnalyst = { viewModel.openAnalyst() },
                                onOpenRbac = { viewModel.openRbac() },
                                onOpenScenario = { viewModel.openScenario() },
                                onSimulateContextShift = { reason, score, challenge ->
                                    viewModel.simulateContextShift(reason, score, challenge)
                                },
                                onSatisfyContinuousChallenge = { viewModel.satisfyContinuousChallenge() },
                                onResolveRecommendation = { id, target ->
                                    viewModel.resolveRecommendation(id, target)
                                },
                                onSelectRequest = { viewModel.selectRequest(it) },
                                onApproveRequest = { viewModel.approveRequest(it) },
                                onDenyRequest = { viewModel.denyRequest(it) }
                            )
                        }

                        NavigationTab.DEVICES -> {
                            DevicesScreen(
                                devices = devices,
                                selectedDevice = uiState.selectedDevice,
                                onSelectDevice = { viewModel.selectDevice(it) },
                                onToggleTrust = { viewModel.toggleDeviceTrust(it) }
                            )
                        }

                        NavigationTab.ACCESS -> {
                            AccessScreen(
                                resources = resources,
                                accessRequests = accessRequests,
                                auditEvents = auditEvents,
                                selectedCategory = uiState.resourceCategoryFilter,
                                auditFilter = uiState.auditCategoryFilter,
                                searchQuery = uiState.searchQuery,
                                selectedResource = uiState.selectedResource,
                                selectedRequest = uiState.selectedRequest,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onSelectCategory = { viewModel.setResourceCategoryFilter(it) },
                                onAuditFilterChange = { viewModel.setAuditCategoryFilter(it) },
                                onSelectResource = { viewModel.selectResource(it) },
                                onSelectRequest = { viewModel.selectRequest(it) },
                                onApproveRequest = { viewModel.approveRequest(it) },
                                onDenyRequest = { viewModel.denyRequest(it) },
                                onRequestResourceAccess = { viewModel.requestResourceAccess(it) }
                            )
                        }

                        NavigationTab.ALERTS -> {
                            AlertsScreen(
                                alerts = alerts,
                                selectedFilter = uiState.alertFilter,
                                selectedAlert = uiState.selectedAlert,
                                onFilterChange = { viewModel.setAlertFilter(it) },
                                onSelectAlert = { viewModel.selectAlert(it) },
                                onDismissAlert = { viewModel.dismissAlert(it) },
                                onBlockDevice = { viewModel.blockDeviceFromAlert(it) }
                            )
                        }

                        NavigationTab.PROFILE -> {
                            ProfileScreen(
                                settings = settings,
                                trustedDevicesCount = devices.count { it.isTrusted },
                                activeSessionsCount = sessions.size,
                                onUpdateSettings = { viewModel.updateSettings(it) },
                                onOpenInstallDialog = { viewModel.openInstallDialog() },
                                onOpenVercelDialog = { viewModel.openVercelDialog() },
                                onResetDemoData = { viewModel.resetDemoData() },
                                onSignOut = onSignOut
                            )
                        }
                    }
                }
            }

            // Section 2: Explainable Access Decision Sheet
            if (uiState.selectedExplainableDecision != null) {
                ExplainableDecisionSheet(
                    decision = uiState.selectedExplainableDecision!!,
                    onDismiss = { viewModel.closeExplainableDecision() },
                    onApprove = {
                        viewModel.approveRequest(uiState.selectedExplainableDecision!!.requestId)
                        viewModel.closeExplainableDecision()
                    },
                    onDeny = {
                        viewModel.denyRequest(uiState.selectedExplainableDecision!!.requestId)
                        viewModel.closeExplainableDecision()
                    }
                )
            }

            // Install on Mobile Phone Dialog (Request #2)
            if (uiState.showInstallDialog) {
                InstallMobileDialog(
                    onDismiss = { viewModel.closeInstallDialog() },
                    onEnrollCurrentDevice = {
                        viewModel.completeDeviceTrust(true)
                    }
                )
            }

            // Vercel Cloud Web Deployment Dialog
            if (uiState.showVercelDialog) {
                VercelDeployDialog(
                    onDismiss = { viewModel.closeVercelDialog() }
                )
            }

            // Posture Detail Dialog
            if (uiState.showPostureDetail) {
                PostureDetailDialog(
                    posture = posture,
                    onDismiss = { viewModel.closePostureDetail() },
                    onOpenDecisionEngine = {
                        viewModel.closePostureDetail()
                        viewModel.openDecisionVisualizer()
                    }
                )
            }

            // Standalone Audit Logs Dialog
            if (uiState.showAuditLogsDialog) {
                AuditLogsDialog(
                    auditEvents = auditEvents,
                    onDismiss = { viewModel.closeAuditLogs() }
                )
            }

            // Interactive Biometric Scanning Modal
            if (uiState.showBiometricScanDialog) {
                BiometricScanDialog(
                    isScanning = uiState.biometricScanning,
                    isSuccess = uiState.biometricScanSuccess,
                    onDismiss = { viewModel.dismissBiometricDialog() }
                )
            }

            // In-app Notification / Feedback Snackbar
            AnimatedVisibility(
                visible = uiState.snackbarMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("in_app_feedback_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.snackbarMessage ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogsDialog(
    auditEvents: List<com.example.data.model.AuditEvent>,
    onDismiss: () -> Unit
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
                    Text("Security Audit Trail", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                    Text("Immutable SOC Telemetry Log", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                }
                Icon(Icons.Default.History, contentDescription = null, tint = CyberCyan)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(auditEvents, key = { it.id }) { event ->
                    AuditEventCard(event = event)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)) {
                Text("Close")
            }
        },
        containerColor = CyberSurfaceElevated,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun navigationBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = CyberCyan,
    selectedTextColor = CyberCyan,
    unselectedIconColor = CyberTextSecondary,
    unselectedTextColor = CyberTextSecondary,
    indicatorColor = CyberBlue.copy(alpha = 0.2f)
)
