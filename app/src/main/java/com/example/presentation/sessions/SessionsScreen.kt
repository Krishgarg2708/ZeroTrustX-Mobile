package com.example.presentation.sessions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.ActiveSession
import com.example.presentation.components.ConfirmationDialog
import com.example.presentation.components.EmptyState
import com.example.ui.theme.*

@Composable
fun SessionsScreen(
    sessions: List<ActiveSession>,
    showTerminateAllDialog: Boolean,
    onShowTerminateAllDialog: (Boolean) -> Unit,
    onTerminateSession: (String) -> Unit,
    onTerminateAllOtherSessions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sessionToTerminate by remember { mutableStateOf<ActiveSession?>(null) }
    val remoteSessions = sessions.filter { !it.isCurrent }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("sessions_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Active Sessions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Zero Trust bearer tokens and authenticated endpoints",
                    fontSize = 13.sp,
                    color = CyberTextSecondary
                )
            }
        }

        // Action Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${sessions.size} Authorized Sessions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )
                            Text(
                                text = "Tokens expire every 30 minutes unless refreshed",
                                fontSize = 12.sp,
                                color = CyberTextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (remoteSessions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { onShowTerminateAllDialog(true) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("terminate_all_sessions_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCriticalRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Terminate All Other Sessions", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        if (sessions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Devices,
                    title = "No Active Sessions",
                    message = "No authenticated sessions currently exist."
                )
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                SessionCardItem(
                    session = session,
                    onTerminate = { sessionToTerminate = session }
                )
            }
        }
    }

    // Terminate Single Session Confirmation
    if (sessionToTerminate != null) {
        ConfirmationDialog(
            title = "Terminate Session?",
            message = "This will immediately revoke access for '${sessionToTerminate?.clientInfo}' in ${sessionToTerminate?.location}. Any unsaved state on that endpoint will be disconnected.",
            confirmText = "Terminate Session",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = {
                sessionToTerminate?.let { onTerminateSession(it.id) }
                sessionToTerminate = null
            },
            onDismiss = { sessionToTerminate = null }
        )
    }

    // Terminate All Other Sessions Confirmation
    if (showTerminateAllDialog) {
        ConfirmationDialog(
            title = "Terminate All Remote Sessions?",
            message = "This will immediately invalidate all Zero Trust credentials and tokens on ${remoteSessions.size} other devices. Only your current mobile session will remain active.",
            confirmText = "Terminate All",
            cancelText = "Cancel",
            isDestructive = true,
            onConfirm = onTerminateAllOtherSessions,
            onDismiss = { onShowTerminateAllDialog(false) }
        )
    }
}

@Composable
fun SessionCardItem(
    session: ActiveSession,
    onTerminate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("session_item_${session.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, if (session.isCurrent) CyberCyan.copy(alpha = 0.5f) else CyberBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = session.deviceName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        if (session.isCurrent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "THIS DEVICE",
                                    color = CyberCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = session.clientInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                }

                if (!session.isCurrent) {
                    IconButton(
                        onClick = onTerminate,
                        modifier = Modifier.testTag("terminate_button_${session.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Terminate Session",
                            tint = CyberCriticalRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${session.location} • ${session.ip}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextMuted
                )
                Text(
                    text = session.lastActive,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (session.isCurrent) CyberSecureGreen else CyberTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
