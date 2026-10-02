package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.SecurityRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppDestination {
    SPLASH,
    LOGIN,
    MFA,
    DEVICE_TRUST,
    MAIN_APP
}

enum class NavigationTab(val title: String) {
    DASHBOARD("Dashboard"),
    DEVICES("Devices"),
    ACCESS("Access"),
    ALERTS("Alerts"),
    PROFILE("Profile")
}

data class UiState(
    val currentDestination: AppDestination = AppDestination.SPLASH,
    val selectedTab: NavigationTab = NavigationTab.DASHBOARD,
    val selectedDevice: Device? = null,
    val selectedRequest: AccessRequest? = null,
    val selectedAlert: SecurityAlert? = null,
    val selectedResource: ProtectedResource? = null,
    val showDecisionVisualizer: Boolean = false,
    val showPostureDetail: Boolean = false,
    val showTerminateAllDialog: Boolean = false,
    val showRadarDialog: Boolean = false,
    val showAuditLogsDialog: Boolean = false,
    val showInstallDialog: Boolean = false,
    val showVercelDialog: Boolean = false,
    val showBiometricScanDialog: Boolean = false,
    val showFlowScreen: Boolean = false,
    val showPolicySimulator: Boolean = false,
    val showAnalyticsScreen: Boolean = false,
    val showAnalystScreen: Boolean = false,
    val showRbacScreen: Boolean = false,
    val showScenarioScreen: Boolean = false,
    val selectedExplainableDecision: ExplainableDecision? = null,
    val biometricScanning: Boolean = false,
    val biometricScanSuccess: Boolean = false,
    val selectedRadarNode: RadarThreatNode? = null,
    val alertFilter: RiskLevel? = null,
    val resourceCategoryFilter: ResourceCategory? = null,
    val auditCategoryFilter: AuditCategory? = null,
    val searchQuery: String = "",
    val decisionResult: AccessDecisionResult? = null,
    val isEvaluatingDecision: Boolean = false,
    val snackbarMessage: String? = null
)

