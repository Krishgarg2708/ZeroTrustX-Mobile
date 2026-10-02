package com.example.data.repository

import com.example.data.model.*

object SecurityDataMocks {

    fun createInitialRecommendations(): List<SecurityRecommendation> {
        return listOf(
            SecurityRecommendation(
                id = "rec_1",
                title = "Enforce FIDO2 Hardware MFA on Super Admin",
                description = "2 administrative accounts currently rely on standard push/TOTP instead of phishing-resistant hardware security keys.",
                impactScore = "+6 Score",
                priority = RecommendationPriority.CRITICAL,
                target = RecommendationTarget.MFA,
                isResolved = false
            ),
            SecurityRecommendation(
                id = "rec_2",
                title = "Review Untrusted iPhone 17 Pro Endpoint",
                description = "Device reported disabled system firewall while accessing internal corporate mail.",
                impactScore = "+4 Score",
                priority = RecommendationPriority.HIGH,
                target = RecommendationTarget.DEVICES,
                isResolved = false
            ),
            SecurityRecommendation(
                id = "rec_3",
                title = "Terminate 2 Stale Remote Sessions",
                description = "Sessions idle for over 45 minutes on Edge/Win11 and Cloud CLI. Reclaim token lifetime.",
                impactScore = "+3 Score",
                priority = RecommendationPriority.MEDIUM,
                target = RecommendationTarget.SESSIONS,
                isResolved = false
            ),
            SecurityRecommendation(
                id = "rec_4",
                title = "Adjudicate Pending High-Risk Production DB Request",
                description = "Access request from Mumbai with new hardware fingerprint requires explicit zero-trust approval.",
                impactScore = "+5 Score",
                priority = RecommendationPriority.HIGH,
                target = RecommendationTarget.REQUESTS,
                isResolved = false
            ),
            SecurityRecommendation(
                id = "rec_5",
                title = "Enforce Microsegmentation on Cloud VPC",
                description = "Restrict direct lateral node traversal between staging cluster and production database.",
                impactScore = "+4 Score",
                priority = RecommendationPriority.LOW,
                target = RecommendationTarget.POSTURE,
                isResolved = false
            )
        )
    }

    fun createInitialRbacRoles(): List<RbacRole> {
        return listOf(
            RbacRole(
                id = "role_sec_admin",
                roleName = "Security Administrator",
                privilegeLevel = "Super Admin (Tier 3)",
                allowedResources = listOf("Policy Engine", "Admin Console", "All Production DBs", "AWS IAM", "Audit Logs"),
                restrictedResources = emptyList(),
                permissionCount = 48,
                leastPrivilegeAdherence = "Dual-Custody Required for Vault Exports",
                description = "Global oversight of enterprise Zero Trust gateways, policy engines, and threat containment."
            ),
            RbacRole(
                id = "role_it_admin",
                roleName = "IT Administrator",
                privilegeLevel = "Elevated Operational",
                allowedResources = listOf("Device Enclave Manager", "SSO Directory", "Helpdesk VPN", "Salesforce"),
                restrictedResources = listOf("Production DB (Write)", "Root Cloud Console"),
                permissionCount = 28,
                leastPrivilegeAdherence = "Hardware TPM Bound",
                description = "Hardware endpoint lifecycle provisioning, certificate rotation, and identity lifecycle."
            ),
            RbacRole(
                id = "role_developer",
                roleName = "Developer",
                privilegeLevel = "Standard Technical",
                allowedResources = listOf("GitLab Enterprise", "Staging Kubernetes", "Internal Documentation", "Jira"),
                restrictedResources = listOf("Production DB (Root)", "AWS Billing", "Customer PII Database"),
                permissionCount = 14,
                leastPrivilegeAdherence = "Just-In-Time 30min Elevation",
                description = "Code authoring, CI/CD pipelines, and sandboxed microservice deployment."
            ),
            RbacRole(
                id = "role_employee",
                roleName = "Employee",
                privilegeLevel = "Baseline User",
                allowedResources = listOf("Salesforce Enterprise", "Internal HR Portal", "Corporate Slack", "Email"),
                restrictedResources = listOf("All Infrastructure", "Production DBs", "Security Console"),
                permissionCount = 6,
                leastPrivilegeAdherence = "Context-Aware Default Deny",
                description = "General enterprise operational productivity applications."
            ),
            RbacRole(
                id = "role_auditor",
                roleName = "Auditor",
                privilegeLevel = "Read-Only Compliance",
                allowedResources = listOf("Immutable Audit Logs", "Posture Reports", "Compliance Matrix", "Policy Definitions"),
                restrictedResources = listOf("Production Data Access", "Configuration Mutate"),
                permissionCount = 9,
                leastPrivilegeAdherence = "Strict Read-Only Enforcement",
                description = "External and internal regulatory compliance telemetry verification."
            )
        )
    }

