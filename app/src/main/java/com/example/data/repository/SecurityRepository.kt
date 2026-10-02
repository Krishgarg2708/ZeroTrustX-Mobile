package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SecurityRepository {

    // Auth State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isMfaVerified = MutableStateFlow(false)
    val isMfaVerified: StateFlow<Boolean> = _isMfaVerified.asStateFlow()

    private val _isCurrentDeviceTrusted = MutableStateFlow(true)
    val isCurrentDeviceTrusted: StateFlow<Boolean> = _isCurrentDeviceTrusted.asStateFlow()

    // Notification Feedback Flow
    private val _userFeedback = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val userFeedback: SharedFlow<String> = _userFeedback.asSharedFlow()

    // Data Collections
    private val _posture = MutableStateFlow(createInitialPosture())
    val posture: StateFlow<SecurityPosture> = _posture.asStateFlow()

    private val _devices = MutableStateFlow(createInitialDevices())
    val devices: StateFlow<List<Device>> = _devices.asStateFlow()

    private val _accessRequests = MutableStateFlow(createInitialAccessRequests())
    val accessRequests: StateFlow<List<AccessRequest>> = _accessRequests.asStateFlow()

    private val _resources = MutableStateFlow(createInitialResources())
    val resources: StateFlow<List<ProtectedResource>> = _resources.asStateFlow()

    private val _alerts = MutableStateFlow(createInitialAlerts())
    val alerts: StateFlow<List<SecurityAlert>> = _alerts.asStateFlow()

    private val _sessions = MutableStateFlow(createInitialSessions())
    val sessions: StateFlow<List<ActiveSession>> = _sessions.asStateFlow()

    private val _settings = MutableStateFlow(UserSecuritySettings())
    val settings: StateFlow<UserSecuritySettings> = _settings.asStateFlow()

    private val _lockdownState = MutableStateFlow(LockdownState())
    val lockdownState: StateFlow<LockdownState> = _lockdownState.asStateFlow()

    private val _auditEvents = MutableStateFlow(createInitialAuditEvents())
    val auditEvents: StateFlow<List<AuditEvent>> = _auditEvents.asStateFlow()

    private val _radarNodes = MutableStateFlow(createInitialRadarNodes())
    val radarNodes: StateFlow<List<RadarThreatNode>> = _radarNodes.asStateFlow()

    private val _recommendations = MutableStateFlow(SecurityDataMocks.createInitialRecommendations())
    val recommendations: StateFlow<List<SecurityRecommendation>> = _recommendations.asStateFlow()

    private val _rbacRoles = MutableStateFlow(SecurityDataMocks.createInitialRbacRoles())
    val rbacRoles: StateFlow<List<RbacRole>> = _rbacRoles.asStateFlow()

    private val _continuousTrustState = MutableStateFlow(ContinuousTrustState())
    val continuousTrustState: StateFlow<ContinuousTrustState> = _continuousTrustState.asStateFlow()

    private val _analyticsData = MutableStateFlow(SecurityAnalyticsSummary())
    val analyticsData: StateFlow<SecurityAnalyticsSummary> = _analyticsData.asStateFlow()

    val scenarioStages: List<ScenarioSimulationStage> = SecurityDataMocks.createScenarioStages()

    private val _analystMessages = MutableStateFlow(
        listOf(
            AnalystMessage(
                id = "welcome",
                senderIsUser = false,
                text = "Welcome to ZeroTrustX AI Security Analyst (DEMO).\n\nAsk me about posture ratings, explainable access decisions, least-privilege policies, or active threat mitigations.",
                timestamp = "Just now",
                suggestedQueries = listOf(
                    "Explain my posture score",
                    "Why is Production DB flagged?",
                    "Summarize active threats",
                    "What is Least Privilege?"
                ),
                references = listOf("NIST SP 800-207 Zero Trust Architecture")
            )
        )
    )
    val analystMessages: StateFlow<List<AnalystMessage>> = _analystMessages.asStateFlow()

    private val _explainableDecisions = MutableStateFlow(SecurityDataMocks.createInitialExplainableDecisions())

    // Auth actions
    fun signIn(email: String, pass: String): Boolean {
        // Prototype credential support
        val normalizedEmail = email.trim()
        val isValid = (normalizedEmail == "admin@zerotrustx.demo" && pass == "Demo@123") ||
                (normalizedEmail.contains("@") && pass.length >= 4)
        if (isValid) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }

    fun verifyMfa(code: String): Boolean {
        if (code == "482169" || code.length == 6) {
            _isMfaVerified.value = true
            return true
        }
        return false
    }

    fun completeDeviceTrust(trusted: Boolean) {
        _isCurrentDeviceTrusted.value = trusted
        _userFeedback.tryEmit(if (trusted) "✓ Device enrolled as Trusted" else "⚠ Enrolled with Limited Trust")
    }

    fun signOut() {
        _isAuthenticated.value = false
        _isMfaVerified.value = false
    }

    // Access Request Actions
    fun approveAccessRequest(id: String) {
        _accessRequests.update { list ->
            list.map { req ->
                if (req.id == id) req.copy(status = RequestStatus.APPROVED) else req
            }
        }
        val request = _accessRequests.value.find { it.id == id }
        val name = request?.resourceName ?: "Resource"
        _userFeedback.tryEmit("✓ Access Approved: $name granted for 30 minutes")
        recalculatePosture()
    }

    fun denyAccessRequest(id: String) {
        _accessRequests.update { list ->
            list.map { req ->
                if (req.id == id) req.copy(status = RequestStatus.DENIED) else req
            }
        }
        val request = _accessRequests.value.find { it.id == id }
        val name = request?.resourceName ?: "Resource"
        _userFeedback.tryEmit("✓ Access Denied: $name request terminated")
        recalculatePosture()
    }

    // Device Actions
    fun toggleDeviceTrust(deviceId: String) {
        var newStatus = false
        var deviceName = ""
        _devices.update { list ->
            list.map { dev ->
                if (dev.id == deviceId) {
                    deviceName = dev.name
                    newStatus = !dev.isTrusted
                    dev.copy(isTrusted = newStatus)
                } else dev
            }
        }
        if (newStatus) {
            _userFeedback.tryEmit("✓ Device Trust Granted: $deviceName is now trusted")
        } else {
            _userFeedback.tryEmit("✓ Device Trust Revoked: $deviceName must re-authenticate")
        }
        recalculatePosture()
    }

    // Alert Actions
    fun dismissAlert(alertId: String) {
        _alerts.update { list ->
            list.map { alert ->
                if (alert.id == alertId) alert.copy(isDismissed = true) else alert
            }
        }
        _userFeedback.tryEmit("✓ Alert dismissed from active queue")
        recalculatePosture()
    }

    fun blockDeviceFromAlert(alertId: String) {
        var devName = ""
        _alerts.update { list ->
            list.map { alert ->
                if (alert.id == alertId) {
                    devName = alert.deviceName
                    alert.copy(isDeviceBlocked = true, isDismissed = true)
                } else alert
            }
        }
        // Also revoke trust if in devices list
        _devices.update { list ->
            list.map { dev ->
                if (dev.name.contains(devName, ignoreCase = true)) {
                    dev.copy(isTrusted = false)
                } else dev
            }
        }
        _userFeedback.tryEmit("✓ Device Blocked: $devName network traffic isolated")
        recalculatePosture()
    }

    // Session Actions
    fun terminateSession(sessionId: String) {
        val session = _sessions.value.find { it.id == sessionId }
        val client = session?.clientInfo ?: "Session"
        _sessions.update { list -> list.filterNot { it.id == sessionId } }
        _userFeedback.tryEmit("✓ Session Terminated: $client disconnected")
        recalculatePosture()
    }

    fun terminateAllOtherSessions() {
        val countBefore = _sessions.value.count { !it.isCurrent }
        _sessions.update { list -> list.filter { it.isCurrent } }
        _userFeedback.tryEmit("✓ Terminated $countBefore active remote sessions")
        recalculatePosture()
    }

    // Resource Actions
    fun requestResourceAccess(resourceId: String) {
        _resources.update { list ->
            list.map { res ->
                if (res.id == resourceId) res.copy(accessStatus = ResourceAccessStatus.GRANTED) else res
            }
        }
        val res = _resources.value.find { it.id == resourceId }
        val name = res?.name ?: "Resource"
        _userFeedback.tryEmit("✓ Temporary Access Granted: $name (30 min token issued)")
    }

    fun updateSecuritySettings(newSettings: UserSecuritySettings) {
        _settings.value = newSettings
        _userFeedback.tryEmit("✓ Security settings updated successfully")
        recalculatePosture()
    }

    fun toggleEmergencyLockdown(reason: String = "Enterprise High-Threat Quarantine") {
        val currentlyActive = _lockdownState.value.isActive
        if (!currentlyActive) {
            _lockdownState.value = LockdownState(
                isActive = true,
                initiatedAt = "Just now",
                reason = reason,
                isolatedCount = _sessions.value.count { !it.isCurrent }
            )
            // Escalate security posture
            _posture.update {
                it.copy(
                    score = 99,
                    statusText = "Lockdown Active",
                    overallPosture = "MAXIMUM SECURITY"
                )
            }
            recordAuditEvent(
                category = AuditCategory.ADMIN,
                actionSummary = "EMERGENCY ZERO TRUST LOCKDOWN ENGAGED: Non-essential tokens isolated",
                severity = RiskLevel.CRITICAL,
                actor = "alex@company.com",
                ip = "10.0.4.82",
                location = "Delhi, IN"
            )
            _userFeedback.tryEmit("🚨 EMERGENCY LOCKDOWN ENGAGED: Dual-custody step-up required for all sessions")
        } else {
            _lockdownState.value = LockdownState(isActive = false)
            recalculatePosture()
            recordAuditEvent(
                category = AuditCategory.ADMIN,
                actionSummary = "Zero Trust Lockdown Disengaged by Security Administrator",
                severity = RiskLevel.MEDIUM,
                actor = "alex@company.com",
                ip = "10.0.4.82",
                location = "Delhi, IN"
            )
            _userFeedback.tryEmit("✓ Emergency Lockdown Disengaged: Normal zero trust policies restored")
        }
    }

    fun recordAuditEvent(
        category: AuditCategory,
        actionSummary: String,
        severity: RiskLevel,
        actor: String = "alex@company.com",
        ip: String = "10.0.4.82",
        location: String = "Delhi, IN"
    ) {
        val newEvent = AuditEvent(
            id = "audit_${System.currentTimeMillis()}",
            timestamp = "Just now",
            category = category,
            actor = actor,
            actionSummary = actionSummary,
            severity = severity,
            ip = ip,
            location = location
        )
        _auditEvents.update { listOf(newEvent) + it.take(25) }
    }

    fun resolveRecommendation(id: String) {
        _recommendations.update { list ->
            list.map { rec ->
                if (rec.id == id) rec.copy(isResolved = true) else rec
            }
        }
        val resolved = _recommendations.value.find { it.id == id }
        _userFeedback.tryEmit("✓ Resolved recommendation: ${resolved?.title}")
        recalculatePosture()
    }

    fun simulateContextShift(reason: String, newScore: Int, challenge: Boolean) {
        _continuousTrustState.update {
            it.copy(
                currentScore = newScore,
                contextShiftReason = reason,
                isChallenged = challenge,
                requiredAction = if (challenge) "STEP-UP AUTHENTICATION REQUIRED" else "Normal Monitoring"
            )
        }
        val arrow = "${_continuousTrustState.value.initialScore} → $newScore"
        _userFeedback.tryEmit("Dynamic Context Shift: Trust updated $arrow ($reason)")
    }

    fun satisfyContinuousChallenge() {
        _continuousTrustState.update {
            it.copy(
                currentScore = 92,
                isChallenged = false,
                contextShiftReason = "Biometric Step-Up Attestation Verified",
                requiredAction = "Trusted Session Re-established"
            )
        }
        _userFeedback.tryEmit("✓ Continuous challenge satisfied: Trust restored to 92")
    }

    fun askAnalyst(query: String) {
        val userMsg = AnalystMessage(
            id = "user_${System.currentTimeMillis()}",
            senderIsUser = true,
            text = query,
            timestamp = "Just now"
        )
        val analystReply = SecurityDataMocks.generateAnalystAnswer(query)
        _analystMessages.update { it + listOf(userMsg, analystReply) }
    }

    fun getExplainableDecision(requestId: String): ExplainableDecision? {
        return _explainableDecisions.value[requestId] ?: _explainableDecisions.value["req_1"]
    }

    // Reset Demo Data
    fun resetDemoData() {
        _posture.value = createInitialPosture()
        _devices.value = createInitialDevices()
        _accessRequests.value = createInitialAccessRequests()
        _resources.value = createInitialResources()
        _alerts.value = createInitialAlerts()
        _sessions.value = createInitialSessions()
        _settings.value = UserSecuritySettings()
        _lockdownState.value = LockdownState()
        _auditEvents.value = createInitialAuditEvents()
        _radarNodes.value = createInitialRadarNodes()
        _recommendations.value = SecurityDataMocks.createInitialRecommendations()
        _rbacRoles.value = SecurityDataMocks.createInitialRbacRoles()
        _continuousTrustState.value = ContinuousTrustState()
        _analyticsData.value = SecurityAnalyticsSummary()
        _isCurrentDeviceTrusted.value = true
        _userFeedback.tryEmit("✓ Demo data reset to initial baseline state")
    }

    // Mock Risk Engine Calculation
    fun evaluateZeroTrustDecision(
        resourceName: String,
        location: String = "Mumbai, IN",
        isKnownDevice: Boolean = true,
        isMfaVerified: Boolean = true
    ): AccessDecisionResult {
        val deviceScore = if (isKnownDevice) 10 else 45
        val locationScore = if (location.contains("Delhi")) 5 else 30
        val networkScore = 15 // Corporate SD-WAN or TLS
        val resourceScore = when {
            resourceName.contains("Database", ignoreCase = true) -> 35
            resourceName.contains("Admin", ignoreCase = true) -> 30
            else -> 15
        }
        val mfaMitigation = if (isMfaVerified) 25 else 0

        val totalRisk = (deviceScore + locationScore + networkScore + resourceScore - mfaMitigation)
            .coerceIn(5, 95)

        val riskLevel = when {
            totalRisk <= 30 -> RiskLevel.LOW
            totalRisk <= 60 -> RiskLevel.MEDIUM
            totalRisk <= 80 -> RiskLevel.HIGH
            else -> RiskLevel.CRITICAL
        }

        val steps = listOf(
            DecisionPipelineStep(
                title = "IDENTITY",
                status = if (isMfaVerified) StepStatus.PASSED else StepStatus.WARNING,
                evaluationDetail = "User credentials verified. Contextual token active.",
                weightDescription = "Identity Authenticated"
            ),
            DecisionPipelineStep(
                title = "DEVICE",
                status = if (isKnownDevice) StepStatus.PASSED else StepStatus.WARNING,
                evaluationDetail = if (isKnownDevice) "Hardware TPM valid, OS patched, EDR active." else "Unrecognized hardware fingerprint.",
                weightDescription = if (isKnownDevice) "Compliant Managed Device" else "+35 Risk: Unknown Device"
            ),
            DecisionPipelineStep(
                title = "LOCATION",
                status = if (location.contains("Delhi")) StepStatus.PASSED else StepStatus.WARNING,
                evaluationDetail = "$location (Unusual geo-coordinate variance detected)",
                weightDescription = "Location Anomaly"
            ),
            DecisionPipelineStep(
                title = "NETWORK",
                status = StepStatus.PASSED,
                evaluationDetail = "Encrypted mTLS tunnel. No proxy or TOR exit node detected.",
                weightDescription = "Secure Transport Verified"
            ),
            DecisionPipelineStep(
                title = "RESOURCE",
                status = if (resourceScore >= 30) StepStatus.WARNING else StepStatus.PASSED,
                evaluationDetail = "$resourceName (Classification: CONFIDENTIAL / ADMIN)",
                weightDescription = "High Value Asset"
            ),
            DecisionPipelineStep(
                title = "RISK ENGINE",
                status = if (totalRisk > 60) StepStatus.FAILED else if (totalRisk > 30) StepStatus.WARNING else StepStatus.PASSED,
                evaluationDetail = "Computed Composite Risk Score: $totalRisk / 100",
                weightDescription = "Adaptive ZTNA Rule Evaluated"
            )
        )

        val verdict = if (totalRisk <= 30) {
            "ACCESS PERMITTED (LEAST PRIVILEGE)"
        } else if (totalRisk <= 60) {
            "STEP-UP AUTHENTICATION REQUIRED"
        } else {
            "ACCESS DENIED (POLICY RESTRICTION)"
        }

        val verdictSubtitle = if (totalRisk <= 30) {
            "Device and identity parameters meet baseline zero trust security criteria."
        } else if (totalRisk <= 60) {
            "Elevated risk due to location variance. Real-time biometrics or admin review required."
        } else {
            "Untrusted environment detected. Threat prevention policy isolated request."
        }

        return AccessDecisionResult(
            subject = "alex@company.com",
            resource = resourceName,
            calculatedRiskScore = totalRisk,
            calculatedRiskLevel = riskLevel,
            steps = steps,
            finalVerdict = verdict,
            verdictSubtitle = verdictSubtitle,
            requiresStepUpAuth = totalRisk in 31..60
        )
    }

    private fun recalculatePosture() {
        val pendingRequests = _accessRequests.value.count { it.status == RequestStatus.PENDING }
        val criticalAlerts = _alerts.value.count { !it.isDismissed && (it.severity == RiskLevel.HIGH || it.severity == RiskLevel.CRITICAL) }
        val trustedDevices = _devices.value.count { it.isTrusted }
        val totalDevices = _devices.value.size

        var score = 92
        if (criticalAlerts > 0) score -= (criticalAlerts * 7)
        if (pendingRequests > 2) score -= 4
        if (trustedDevices < totalDevices) score -= 5
        score = score.coerceIn(40, 99)

        val statusText = if (score >= 80) "Protected" else if (score >= 60) "Attention Needed" else "At Risk"
        val overall = if (score >= 80) "SECURE" else if (score >= 60) "ELEVATED RISK" else "VULNERABLE"

        _posture.value = _posture.value.copy(
            score = score,
            statusText = statusText,
            overallPosture = overall
        )
    }

    // Initial Baseline Mocks
    private fun createInitialPosture(): SecurityPosture {
        return SecurityPosture(
            score = 87,
            statusText = "Protected",
            identityVerified = true,
            deviceCompliant = true,
            mfaEnabled = true,
            networkMonitored = true,
            overallPosture = "SECURE"
        )
    }

    private fun createInitialDevices(): List<Device> {
        return listOf(
            Device(
                id = "dev_1",
                name = "MacBook Pro 16\"",
                type = DeviceType.LAPTOP,
                os = "macOS 15.1 Sequoia",
                location = "Mumbai, IN",
                ip = "192.168.1.144",
                lastActive = "2 minutes ago",
                isTrusted = true,
                healthPercentage = 94,
                encryptionEnabled = true,
                firewallEnabled = true,
                screenLockEnabled = true,
                biometricsEnabled = true,
                mfaEnabled = true,
                deviceIdTag = "ZX-MBP-8821"
            ),
            Device(
                id = "dev_2",
                name = "Pixel 9 Pro",
                type = DeviceType.MOBILE,
                os = "Android 16",
                location = "Delhi, IN",
                ip = "10.0.4.82",
                lastActive = "Active now",
                isTrusted = true,
                healthPercentage = 98,
                encryptionEnabled = true,
                firewallEnabled = true,
                screenLockEnabled = true,
                biometricsEnabled = true,
                mfaEnabled = true,
                deviceIdTag = "ZX-94A7-21"
            ),
            Device(
                id = "dev_3",
                name = "ThinkPad X1 Carbon",
                type = DeviceType.LAPTOP,
                os = "Windows 11 Enterprise",
                location = "Bengaluru, IN",
                ip = "172.16.20.15",
                lastActive = "1 hour ago",
                isTrusted = true,
                healthPercentage = 88,
                encryptionEnabled = true,
                firewallEnabled = true,
                screenLockEnabled = true,
                biometricsEnabled = false,
                mfaEnabled = true,
                deviceIdTag = "ZX-WIN-4409"
            ),
            Device(
                id = "dev_4",
                name = "iPhone 17 Pro",
                type = DeviceType.MOBILE,
                os = "iOS 19.0",
                location = "Hyderabad, IN",
                ip = "192.168.8.60",
                lastActive = "Yesterday",
                isTrusted = false,
                healthPercentage = 76,
                encryptionEnabled = true,
                firewallEnabled = false,
                screenLockEnabled = true,
                biometricsEnabled = true,
                mfaEnabled = true,
                deviceIdTag = "ZX-IOS-1022"
            )
        )
    }

    private fun createInitialAccessRequests(): List<AccessRequest> {
        return listOf(
            AccessRequest(
                id = "req_1",
                requesterName = "Alex Morgan",
                requesterEmail = "alex@company.com",
                resourceName = "Production Database",
                resourceCategory = "Databases",
                deviceName = "MacBook Pro",
                os = "macOS 15.1",
                location = "Mumbai, IN",
                ip = "192.168.1.144",
                requestedTimeAgo = "2 minutes ago",
                riskLevel = RiskLevel.HIGH,
                riskScore = 74,
                riskFactors = listOf(
                    RiskFactorItem("New device signature", isPositive = false),
                    RiskFactorItem("Unusual geolocation variance", isPositive = false),
                    RiskFactorItem("Hardware MFA verified", isPositive = true),
                    RiskFactorItem("Device disk encrypted (FileVault)", isPositive = true)
                ),
                status = RequestStatus.PENDING
            ),
            AccessRequest(
                id = "req_2",
                requesterName = "Sarah Chen",
                requesterEmail = "sarah.chen@company.com",
                resourceName = "Admin Portal",
                resourceCategory = "Internal Tools",
                deviceName = "Windows 11 • Chrome",
                os = "Windows 11",
                location = "Delhi, IN",
                ip = "10.0.12.91",
                requestedTimeAgo = "8 minutes ago",
                riskLevel = RiskLevel.MEDIUM,
                riskScore = 48,
                riskFactors = listOf(
                    RiskFactorItem("Familiar enterprise branch network", isPositive = true),
                    RiskFactorItem("Off-hours session initialization", isPositive = false),
                    RiskFactorItem("Role-based privilege match: DevSecOps", isPositive = true)
                ),
                status = RequestStatus.PENDING
            ),
            AccessRequest(
                id = "req_3",
                requesterName = "Rahul Sharma",
                requesterEmail = "rahul.s@company.com",
                resourceName = "AWS IAM Production",
                resourceCategory = "Cloud Services",
                deviceName = "ThinkPad X1",
                os = "Linux / Ubuntu 24.04",
                location = "Bengaluru, IN",
                ip = "172.16.5.10",
                requestedTimeAgo = "25 minutes ago",
                riskLevel = RiskLevel.LOW,
                riskScore = 22,
                riskFactors = listOf(
                    RiskFactorItem("Corporate VPN mTLS active", isPositive = true),
                    RiskFactorItem("Zero Trust posture score: 98%", isPositive = true),
                    RiskFactorItem("MFA FIDO2 hardware key token", isPositive = true)
                ),
                status = RequestStatus.PENDING
            )
        )
    }

    private fun createInitialResources(): List<ProtectedResource> {
        return listOf(
            ProtectedResource(
                id = "res_1",
                name = "Salesforce Enterprise",
                category = ResourceCategory.APPLICATIONS,
                classification = AccessClassification.INTERNAL,
                accessLevel = "Standard User",
                mfaRequired = true,
                deviceTrustRequired = false,
                riskThreshold = RiskLevel.MEDIUM,
                sessionDurationMinutes = 120,
                accessStatus = ResourceAccessStatus.GRANTED,
                description = "Customer relationship management and enterprise sales telemetry."
            ),
            ProtectedResource(
                id = "res_2",
                name = "Production Database (PostgreSQL)",
                category = ResourceCategory.DATABASES,
                classification = AccessClassification.CONFIDENTIAL,
                accessLevel = "Admin Privileged",
                mfaRequired = true,
                deviceTrustRequired = true,
                riskThreshold = RiskLevel.LOW,
                sessionDurationMinutes = 30,
                accessStatus = ResourceAccessStatus.APPROVAL_REQUIRED,
                description = "Primary transactional relational database cluster with encrypted storage."
            ),
            ProtectedResource(
                id = "res_3",
                name = "Admin Console & Policy Engine",
                category = ResourceCategory.INTERNAL_TOOLS,
                classification = AccessClassification.RESTRICTED,
                accessLevel = "Super Admin",
                mfaRequired = true,
                deviceTrustRequired = true,
                riskThreshold = RiskLevel.LOW,
                sessionDurationMinutes = 45,
                accessStatus = ResourceAccessStatus.RESTRICTED,
                description = "Global Zero Trust gateway orchestration and firewall rule provisioning."
            ),
            ProtectedResource(
                id = "res_4",
                name = "AWS Production Root Console",
                category = ResourceCategory.CLOUD_SERVICES,
                classification = AccessClassification.RESTRICTED,
                accessLevel = "Cloud Engineer",
                mfaRequired = true,
                deviceTrustRequired = true,
                riskThreshold = RiskLevel.LOW,
                sessionDurationMinutes = 60,
                accessStatus = ResourceAccessStatus.APPROVAL_REQUIRED,
                description = "Multi-region cloud infrastructure management and VPC peering."
            ),
            ProtectedResource(
                id = "res_5",
                name = "GitLab Enterprise",
                category = ResourceCategory.APPLICATIONS,
                classification = AccessClassification.INTERNAL,
                accessLevel = "Developer Write",
                mfaRequired = true,
                deviceTrustRequired = true,
                riskThreshold = RiskLevel.MEDIUM,
                sessionDurationMinutes = 180,
                accessStatus = ResourceAccessStatus.GRANTED,
                description = "Private source code repositories, CI/CD runners, and deployment secrets."
            ),
            ProtectedResource(
                id = "res_6",
                name = "Snowflake Data Warehouse",
                category = ResourceCategory.DATABASES,
                classification = AccessClassification.CONFIDENTIAL,
                accessLevel = "Data Analyst",
                mfaRequired = true,
                deviceTrustRequired = true,
                riskThreshold = RiskLevel.MEDIUM,
                sessionDurationMinutes = 60,
                accessStatus = ResourceAccessStatus.APPROVAL_REQUIRED,
                description = "Enterprise business analytics warehouse with anonymized records."
            ),
            ProtectedResource(
                id = "res_7",
                name = "Kubernetes Production Secrets Vault",
                category = ResourceCategory.INTERNAL_TOOLS,
                classification = AccessClassification.CRITICAL,
                accessLevel = "SecOps Lead Only",
                mfaRequired = true,
                deviceTrustRequired = true,
                riskThreshold = RiskLevel.LOW,
                sessionDurationMinutes = 15,
                accessStatus = ResourceAccessStatus.RESTRICTED,
                description = "Hardware cryptographic HSM master keys and TLS certificate root authority."
            ),
            ProtectedResource(
                id = "res_8",
                name = "Corporate Status Page & API Docs",
                category = ResourceCategory.APPLICATIONS,
                classification = AccessClassification.PUBLIC,
                accessLevel = "Anonymous Read",
                mfaRequired = false,
                deviceTrustRequired = false,
                riskThreshold = RiskLevel.HIGH,
                sessionDurationMinutes = 480,
                accessStatus = ResourceAccessStatus.GRANTED,
                description = "Public infrastructure uptime status and developer documentation."
            )
        )
    }

    private fun createInitialAlerts(): List<SecurityAlert> {
        return listOf(
            SecurityAlert(
                id = "alert_1",
                title = "Suspicious Login Attempt",
                description = "New device detected attempting SSH tunnel with elevated credentials.",
                severity = RiskLevel.HIGH,
                detectedTimeAgo = "3 minutes ago",
                deviceName = "Unknown Windows PC",
                location = "Mumbai, IN",
                ip = "185.220.101.5",
                riskScore = 82,
                riskFactors = listOf(
                    "New unmanaged device fingerprint",
                    "Unusual location coordinate mismatch",
                    "Unknown ISP / Non-corporate gateway",
                    "Failed biometric step-up"
                )
            ),
            SecurityAlert(
                id = "alert_2",
                title = "Successful MFA Verification",
                description = "FIDO2 security token authentication approved for Alex Morgan.",
                severity = RiskLevel.LOW,
                detectedTimeAgo = "12 minutes ago",
                deviceName = "Pixel 9 Pro",
                location = "Delhi, IN",
                ip = "10.0.4.82",
                riskScore = 14,
                riskFactors = listOf(
                    "Hardware token verified",
                    "Device encryption active",
                    "Known corporate subnet"
                )
            ),
            SecurityAlert(
                id = "alert_3",
                title = "High Risk Access Attempt",
                description = "Production DB access requested from an unpatched OS version.",
                severity = RiskLevel.CRITICAL,
                detectedTimeAgo = "25 minutes ago",
                deviceName = "Untrusted Workstation",
                location = "Hyderabad, IN",
                ip = "203.0.113.88",
                riskScore = 91,
                riskFactors = listOf(
                    "Critical CVE unpatched in system kernel",
                    "Untrusted hardware certificate",
                    "Access policy requires Zero Trust Score >= 85"
                )
            ),
            SecurityAlert(
                id = "alert_4",
                title = "Session Anomaly Detected",
                description = "Concurrent session opened from different geographic regions within 5 minutes.",
                severity = RiskLevel.MEDIUM,
                detectedTimeAgo = "1 hour ago",
                deviceName = "Chrome Web Session",
                location = "Bengaluru, IN",
                ip = "172.16.8.22",
                riskScore = 54,
                riskFactors = listOf(
                    "Impossible travel time velocity anomaly",
                    "Session token duplicated"
                )
            )
        )
    }

    private fun createInitialSessions(): List<ActiveSession> {
        return listOf(
            ActiveSession(
                id = "sess_1",
                deviceName = "Pixel 9 Pro",
                clientInfo = "ZeroTrustX Mobile v2.4",
                location = "Delhi, IN",
                ip = "10.0.4.82",
                isCurrent = true,
                lastActive = "Active now",
                startedAt = "Today, 08:30"
            ),
            ActiveSession(
                id = "sess_2",
                deviceName = "MacBook Pro 16\"",
                clientInfo = "Chrome 129 • macOS",
                location = "Mumbai, IN",
                ip = "192.168.1.144",
                isCurrent = false,
                lastActive = "2 min ago",
                startedAt = "Today, 09:15"
            ),
            ActiveSession(
                id = "sess_3",
                deviceName = "ThinkPad X1 Carbon",
                clientInfo = "Edge Enterprise • Win11",
                location = "Bengaluru, IN",
                ip = "172.16.20.15",
                isCurrent = false,
                lastActive = "45 min ago",
                startedAt = "Yesterday, 17:40"
            ),
            ActiveSession(
                id = "sess_4",
                deviceName = "Cloud CLI Terminal",
                clientInfo = "ZTNA-CLI / Go 1.23",
                location = "Delhi, IN",
                ip = "10.0.99.12",
                isCurrent = false,
                lastActive = "2 hours ago",
                startedAt = "Yesterday, 14:10"
            )
        )
    }

    private fun createInitialAuditEvents(): List<AuditEvent> {
        return listOf(
            AuditEvent(
                id = "audit_1",
                timestamp = "2m ago",
                category = AuditCategory.ACCESS,
                actor = "alex@company.com",
                actionSummary = "Just-in-time privileged access token requested for Production DB",
                severity = RiskLevel.HIGH,
                ip = "192.168.1.144",
                location = "Mumbai, IN"
            ),
            AuditEvent(
                id = "audit_2",
                timestamp = "8m ago",
                category = AuditCategory.AUTH,
                actor = "sarah.chen@company.com",
                actionSummary = "Hardware FIDO2 WebAuthn token verified for Admin Portal",
                severity = RiskLevel.LOW,
                ip = "10.0.12.91",
                location = "Delhi, IN"
            ),
            AuditEvent(
                id = "audit_3",
                timestamp = "14m ago",
                category = AuditCategory.THREAT,
                actor = "system-edr",
                actionSummary = "Suspicious geographic displacement velocity between sessions",
                severity = RiskLevel.CRITICAL,
                ip = "185.220.101.5",
                location = "Mumbai, IN"
            ),
            AuditEvent(
                id = "audit_4",
                timestamp = "22m ago",
                category = AuditCategory.NETWORK,
                actor = "ztna-gateway-01",
                actionSummary = "mTLS mutual TLS session re-keyed with TLS 1.3 ChaCha20",
                severity = RiskLevel.LOW,
                ip = "10.0.4.82",
                location = "Delhi, IN"
            ),
            AuditEvent(
                id = "audit_5",
                timestamp = "1h ago",
                category = AuditCategory.ADMIN,
                actor = "alex@company.com",
                actionSummary = "Zero Trust microsegmentation rule enforced on Kubernetes VPC",
                severity = RiskLevel.MEDIUM,
                ip = "10.0.4.82",
                location = "Delhi, IN"
            )
        )
    }

    private fun createInitialRadarNodes(): List<RadarThreatNode> {
        return listOf(
            RadarThreatNode(
                id = "node_delhi",
                locationName = "Delhi Hub (HQ)",
                ip = "10.0.4.82",
                latencyMs = 12,
                status = NodeSecurityStatus.SECURE,
                activeTunnels = 8,
                threatSummary = "Compliant Enterprise Branch • 100% Policy Adherence",
                angleDegrees = 45f,
                radiusRatio = 0.35f
            ),
            RadarThreatNode(
                id = "node_mumbai",
                locationName = "Mumbai Gateway",
                ip = "192.168.1.144",
                latencyMs = 28,
                status = NodeSecurityStatus.CAUTION,
                activeTunnels = 3,
                threatSummary = "Geo-Coordinate Variance Detected • Step-up Auth Required",
                angleDegrees = 160f,
                radiusRatio = 0.55f
            ),
            RadarThreatNode(
                id = "node_blr",
                locationName = "Bengaluru R&D",
                ip = "172.16.20.15",
                latencyMs = 22,
                status = NodeSecurityStatus.SECURE,
                activeTunnels = 5,
                threatSummary = "Encrypted Hardware VPN Mesh • Low Risk Profile",
                angleDegrees = 260f,
                radiusRatio = 0.42f
            ),
            RadarThreatNode(
                id = "node_hyd",
                locationName = "Hyderabad Cloud Edge",
                ip = "203.0.113.88",
                latencyMs = 85,
                status = NodeSecurityStatus.ANOMALY,
                activeTunnels = 1,
                threatSummary = "Untrusted Certificate Handshake • Traffic Quarantined",
                angleDegrees = 330f,
                radiusRatio = 0.78f
            )
        )
    }
}
