package com.example.data.model

enum class RiskLevel(val label: String, val scoreRange: String) {
    LOW("LOW", "0-30"),
    MEDIUM("MEDIUM", "31-60"),
    HIGH("HIGH", "61-80"),
    CRITICAL("CRITICAL", "81-100")
}

enum class DeviceType {
    LAPTOP,
    MOBILE,
    TABLET,
    WORKSTATION
}

data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val os: String,
    val location: String,
    val ip: String,
    val lastActive: String,
    val isTrusted: Boolean,
    val healthPercentage: Int,
    val encryptionEnabled: Boolean = true,
    val firewallEnabled: Boolean = true,
    val screenLockEnabled: Boolean = true,
    val biometricsEnabled: Boolean = true,
    val mfaEnabled: Boolean = true,
    val deviceIdTag: String = "ZX-94A7-21"
)

enum class RequestStatus {
    PENDING,
    APPROVED,
    DENIED
}

data class AccessRequest(
    val id: String,
    val requesterName: String,
    val requesterEmail: String,
    val resourceName: String,
    val resourceCategory: String,
    val deviceName: String,
    val os: String,
    val location: String,
    val ip: String,
    val requestedTimeAgo: String,
    val riskLevel: RiskLevel,
    val riskScore: Int,
    val riskFactors: List<RiskFactorItem>,
    val status: RequestStatus = RequestStatus.PENDING,
    val approvedDurationMinutes: Int = 30
)

data class RiskFactorItem(
    val label: String,
    val isPositive: Boolean // positive = verified/safe, negative = risk factor
)

data class SecurityAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: RiskLevel,
    val detectedTimeAgo: String,
    val deviceName: String,
    val location: String,
    val ip: String,
    val riskScore: Int,
    val riskFactors: List<String>,
    val isDismissed: Boolean = false,
    val isDeviceBlocked: Boolean = false
)

data class ActiveSession(
    val id: String,
    val deviceName: String,
    val clientInfo: String,
    val location: String,
    val ip: String,
    val isCurrent: Boolean,
    val lastActive: String,
    val startedAt: String
)

enum class ResourceCategory(val displayName: String) {
    APPLICATIONS("Applications"),
    DATABASES("Databases"),
    INTERNAL_TOOLS("Internal Tools"),
    CLOUD_SERVICES("Cloud Services")
}

enum class AccessClassification(val label: String, val sensitivityRank: Int) {
    PUBLIC("PUBLIC", 1),
    INTERNAL("INTERNAL", 2),
    CONFIDENTIAL("CONFIDENTIAL", 3),
    RESTRICTED("RESTRICTED", 4),
    CRITICAL("CRITICAL", 5)
}

enum class ResourceAccessStatus(val label: String) {
    GRANTED("Granted"),
    APPROVAL_REQUIRED("Approval Required"),
    RESTRICTED("Restricted")
}

data class ProtectedResource(
    val id: String,
    val name: String,
    val category: ResourceCategory,
    val classification: AccessClassification,
    val accessLevel: String,
    val mfaRequired: Boolean = true,
    val deviceTrustRequired: Boolean = true,
    val riskThreshold: RiskLevel = RiskLevel.LOW,
    val sessionDurationMinutes: Int = 30,
    val accessStatus: ResourceAccessStatus = ResourceAccessStatus.APPROVAL_REQUIRED,
    val description: String = ""
)

data class DomainSecurityScores(
    val identityScore: Int = 92,
    val deviceScore: Int = 88,
    val networkScore: Int = 84,
    val applicationScore: Int = 85
)

data class SecurityPosture(
    val score: Int = 87,
    val statusText: String = "Protected",
    val identityVerified: Boolean = true,
    val deviceCompliant: Boolean = true,
    val mfaEnabled: Boolean = true,
    val networkMonitored: Boolean = true,
    val overallPosture: String = "SECURE",
    val domainScores: DomainSecurityScores = DomainSecurityScores(),
    val amISecureSummary: String = "All baseline Zero Trust compliance checks satisfied with minor location variances.",
    val attentionSummary: String = "3 pending access approvals and 1 unmanaged endpoint flagged for review.",
    val whyRiskySummary: String = "Production DB requested from outside corporate branch with off-hours token."
)

enum class StepStatus {
    PASSED,
    WARNING,
    FAILED
}

data class DecisionPipelineStep(
    val title: String,
    val status: StepStatus,
    val evaluationDetail: String,
    val weightDescription: String
)

data class AccessDecisionResult(
    val subject: String,
    val resource: String,
    val calculatedRiskScore: Int,
    val calculatedRiskLevel: RiskLevel,
    val steps: List<DecisionPipelineStep>,
    val finalVerdict: String,
    val verdictSubtitle: String,
    val requiresStepUpAuth: Boolean
)

