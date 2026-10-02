package com.example.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun RiskBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val (bg, textColor, borderColor) = when (riskLevel) {
        RiskLevel.LOW -> Triple(Color(0xFF064E3B), CyberSecureGreen, Color(0xFF047857))
        RiskLevel.MEDIUM -> Triple(Color(0xFF451A03), CyberWarningAmber, Color(0xFFB45309))
        RiskLevel.HIGH -> Triple(Color(0xFF450A0A), Color(0xFFF87171), Color(0xFFB91C1C))
        RiskLevel.CRITICAL -> Triple(Color(0xFF500724), CyberCriticalRed, Color(0xFFBE123C))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("risk_badge_${riskLevel.label.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = riskLevel.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun SecurityScoreCard(
    posture: SecurityPosture,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scoreProgress by animateFloatAsState(
        targetValue = posture.score / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "score_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("security_score_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyberSecureGreen)
                    )
                    Text(
                        text = "SECURITY SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${posture.score}",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberTextPrimary,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = " / 100",
                        fontSize = 16.sp,
                        color = CyberTextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = CyberSecureGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${posture.statusText} • ${posture.overallPosture}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberSecureGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to inspect Zero Trust posture details →",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextMuted
                )
            }

            // Circular Visual Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(92.dp)
                    .padding(4.dp)
            ) {
                // Background Track
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = CyberBorder,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = if (posture.score >= 80) CyberSecureGreen else CyberWarningAmber,
                        startAngle = -90f,
                        sweepAngle = 360f * scoreProgress,
                        useCenter = false,
                        style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = CyberCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "${posture.score}%",
                        color = CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("metric_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberTextSecondary
                )
            }
        }

        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    color = CyberBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AccessRequestCard(
    request: AccessRequest,
    onCardClick: () -> Unit,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("request_card_${request.id}"),
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
                        text = request.resourceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${request.deviceName} • ${request.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                }
                RiskBadge(riskLevel = request.riskLevel)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Requested by ${request.requesterName} • ${request.requestedTimeAgo}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextMuted
                )

                if (request.status == RequestStatus.APPROVED) {
                    Text(
                        text = "✓ APPROVED",
                        color = CyberSecureGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (request.status == RequestStatus.DENIED) {
                    Text(
                        text = "✕ DENIED",
                        color = CyberCriticalRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (request.status == RequestStatus.PENDING) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDeny,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("deny_button_${request.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberCriticalRed
                        ),
                        border = BorderStroke(1.dp, CyberCriticalRed.copy(alpha = 0.5f))
                    ) {
                        Text("DENY", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onApprove,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("approve_button_${request.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberBlue
                        )
                    ) {
                        Text("APPROVE", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("device_card_${device.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                val icon = when (device.type) {
                    DeviceType.LAPTOP -> Icons.Default.Laptop
                    DeviceType.MOBILE -> Icons.Default.Smartphone
                    DeviceType.TABLET -> Icons.Default.Tablet
                    DeviceType.WORKSTATION -> Icons.Default.Computer
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (device.isTrusted) CyberCyan else CyberWarningAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${device.os} • ${device.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (device.isTrusted) CyberSecureGreen else CyberWarningAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (device.isTrusted) "Trusted • ${device.lastActive}" else "Limited Trust • ${device.lastActive}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (device.isTrusted) CyberSecureGreen else CyberWarningAmber
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = CyberTextMuted
            )
        }
    }
}

@Composable
fun AlertCard(
    alert: SecurityAlert,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("alert_card_${alert.id}"),
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
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (alert.severity) {
                        RiskLevel.CRITICAL -> Icons.Default.Dangerous
                        RiskLevel.HIGH -> Icons.Default.Warning
                        RiskLevel.MEDIUM -> Icons.Default.ReportProblem
                        RiskLevel.LOW -> Icons.Default.CheckCircle
                    }
                    val iconTint = when (alert.severity) {
                        RiskLevel.CRITICAL -> CyberCriticalRed
                        RiskLevel.HIGH -> Color(0xFFF87171)
                        RiskLevel.MEDIUM -> CyberWarningAmber
                        RiskLevel.LOW -> CyberSecureGreen
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                RiskBadge(riskLevel = alert.severity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall,
                color = CyberTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${alert.deviceName} • ${alert.location}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextMuted
                )
                Text(
                    text = alert.detectedTimeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan
                )
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = CyberTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) CyberCriticalRed else CyberBlue
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = cancelText, color = CyberTextSecondary)
            }
        },
        containerColor = CyberSurfaceElevated,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CyberSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = CyberTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun LockdownBanner(
    lockdownState: LockdownState,
    onDisengage: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!lockdownState.isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "lockdown_pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lockdown_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lockdown_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCriticalRedBg.copy(alpha = alphaAnim)),
        border = BorderStroke(1.5.dp, CyberCriticalRed)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ShieldMoon,
                    contentDescription = "Lockdown",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "EMERGENCY ZERO TRUST LOCKDOWN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Dual-custody verification enforced. Non-essential tokens isolated.",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD1D1)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onDisengage,
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "DISENGAGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }
        }
    }
}

