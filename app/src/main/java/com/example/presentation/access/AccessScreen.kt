package com.example.presentation.access

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.presentation.components.AuditEventCard
import com.example.presentation.components.ConfirmationDialog
import com.example.presentation.components.EmptyState
import com.example.presentation.components.RiskBadge
import com.example.ui.theme.*

@Composable
fun AccessScreen(
    resources: List<ProtectedResource>,
    accessRequests: List<AccessRequest>,
    auditEvents: List<AuditEvent>,
    selectedCategory: ResourceCategory?,
    auditFilter: AuditCategory?,
    searchQuery: String,
    selectedResource: ProtectedResource?,
    selectedRequest: AccessRequest?,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (ResourceCategory?) -> Unit,
    onAuditFilterChange: (AuditCategory?) -> Unit,
    onSelectResource: (ProtectedResource?) -> Unit,
    onSelectRequest: (AccessRequest?) -> Unit,
    onApproveRequest: (String) -> Unit,
    onDenyRequest: (String) -> Unit,
    onRequestResourceAccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var requestToDeny by remember { mutableStateOf<AccessRequest?>(null) }
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Resources, 1: Requests, 2: Audit Trail

    val filteredResources = resources.filter { res ->
        (selectedCategory == null || res.category == selectedCategory) &&
                (searchQuery.isBlank() || res.name.contains(searchQuery, ignoreCase = true) || res.category.displayName.contains(searchQuery, ignoreCase = true))
    }

    val pendingRequests = accessRequests.filter { it.status == RequestStatus.PENDING }
    val filteredAuditEvents = if (auditFilter == null) auditEvents else auditEvents.filter { it.category == auditFilter }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("access_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Access Management",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Zero Trust resource entitlements, role boundaries & request approval",
                    fontSize = 13.sp,
                    color = CyberTextSecondary
                )
            }
        }

        // Sub-tabs: Resources vs Requests vs Audit Trail
        item {
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = CyberSurfaceElevated,
                contentColor = CyberCyan,
                divider = {},
                indicator = {}
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = {
                        Text(
                            text = "Resources (${resources.size})",
                            fontWeight = if (activeSubTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeSubTab == 0) CyberCyan else CyberTextSecondary
                        )
                    }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = {
                        Text(
                            text = "Requests (${pendingRequests.size})",
                            fontWeight = if (activeSubTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeSubTab == 1) CyberCyan else CyberTextSecondary
                        )
                    }
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = {
                        Text(
                            text = "Audit Trail",
                            fontWeight = if (activeSubTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeSubTab == 2) CyberCyan else CyberTextSecondary
                        )
                    }
                )
            }
        }

        if (activeSubTab == 0) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search resources, categories, or classifications...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyberTextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CyberTextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("resource_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberSurfaceCard,
                        unfocusedContainerColor = CyberSurfaceCard,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    )
                )
            }

            // Category Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberBlue,
                                selectedLabelColor = Color.White,
                                containerColor = CyberSurfaceCard,
                                labelColor = CyberTextSecondary
                            ),
                            border = BorderStroke(1.dp, CyberBorder)
                        )
                    }
                    items(ResourceCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onSelectCategory(if (selectedCategory == cat) null else cat) },
                            label = { Text(cat.displayName) },
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

            if (filteredResources.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.SearchOff,
                        title = "No Resources Found",
                        message = "No protected resources matched your search filter."
                    )
                }
            } else {
                items(filteredResources, key = { it.id }) { res ->
                    ProtectedResourceCard(
                        resource = res,
                        onClick = { onSelectResource(res) }
                    )
                }
            }
        } else if (activeSubTab == 1) {
            // Requests sub-tab
            if (pendingRequests.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.DoneAll,
                        title = "No Pending Access Requests",
                        message = "All incoming access requests have been reviewed and adjudicated."
                    )
                }
            } else {
                items(pendingRequests, key = { it.id }) { req ->
                    com.example.presentation.components.AccessRequestCard(
                        request = req,
                        onCardClick = { onSelectRequest(req) },
                        onApprove = { onApproveRequest(req.id) },
                        onDeny = { requestToDeny = req }
                    )
                }
            }
        } else {
            // Audit Trail sub-tab
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = auditFilter == null,
                            onClick = { onAuditFilterChange(null) },
                            label = { Text("All Events") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberBlue,
                                selectedLabelColor = Color.White,
                                containerColor = CyberSurfaceCard,
                                labelColor = CyberTextSecondary
                            ),
                            border = BorderStroke(1.dp, CyberBorder)
                        )
                    }
                    items(AuditCategory.entries) { cat ->
                        FilterChip(
                            selected = auditFilter == cat,
                            onClick = { onAuditFilterChange(if (auditFilter == cat) null else cat) },
                            label = { Text(cat.name) },
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

            items(filteredAuditEvents, key = { it.id }) { event ->
                AuditEventCard(event = event)
            }
        }
    }

    // Resource Detail Dialog (Section 19)
    if (selectedResource != null) {
        ResourceDetailDialog(
            resource = selectedResource,
            onDismiss = { onSelectResource(null) },
            onRequestAccess = {
                onRequestResourceAccess(selectedResource.id)
            }
        )
    }

    // Access Request Detail Dialog (Section 15)
    if (selectedRequest != null) {
        AccessRequestDetailDialog(
            request = selectedRequest,
            onDismiss = { onSelectRequest(null) },
            onApprove = {
                onApproveRequest(selectedRequest.id)
                onSelectRequest(null)
            },
            onDeny = {
                requestToDeny = selectedRequest
            }
        )
    }

    // Deny Request Confirmation Dialog (Section 15)
    if (requestToDeny != null) {
        ConfirmationDialog(
            title = "Deny Access Request?",
            message = "This will terminate the pending authentication request for ${requestToDeny?.resourceName}. The requester will be notified that access was refused under Zero Trust policy.",
            confirmText = "Deny Request",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = {
                requestToDeny?.let { onDenyRequest(it.id) }
                requestToDeny = null
                onSelectRequest(null)
            },
            onDismiss = { requestToDeny = null }
        )
    }
}