data class UserSecuritySettings(
    val mfaEnabled: Boolean = true,
    val biometricLoginEnabled: Boolean = true,
    val loginNotificationsEnabled: Boolean = true,
    val newDeviceVerificationEnabled: Boolean = true,
    val sessionTimeoutMinutes: Int = 30,
    val darkModeEnabled: Boolean = true
)

enum class AuditCategory {
    AUTH,
    ACCESS,
    NETWORK,
    THREAT,
    ADMIN
}

data class AuditEvent(
    val id: String,
    val timestamp: String,
    val category: AuditCategory,
    val actor: String,
    val actionSummary: String,
    val severity: RiskLevel,
    val ip: String,
    val location: String,
    val device: String = "Pixel 9 Pro",
    val resource: String = "Access Gateway",
    val actionTaken: String = "Policy Logged"
)

enum class NodeSecurityStatus {
    SECURE,
    CAUTION,
    ANOMALY
}

data class RadarThreatNode(
    val id: String,
    val locationName: String,
    val ip: String,
    val latencyMs: Int,
    val status: NodeSecurityStatus,
    val activeTunnels: Int,
    val threatSummary: String,
    val angleDegrees: Float,
    val radiusRatio: Float
)

data class LockdownState(
    val isActive: Boolean = false,
    val initiatedAt: String = "",
    val reason: String = "",
    val isolatedCount: Int = 0
)

enum class RecommendationPriority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

enum class RecommendationTarget {
    MFA,
    DEVICES,
    SESSIONS,
    REQUESTS,
    POSTURE,
    LOCKDOWN
}

data class SecurityRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val impactScore: String,
    val priority: RecommendationPriority,
    val target: RecommendationTarget,
    val isResolved: Boolean = false
)

data class PolicyEvaluationResult(
    val policyName: String,
    val isPassed: Boolean,
    val reason: String
)

data class ExplainableDecision(
    val requestId: String,
    val resourceName: String,
    val requester: String,
    val identityVerified: Boolean,
    val mfaStatus: String,
    val deviceTrust: String,
    val devicePosture: String,
    val locationFamiliarity: String,
    val networkTrust: String,
    val resourceSensitivity: AccessClassification,
    val currentRiskScore: Int,
    val appliedPolicies: List<PolicyEvaluationResult>,
    val finalDecision: String,
    val whyExplanation: String
)

data class ContinuousTrustState(
    val initialScore: Int = 92,
    val currentScore: Int = 74,
    val contextShiftReason: String = "Switched to Public Unencrypted Wi-Fi (Cafe Hotspot)",
    val requiredAction: String = "STEP-UP AUTHENTICATION REQUIRED",
    val isChallenged: Boolean = true
)

data class RbacRole(
    val id: String,
    val roleName: String,
    val privilegeLevel: String,
    val allowedResources: List<String>,
    val restrictedResources: List<String>,
    val permissionCount: Int,
    val leastPrivilegeAdherence: String,
    val description: String
)

data class ScenarioSimulationStage(
    val stageNumber: Int,
    val title: String,
    val description: String,
    val postureScore: Int,
    val riskLevel: RiskLevel,
    val detectedChange: String,
    val policyAction: String
)

data class AnalystMessage(
    val id: String,
    val senderIsUser: Boolean,
    val text: String,
    val timestamp: String,
    val suggestedQueries: List<String> = emptyList(),
    val references: List<String> = emptyList()
)

data class DayActivityTrend(
    val day: String,
    val totalRequests: Int,
    val challengesOrBlocks: Int
)

data class SecurityAnalyticsSummary(
    val allowedCount: Int = 1420,
    val challengedCount: Int = 190,
    val deniedCount: Int = 120,
    val riskDistribution: Map<RiskLevel, Int> = mapOf(
        RiskLevel.LOW to 68,
        RiskLevel.MEDIUM to 22,
        RiskLevel.HIGH to 8,
        RiskLevel.CRITICAL to 2
    ),
    val weeklyTrend: List<DayActivityTrend> = listOf(
        DayActivityTrend("Mon", 210, 18),
        DayActivityTrend("Tue", 260, 24),
        DayActivityTrend("Wed", 310, 42),
        DayActivityTrend("Thu", 290, 31),
        DayActivityTrend("Fri", 340, 56),
        DayActivityTrend("Sat", 150, 12),
        DayActivityTrend("Sun", 120, 8)
    ),
    val deviceTrustShifts: List<String> = listOf(
        "ThinkPad Carbon: Health dropped 94% -> 88% (Missing OS sub-patch)",
        "iPhone 17 Pro: Demoted to Limited Trust (Firewall disabled)",
        "Pixel 9 Pro: Elevated to Strict Trusted (FIDO2 Enclave verified)"
    )
)