@Composable
fun ThreatRadarView(
    nodes: List<RadarThreatNode>,
    selectedNode: RadarThreatNode?,
    onSelectNode: (RadarThreatNode?) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("threat_radar_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GLOBAL THREAT RADAR & SENSORS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "${nodes.size} ENCLAVES LIVE",
                    fontSize = 10.sp,
                    color = CyberSecureGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Radar Scope Canvas
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceElevated)
                    .border(BorderStroke(1.5.dp, CyberBorder), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                    val maxRadius = size.width / 2

                    // Concentric Range Rings
                    listOf(0.33f, 0.66f, 0.95f).forEach { ratio ->
                        drawCircle(
                            color = CyberBorder,
                            radius = maxRadius * ratio,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Crosshair grid lines
                    drawLine(
                        color = CyberBorderSubtle,
                        start = androidx.compose.ui.geometry.Offset(center.x, 0f),
                        end = androidx.compose.ui.geometry.Offset(center.x, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = CyberBorderSubtle,
                        start = androidx.compose.ui.geometry.Offset(0f, center.y),
                        end = androidx.compose.ui.geometry.Offset(size.width, center.y),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Sweeping Beam Wedge
                    drawArc(
                        brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                CyberCyan.copy(alpha = 0.05f),
                                CyberCyan.copy(alpha = 0.35f)
                            )
                        ),
                        startAngle = sweepAngle - 55f,
                        sweepAngle = 55f,
                        useCenter = true
                    )
                }

                // Plot Nodes as Interactive Clickable Overlays
                nodes.forEach { node ->
                    val angleRad = Math.toRadians(node.angleDegrees.toDouble())
                    val radiusPx = 110.dp.value * node.radiusRatio
                    val offsetX = (radiusPx * Math.cos(angleRad)).dp
                    val offsetY = (radiusPx * Math.sin(angleRad)).dp

                    val dotColor = when (node.status) {
                        NodeSecurityStatus.SECURE -> CyberSecureGreen
                        NodeSecurityStatus.CAUTION -> CyberWarningAmber
                        NodeSecurityStatus.ANOMALY -> CyberCriticalRed
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = offsetX, y = offsetY)
                            .size(24.dp)
                            .clickable { onSelectNode(node) }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(dotColor.copy(alpha = 0.25f))
                                .border(BorderStroke(1.5.dp, dotColor), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Node Telemetry Banner
            if (selectedNode != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedNode.locationName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CyberTextPrimary
                            )
                            Text(
                                text = "${selectedNode.latencyMs}ms • ${selectedNode.ip}",
                                fontSize = 11.sp,
                                color = CyberCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedNode.threatSummary,
                            fontSize = 11.sp,
                            color = CyberTextSecondary
                        )
                    }
                }
            } else {
                Text(
                    text = "Tap any radar node to inspect gateway telemetry",
                    fontSize = 11.sp,
                    color = CyberTextMuted
                )
            }
        }
    }
}

@Composable
fun BiometricScanDialog(
    isScanning: Boolean,
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_scan")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_offset"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isSuccess) "Identity Verified" else "Biometric Verification",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccess) CyberSecureGreen else CyberTextPrimary
                )
                Text(
                    text = "Secure Enclave Hardware Handshake",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(CyberSurface)
                        .border(
                            BorderStroke(2.dp, if (isSuccess) CyberSecureGreen else CyberCyan),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                        contentDescription = "Sensor",
                        tint = if (isSuccess) CyberSecureGreen else CyberCyan,
                        modifier = Modifier.size(54.dp)
                    )

                    if (isScanning) {
                        // Laser Scan Line
                        Box(
                            modifier = Modifier
                                .offset(y = laserOffset.dp)
                                .fillMaxWidth(0.7f)
                                .height(2.5.dp)
                                .background(CyberCyanNeon)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isSuccess) "✓ BIOMETRIC MATCH VERIFIED\nSecure Enclave Key Released" else if (isScanning) "Reading hardware fingerprint..." else "Touch sensor to authenticate",
                    fontSize = 13.sp,
                    color = if (isSuccess) CyberSecureGreen else CyberTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = if (isSuccess) FontWeight.Bold else FontWeight.Normal
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CyberTextSecondary)
            }
        },
        containerColor = CyberSurfaceElevated,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AuditEventCard(
    event: AuditEvent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audit_card_${event.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val (categoryColor, categoryLabel) = when (event.category) {
                        AuditCategory.AUTH -> Pair(CyberBlue, "AUTH")
                        AuditCategory.ACCESS -> Pair(CyberCyan, "ACCESS")
                        AuditCategory.NETWORK -> Pair(CyberIndigo, "NETWORK")
                        AuditCategory.THREAT -> Pair(CyberCriticalRed, "THREAT")
                        AuditCategory.ADMIN -> Pair(CyberWarningAmber, "ADMIN")
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .border(BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f)), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = categoryLabel,
                            color = categoryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = event.actor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberTextPrimary
                    )
                }

                Text(
                    text = event.timestamp,
                    fontSize = 11.sp,
                    color = CyberTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.actionSummary,
                fontSize = 13.sp,
                color = CyberTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${event.location} • ${event.ip}",
                fontSize = 10.sp,
                color = CyberTextMuted
            )
        }
    }
}

