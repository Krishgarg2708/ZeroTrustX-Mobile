package com.example.presentation.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.model.RiskLevel
import com.example.data.model.SecurityAlert
import com.example.presentation.components.AlertCard
import com.example.presentation.components.ConfirmationDialog
import com.example.presentation.components.EmptyState
import com.example.presentation.components.RiskBadge
import com.example.ui.theme.*

@Composable
fun AlertsScreen(
    alerts: List<SecurityAlert>,
    selectedFilter: RiskLevel?,
    selectedAlert: SecurityAlert?,
    onFilterChange: (RiskLevel?) -> Unit,
    onSelectAlert: (SecurityAlert?) -> Unit,
    onDismissAlert: (String) -> Unit,
    onBlockDevice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var alertToBlock by remember { mutableStateOf<SecurityAlert?>(null) }

    val activeAlerts = alerts.filter { !it.isDismissed }
    val filteredAlerts = if (selectedFilter == null) {
        activeAlerts
    } else {
        activeAlerts.filter { it.severity == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("alerts_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Security Alerts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Real-time threat detection, anomaly scoring & incident response",
                    fontSize = 13.sp,
                    color = CyberTextSecondary
                )
            }
        }

        // Severity Filter Chips (Section 20)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { onFilterChange(null) },
                        label = { Text("All (${activeAlerts.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberBlue,
                            selectedLabelColor = Color.White,
                            containerColor = CyberSurfaceCard,
                            labelColor = CyberTextSecondary
                        ),
                        border = BorderStroke(1.dp, CyberBorder)
                    )
                }
                items(RiskLevel.entries) { severity ->
                    val count = activeAlerts.count { it.severity == severity }
                    FilterChip(
                        selected = selectedFilter == severity,
                        onClick = { onFilterChange(if (selectedFilter == severity) null else severity) },
                        label = { Text("${severity.label} ($count)") },
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

        if (filteredAlerts.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Shield,
                    title = "No Active Alerts",
                    message = "Your Zero Trust environment is clear. No threat anomalies detected."
                )
            }
        } else {
            items(filteredAlerts, key = { it.id }) { alert ->
                AlertCard(
                    alert = alert,
                    onClick = { onSelectAlert(alert) }
                )
            }
        }
    }

    // Alert Detail Dialog (Section 21)
    if (selectedAlert != null) {
        AlertDetailDialog(
            alert = selectedAlert,
            onDismiss = { onSelectAlert(null) },
            onDismissAlertAction = {
                onDismissAlert(selectedAlert.id)
            },
            onBlockDeviceAction = {
                alertToBlock = selectedAlert
            }
        )
    }

    // Block Device Confirmation Dialog
    if (alertToBlock != null) {
        ConfirmationDialog(
            title = "Block & Isolate Device?",
            message = "This will immediately revoke network access for '${alertToBlock?.deviceName}' across all Zero Trust gateways and terminate active sessions.",
            confirmText = "Block Device",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = {
                alertToBlock?.let { onBlockDevice(it.id) }
                alertToBlock = null
                onSelectAlert(null)
            },
            onDismiss = { alertToBlock = null }
        )
    }
}

@Composable
fun AlertDetailDialog(
    alert: SecurityAlert,
    onDismiss: () -> Unit,
    onDismissAlertAction: () -> Unit,
    onBlockDeviceAction: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Security Alert",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                }
                RiskBadge(riskLevel = alert.severity)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = alert.description,
                    fontSize = 13.sp,
                    color = CyberTextSecondary
                )

                HorizontalDivider(color = CyberBorderSubtle)

                DetailRow("Detected", alert.detectedTimeAgo)
                DetailRow("Endpoint", alert.deviceName)
                DetailRow("Location", alert.location)
                DetailRow("IP Address", alert.ip)
                DetailRow("Calculated Risk Score", "${alert.riskScore} / 100")

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = CyberBorderSubtle)

                Text(
                    text = "IDENTIFIED RISK FACTORS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted,
                    letterSpacing = 1.sp
                )

                alert.riskFactors.forEach { factor ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "•",
                            color = CyberCriticalRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = factor,
                            fontSize = 12.sp,
                            color = CyberTextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDismissAlertAction,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextSecondary),
                    border = BorderStroke(1.dp, CyberBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Dismiss Alert")
                }

                Button(
                    onClick = onBlockDeviceAction,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCriticalRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Block Device", fontWeight = FontWeight.Bold)
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
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = CyberTextSecondary)
        Text(text = value, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)
    }
}