@Composable
fun ProtectedResourceCard(
    resource: ProtectedResource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("resource_card_${resource.id}"),
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
                        text = resource.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${resource.category.displayName} • ${resource.accessLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                }

                // Classification Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceElevated)
                        .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = resource.classification.label,
                        color = when (resource.classification) {
                            AccessClassification.CRITICAL -> CyberCriticalRed
                            AccessClassification.CONFIDENTIAL -> Color(0xFFF43F5E)
                            AccessClassification.RESTRICTED -> CyberWarningAmber
                            AccessClassification.INTERNAL -> CyberBlue
                            AccessClassification.PUBLIC -> CyberSecureGreen
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (resource.accessStatus) {
                        ResourceAccessStatus.GRANTED -> CyberSecureGreen
                        ResourceAccessStatus.APPROVAL_REQUIRED -> CyberWarningAmber
                        ResourceAccessStatus.RESTRICTED -> CyberCriticalRed
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Access: ${resource.accessStatus.label}",
                        fontSize = 12.sp,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Details",
                    tint = CyberTextMuted
                )
            }
        }
    }
}

@Composable
fun ResourceDetailDialog(
    resource: ProtectedResource,
    onDismiss: () -> Unit,
    onRequestAccess: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = resource.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Text(
                    text = resource.category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberCyan
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (resource.description.isNotEmpty()) {
                    Text(
                        text = resource.description,
                        fontSize = 13.sp,
                        color = CyberTextSecondary
                    )
                    HorizontalDivider(color = CyberBorderSubtle)
                }

                DetailRow("Classification", resource.classification.label)
                DetailRow("Access Level", resource.accessLevel)
                DetailRow("MFA Required", if (resource.mfaRequired) "FIDO2 / TOTP" else "Optional")
                DetailRow("Device Trust", if (resource.deviceTrustRequired) "Strict Enrolled" else "Any Compliant")
                DetailRow("Risk Threshold", resource.riskThreshold.label)
                DetailRow("Session Token TTL", "${resource.sessionDurationMinutes} minutes")

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Current Status", fontSize = 13.sp, color = CyberTextSecondary)
                    Text(
                        text = resource.accessStatus.label,
                        color = if (resource.accessStatus == ResourceAccessStatus.GRANTED) CyberSecureGreen else CyberWarningAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            if (resource.accessStatus != ResourceAccessStatus.GRANTED) {
                Button(
                    onClick = onRequestAccess,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("request_resource_access_button")
                ) {
                    Text("Request Access", fontWeight = FontWeight.Bold)
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
fun AccessRequestDetailDialog(
    request: AccessRequest,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onDeny: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Access Request",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = request.resourceName,
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberCyan
                    )
                }
                RiskBadge(riskLevel = request.riskLevel)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailRow("Requester", request.requesterEmail)
                DetailRow("Device", request.deviceName)
                DetailRow("OS", request.os)
                DetailRow("Location", request.location)
                DetailRow("IP Address", request.ip)
                DetailRow("Requested", request.requestedTimeAgo)

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = CyberBorderSubtle)

                Text(
                    text = "RISK ASSESSMENT FACTORS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted,
                    letterSpacing = 1.sp
                )

                request.riskFactors.forEach { factor ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (factor.isPositive) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (factor.isPositive) CyberSecureGreen else CyberWarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = factor.label,
                            fontSize = 12.sp,
                            color = if (factor.isPositive) CyberTextSecondary else CyberWarningAmber
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDeny,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCriticalRed),
                    border = BorderStroke(1.dp, CyberCriticalRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Deny", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Approve", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = CyberTextSecondary)
            }
        },
        containerColor = CyberSurfaceElevated,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = CyberTextSecondary)
        Text(text = value, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
    }
}