    fun createScenarioStages(): List<ScenarioSimulationStage> {
        return listOf(
            ScenarioSimulationStage(
                stageNumber = 1,
                title = "1. Normal Baseline Session",
                description = "Alex connects from corporate office in Delhi using compliant Pixel 9 Pro. Biometric FIDO2 token verified.",
                postureScore = 94,
                riskLevel = RiskLevel.LOW,
                detectedChange = "None • Device and IP match corporate baseline",
                policyAction = "ACCESS PERMITTED (Zero Trust Baseline Satisfied)"
            ),
            ScenarioSimulationStage(
                stageNumber = 2,
                title = "2. Unrecognized Device Attempt",
                description = "Simulated login attempt on credentials from an unmanaged personal laptop.",
                postureScore = 82,
                riskLevel = RiskLevel.MEDIUM,
                detectedChange = "Hardware Fingerprint mismatch • Missing enterprise EDR agent",
                policyAction = "DEVICE UNTRUSTED: Restrict to sandboxed web client"
            ),
            ScenarioSimulationStage(
                stageNumber = 3,
                title = "3. Geolocation & Impossible Travel",
                description = "Session initiated from Mumbai within 12 minutes of Delhi activity (impossible physical velocity).",
                postureScore = 68,
                riskLevel = RiskLevel.HIGH,
                detectedChange = "Geo-Coordinate Velocity: 1,150 km/h anomaly detected",
                policyAction = "ANOMALY FLAGGED: Session tokens quarantined"
            ),
            ScenarioSimulationStage(
                stageNumber = 4,
                title = "4. Resource Sensitivity Escalation",
                description = "The unverified session attempts to read credentials from Production Database (PostgreSQL).",
                postureScore = 48,
                riskLevel = RiskLevel.HIGH,
                detectedChange = "Target asset classified as CONFIDENTIAL / HIGH SENSITIVITY",
                policyAction = "STEP-UP AUTHENTICATION REQUIRED • Dual-Custody Triggered"
            ),
            ScenarioSimulationStage(
                stageNumber = 5,
                title = "5. Biometric Step-Up Challenge",
                description = "Zero Trust engine presents interactive biometric challenge to registered mobile enclave.",
                postureScore = 62,
                riskLevel = RiskLevel.MEDIUM,
                detectedChange = "Awaiting hardware keystore challenge response",
                policyAction = "Step-up challenge presented to user Alex Morgan"
            ),
            ScenarioSimulationStage(
                stageNumber = 6,
                title = "6. Policy Enforcement & Verdict",
                description = "User fails step-up challenge or times out. Automated least-privilege policy enforces access termination.",
                postureScore = 75,
                riskLevel = RiskLevel.HIGH,
                detectedChange = "Policy Rule #ZT-8891 triggered: 'Zero tolerance on failed elevation'",
                policyAction = "ACCESS DENIED • Remote connection severed"
            ),
            ScenarioSimulationStage(
                stageNumber = 7,
                title = "7. SOC Containment & Threat Alert",
                description = "Incident logged to Immutable Audit Trail. Security alert dispatched to SOC Tier 3 dashboard.",
                postureScore = 87,
                riskLevel = RiskLevel.LOW,
                detectedChange = "Threat isolated • Source IP placed in temporary perimeter blacklist",
                policyAction = "INCIDENT RESOLVED: System returned to protected posture"
            )
        )
    }