class MainViewModel(
    val repository: SecurityRepository = SecurityRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val posture = repository.posture
    val devices = repository.devices
    val accessRequests = repository.accessRequests
    val resources = repository.resources
    val alerts = repository.alerts
    val sessions = repository.sessions
    val settings = repository.settings
    val lockdownState = repository.lockdownState
    val auditEvents = repository.auditEvents
    val radarNodes = repository.radarNodes
    val recommendations = repository.recommendations
    val rbacRoles = repository.rbacRoles
    val continuousTrustState = repository.continuousTrustState
    val analyticsData = repository.analyticsData
    val scenarioStages = repository.scenarioStages
    val analystMessages = repository.analystMessages

    init {
        viewModelScope.launch {
            repository.userFeedback.collect { message ->
                _uiState.update { it.copy(snackbarMessage = message) }
            }
        }
        calculateDecision("Production Database (PostgreSQL)")
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun onSplashFinished() {
        _uiState.update { it.copy(currentDestination = AppDestination.LOGIN) }
    }

    fun login(email: String, pass: String): Boolean {
        val success = repository.signIn(email, pass)
        if (success) {
            repository.recordAuditEvent(
                category = AuditCategory.AUTH,
                actionSummary = "Successful primary credential authentication for $email",
                severity = RiskLevel.LOW
            )
            _uiState.update { it.copy(currentDestination = AppDestination.MFA) }
        }
        return success
    }

    fun verifyMfa(code: String): Boolean {
        val success = repository.verifyMfa(code)
        if (success) {
            repository.recordAuditEvent(
                category = AuditCategory.AUTH,
                actionSummary = "Step-up MFA verified with 6-digit TOTP",
                severity = RiskLevel.LOW
            )
            _uiState.update { it.copy(currentDestination = AppDestination.DEVICE_TRUST) }
        }
        return success
    }

    fun startBiometricVerification(onSuccessAction: () -> Unit) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showBiometricScanDialog = true,
                    biometricScanning = true,
                    biometricScanSuccess = false
                )
            }
            delay(1400) // Realistic sensor pulse
            _uiState.update {
                it.copy(
                    biometricScanning = false,
                    biometricScanSuccess = true
                )
            }
            delay(600)
            _uiState.update { it.copy(showBiometricScanDialog = false) }
            repository.recordAuditEvent(
                category = AuditCategory.AUTH,
                actionSummary = "Biometric FIDO2 hardware enclave key successfully unlocked",
                severity = RiskLevel.LOW
            )
            onSuccessAction()
        }
    }

    fun dismissBiometricDialog() {
        _uiState.update {
            it.copy(
                showBiometricScanDialog = false,
                biometricScanning = false,
                biometricScanSuccess = false
            )
        }
    }

    fun completeDeviceTrust(trusted: Boolean) {
        repository.completeDeviceTrust(trusted)
        repository.recordAuditEvent(
            category = AuditCategory.ADMIN,
            actionSummary = if (trusted) "Device Pixel 9 Pro verified and enrolled as Trusted" else "Device enrolled with Limited Trust boundary",
            severity = if (trusted) RiskLevel.LOW else RiskLevel.MEDIUM
        )
        _uiState.update { it.copy(currentDestination = AppDestination.MAIN_APP) }
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                showDecisionVisualizer = false,
                showPostureDetail = false,
                showRadarDialog = false,
                showAuditLogsDialog = false,
                showInstallDialog = false,
                showVercelDialog = false,
                showFlowScreen = false,
                showPolicySimulator = false,
                showAnalyticsScreen = false,
                showAnalystScreen = false,
                showRbacScreen = false,
                showScenarioScreen = false,
                selectedExplainableDecision = null,
                selectedDevice = null,
                selectedRequest = null,
                selectedAlert = null,
                selectedResource = null,
                searchQuery = ""
            )
        }
    }

    fun openSignatureFlow() {
        _uiState.update { it.copy(showFlowScreen = true) }
    }

    fun closeSignatureFlow() {
        _uiState.update { it.copy(showFlowScreen = false) }
    }

    fun openPolicySimulator() {
        _uiState.update { it.copy(showPolicySimulator = true) }
    }

    fun closePolicySimulator() {
        _uiState.update { it.copy(showPolicySimulator = false) }
    }

    fun openAnalytics() {
        _uiState.update { it.copy(showAnalyticsScreen = true) }
    }

    fun closeAnalytics() {
        _uiState.update { it.copy(showAnalyticsScreen = false) }
    }

    fun openAnalyst() {
        _uiState.update { it.copy(showAnalystScreen = true) }
    }

    fun closeAnalyst() {
        _uiState.update { it.copy(showAnalystScreen = false) }
    }

    fun openRbac() {
        _uiState.update { it.copy(showRbacScreen = true) }
    }

    fun closeRbac() {
        _uiState.update { it.copy(showRbacScreen = false) }
    }

    fun openScenario() {
        _uiState.update { it.copy(showScenarioScreen = true) }
    }

    fun closeScenario() {
        _uiState.update { it.copy(showScenarioScreen = false) }
    }

    fun openExplainableDecision(requestId: String) {
        val decision = repository.getExplainableDecision(requestId)
        _uiState.update { it.copy(selectedExplainableDecision = decision) }
    }

    fun closeExplainableDecision() {
        _uiState.update { it.copy(selectedExplainableDecision = null) }
    }

    fun simulateContextShift(reason: String, newScore: Int, challenge: Boolean) {
        repository.simulateContextShift(reason, newScore, challenge)
    }

    fun satisfyContinuousChallenge() {
        startBiometricVerification {
            repository.satisfyContinuousChallenge()
        }
    }

    fun sendAnalystMessage(query: String) {
        repository.askAnalyst(query)
    }

    fun resolveRecommendation(id: String, target: RecommendationTarget) {
        repository.resolveRecommendation(id)
        when (target) {
            RecommendationTarget.DEVICES -> selectTab(NavigationTab.DEVICES)
            RecommendationTarget.REQUESTS -> selectTab(NavigationTab.ACCESS)
            RecommendationTarget.SESSIONS -> selectTab(NavigationTab.PROFILE)
            RecommendationTarget.MFA -> startBiometricVerification { }
            RecommendationTarget.POSTURE -> openPostureDetail()
            RecommendationTarget.LOCKDOWN -> toggleLockdown()
        }
    }

    fun openDecisionVisualizer(resourceName: String = "Production Database") {
        calculateDecision(resourceName)
        _uiState.update { it.copy(showDecisionVisualizer = true) }
    }

    fun closeDecisionVisualizer() {
        _uiState.update { it.copy(showDecisionVisualizer = false) }
    }

    fun openPostureDetail() {
        _uiState.update { it.copy(showPostureDetail = true) }
    }

    fun closePostureDetail() {
        _uiState.update { it.copy(showPostureDetail = false) }
    }

    fun openRadar() {
        _uiState.update { it.copy(showRadarDialog = true) }
    }

    fun closeRadar() {
        _uiState.update { it.copy(showRadarDialog = false, selectedRadarNode = null) }
    }

    fun selectRadarNode(node: RadarThreatNode?) {
        _uiState.update { it.copy(selectedRadarNode = node) }
    }

    fun openAuditLogs() {
        _uiState.update { it.copy(showAuditLogsDialog = true) }
    }

    fun closeAuditLogs() {
        _uiState.update { it.copy(showAuditLogsDialog = false) }
    }

    fun openInstallDialog() {
        _uiState.update { it.copy(showInstallDialog = true) }
    }

    fun closeInstallDialog() {
        _uiState.update { it.copy(showInstallDialog = false) }
    }

    fun openVercelDialog() {
        _uiState.update { it.copy(showVercelDialog = true) }
    }

    fun closeVercelDialog() {
        _uiState.update { it.copy(showVercelDialog = false) }
    }

    fun setAuditCategoryFilter(category: AuditCategory?) {
        _uiState.update { it.copy(auditCategoryFilter = category) }
    }

    fun toggleLockdown() {
        repository.toggleEmergencyLockdown()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectDevice(device: Device?) {
        _uiState.update { it.copy(selectedDevice = device) }
    }

    fun selectRequest(request: AccessRequest?) {
        _uiState.update { it.copy(selectedRequest = request) }
    }

    fun selectAlert(alert: SecurityAlert?) {
        _uiState.update { it.copy(selectedAlert = alert) }
    }

    fun selectResource(resource: ProtectedResource?) {
        _uiState.update { it.copy(selectedResource = resource) }
    }

    fun setAlertFilter(filter: RiskLevel?) {
        _uiState.update { it.copy(alertFilter = filter) }
    }

    fun setResourceCategoryFilter(category: ResourceCategory?) {
        _uiState.update { it.copy(resourceCategoryFilter = category) }
    }

    fun setShowTerminateAllDialog(show: Boolean) {
        _uiState.update { it.copy(showTerminateAllDialog = show) }
    }

    fun approveRequest(id: String) {
        repository.approveAccessRequest(id)
        repository.recordAuditEvent(
            category = AuditCategory.ACCESS,
            actionSummary = "Admin approved JIT access request $id",
            severity = RiskLevel.MEDIUM
        )
        _uiState.update { it.copy(selectedRequest = null) }
    }

    fun denyRequest(id: String) {
        repository.denyAccessRequest(id)
        repository.recordAuditEvent(
            category = AuditCategory.ACCESS,
            actionSummary = "Admin DENIED access request $id under Zero Trust policy",
            severity = RiskLevel.HIGH
        )
        _uiState.update { it.copy(selectedRequest = null) }
    }

    fun toggleDeviceTrust(id: String) {
        repository.toggleDeviceTrust(id)
        val updated = repository.devices.value.find { it.id == id }
        _uiState.update { it.copy(selectedDevice = updated) }
    }

    fun dismissAlert(id: String) {
        repository.dismissAlert(id)
        _uiState.update { it.copy(selectedAlert = null) }
    }

    fun blockDeviceFromAlert(id: String) {
        repository.blockDeviceFromAlert(id)
        repository.recordAuditEvent(
            category = AuditCategory.THREAT,
            actionSummary = "Endpoint blocked and network traffic isolated from alert $id",
            severity = RiskLevel.CRITICAL
        )
        _uiState.update { it.copy(selectedAlert = null) }
    }

    fun terminateSession(id: String) {
        repository.terminateSession(id)
    }

    fun terminateAllOtherSessions() {
        repository.terminateAllOtherSessions()
        _uiState.update { it.copy(showTerminateAllDialog = false) }
    }

    fun requestResourceAccess(resourceId: String) {
        repository.requestResourceAccess(resourceId)
        val updated = repository.resources.value.find { it.id == resourceId }
        _uiState.update { it.copy(selectedResource = updated) }
    }

    fun updateSettings(settings: UserSecuritySettings) {
        repository.updateSecuritySettings(settings)
    }

    fun resetDemoData() {
        repository.resetDemoData()
        _uiState.update {
            it.copy(
                selectedDevice = null,
                selectedRequest = null,
                selectedAlert = null,
                selectedResource = null,
                showDecisionVisualizer = false,
                showPostureDetail = false,
                showRadarDialog = false,
                showAuditLogsDialog = false,
                showInstallDialog = false,
                showVercelDialog = false,
                showFlowScreen = false,
                showPolicySimulator = false,
                showAnalyticsScreen = false,
                showAnalystScreen = false,
                showRbacScreen = false,
                showScenarioScreen = false,
                selectedExplainableDecision = null,
                searchQuery = ""
            )
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.update {
            it.copy(
                currentDestination = AppDestination.LOGIN,
                selectedTab = NavigationTab.DASHBOARD
            )
        }
    }

    fun calculateDecision(
        resourceName: String,
        location: String = "Mumbai, IN",
        isKnownDevice: Boolean = true,
        isMfaVerified: Boolean = true
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEvaluatingDecision = true) }
            delay(350)
            val result = repository.evaluateZeroTrustDecision(
                resourceName = resourceName,
                location = location,
                isKnownDevice = isKnownDevice,
                isMfaVerified = isMfaVerified
            )
            _uiState.update {
                it.copy(
                    decisionResult = result,
                    isEvaluatingDecision = false
                )
            }
        }
    }
}