    fun createInitialExplainableDecisions(): Map<String, ExplainableDecision> {
        return mapOf(
            "req_1" to ExplainableDecision(
                requestId = "req_1",
                resourceName = "Production Database (PostgreSQL)",
                requester = "alex@company.com",
                identityVerified = true,
                mfaStatus = "FIDO2 Hardware Key Verified",
                deviceTrust = "Trusted Managed (MacBook Pro 16\")",
                devicePosture = "Compliant (OS 15.1, FileVault Encrypted, EDR Active)",
                locationFamiliarity = "Unfamiliar (Mumbai, IN vs typical Delhi HQ)",
                networkTrust = "Encrypted TLS 1.3 Tunnel (Remote ISP)",
                resourceSensitivity = AccessClassification.CONFIDENTIAL,
                currentRiskScore = 74,
                appliedPolicies = listOf(
                    PolicyEvaluationResult("Policy #1: Multi-Factor Authentication", true, "Hardware token verified successfully"),
                    PolicyEvaluationResult("Policy #2: Device Health & Disk Encryption", true, "FileVault disk encryption active"),
                    PolicyEvaluationResult("Policy #3: Geographic Boundary Familiarity", false, "Location variance exceeds 500km threshold"),
                    PolicyEvaluationResult("Policy #4: High-Value Asset Least Privilege", false, "Off-hours access requires interactive step-up or admin co-sign")
                ),
                finalDecision = "STEP-UP AUTHENTICATION REQUIRED",
                whyExplanation = "Although credentials and hardware compliance passed, access originated from an unusual geographic coordinate outside Delhi HQ to a CONFIDENTIAL asset. Zero Trust policy demands step-up verification before issuing temporary session tokens."
            ),
            "req_2" to ExplainableDecision(
                requestId = "req_2",
                resourceName = "Admin Portal & Policy Engine",
                requester = "sarah.chen@company.com",
                identityVerified = true,
                mfaStatus = "TOTP Authenticator Verified",
                deviceTrust = "Trusted Managed (Windows 11 Carbon)",
                devicePosture = "Compliant (BitLocker Active, Firewall ON)",
                locationFamiliarity = "Familiar (Corporate Branch Delhi)",
                networkTrust = "Corporate SD-WAN / 802.1x",
                resourceSensitivity = AccessClassification.RESTRICTED,
                currentRiskScore = 48,
                appliedPolicies = listOf(
                    PolicyEvaluationResult("Policy #1: Multi-Factor Authentication", true, "MFA token approved"),
                    PolicyEvaluationResult("Policy #2: Enterprise Network Boundary", true, "Corporate subnet authenticated"),
                    PolicyEvaluationResult("Policy #3: Off-Hours Session Review", false, "Session requested at 23:45 outside normal work schedule")
                ),
                finalDecision = "APPROVAL REQUIRED (LOW/MED RISK)",
                whyExplanation = "Requester identity, device, and network are verified on corporate premises. The access is flagged for review purely because it occurred outside regular operating hours on a RESTRICTED asset."
            ),
            "req_3" to ExplainableDecision(
                requestId = "req_3",
                resourceName = "AWS Production Root Console",
                requester = "rahul.s@company.com",
                identityVerified = true,
                mfaStatus = "Hardware FIDO2 Security Key",
                deviceTrust = "Trusted Managed (ThinkPad X1)",
                devicePosture = "100% Compliant (Kernel Patched, LUKS Active)",
                locationFamiliarity = "Familiar (Bengaluru Tech Park)",
                networkTrust = "mTLS WireGuard Enterprise Tunnel",
                resourceSensitivity = AccessClassification.CRITICAL,
                currentRiskScore = 22,
                appliedPolicies = listOf(
                    PolicyEvaluationResult("Policy #1: Hardware-backed Identity", true, "FIDO2 token validated"),
                    PolicyEvaluationResult("Policy #2: Endpoint Telemetry Attestation", true, "TPM 2.0 attestation clean"),
                    PolicyEvaluationResult("Policy #3: Cloud Governance Dual Custody", true, "Approved for 30m limited TTL")
                ),
                finalDecision = "ALLOW (LEAST PRIVILEGE TOKEN ISSUED)",
                whyExplanation = "All Zero Trust verification pillars (Identity, Device, Network, Location, Compliance) fully satisfied. A time-bound 30-minute just-in-time token is granted."
            )
        )
    }

    fun generateAnalystAnswer(query: String): AnalystMessage {
        val q = query.lowercase()
        val (text, suggestions, refs) = when {
            q.contains("posture") || q.contains("score") || q.contains("secure") -> Triple(
                "Your enterprise Zero Trust Posture Score is currently 87/100 (SECURE).\n\n" +
                        "• Identity Pillar: 92/100 (MFA active on 98% of users)\n" +
                        "• Device Pillar: 88/100 (3 of 4 endpoints fully compliant)\n" +
                        "• Network Pillar: 84/100 (mTLS enforced across corporate enclaves)\n" +
                        "• Application Pillar: 85/100 (Least privilege RBAC configured)\n\n" +
                        "To reach 95+, resolve the pending high-risk Production DB request and enforce FileVault on the untrusted iPhone endpoint.",
                listOf("Why is Production DB flagged?", "Show active recommendations", "Explain Least Privilege"),
                listOf("Zero Trust Architecture NIST SP 800-207", "Security Posture Matrix")
            )
            q.contains("db") || q.contains("database") || q.contains("mumbai") -> Triple(
                "The access request for 'Production Database (PostgreSQL)' from Alex Morgan is flagged HIGH RISK (Score 74/100) because:\n\n" +
                        "1. Unusual Geographic Variance: Request originated from Mumbai, IN while previous session was in Delhi 20 minutes earlier.\n" +
                        "2. Target Classification: The database contains CONFIDENTIAL customer records.\n" +
                        "3. Policy Trigger: Policy #ZT-402 requires step-up biometric re-authentication for all off-network confidential accesses.",
                listOf("Review explainable decision", "How to approve this request?", "Simulate policy outcome"),
                listOf("Policy Engine Rule #ZT-402", "Geo-Velocity Correlator")
            )
            q.contains("threat") || q.contains("alert") -> Triple(
                "There are 2 active alerts requiring SOC attention:\n\n" +
                        "1. High Risk Access Attempt (CRITICAL, Score 91): Untrusted workstation in Hyderabad attempted kernel-level bypass.\n" +
                        "2. Suspicious Login Attempt (HIGH, Score 82): Unmanaged Windows PC in Mumbai attempted credential stuffing.\n\n" +
                        "Recommendation: Keep the Hyderabad workstation isolated and enforce device certificates on the Mumbai gateway.",
                listOf("Block suspicious devices", "Open Threat Radar", "Run Scenario Walkthrough"),
                listOf("SOC Incident Feed #2026-X11", "Threat Mitigation Playbook")
            )
            q.contains("least privilege") || q.contains("rbac") -> Triple(
                "Zero Trust principle of Least Privilege dictates that users, devices, and workloads receive ONLY the minimum entitlements necessary to perform their duty, and only for the minimum duration required.\n\n" +
                        "• No standing administrative privileges (all root access uses 30-minute JIT tokens)\n" +
                        "• Strict role-based microsegmentation across Applications, Databases, and Cloud Enclaves\n" +
                        "• Continuous dynamic posture re-assessment rather than permanent trust.",
                listOf("View RBAC Roles Matrix", "Simulate Policy", "Explain dynamic trust"),
                listOf("NIST Least Privilege Guidelines", "RBAC Entitlement Directory")
            )
            else -> Triple(
                "Based on continuous Zero Trust telemetry evaluation across your enterprise enclaves:\n\n" +
                        "• Your posture is currently SECURE (87/100)\n" +
                        "• Zero Trust philosophy 'Never trust, always verify' is actively inspecting all 4 sessions\n" +
                        "• Dynamic trust scoring is active. If any device switches network context or exhibits anomalous latency, step-up challenges trigger automatically.",
                listOf("Explain my posture score", "Show high-risk events", "Run live scenario demo"),
                listOf("ZeroTrustX Policy Core v3.2", "Continuous Telemetry Engine")
            )
        }

        return AnalystMessage(
            id = "analyst_${System.currentTimeMillis()}",
            senderIsUser = false,
            text = text,
            timestamp = "Just now",
            suggestedQueries = suggestions,
            references = refs
        )
    }
}
