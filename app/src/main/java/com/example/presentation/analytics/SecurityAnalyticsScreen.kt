package com.example.presentation.analytics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayActivityTrend
import com.example.data.model.RiskLevel
import com.example.data.model.SecurityAnalyticsSummary
import com.example.ui.theme.*

@Composable
fun SecurityAnalyticsScreen(
    analyticsData: SecurityAnalyticsSummary,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("security_analytics_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberTextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Security Analytics",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "Zero Trust Traffic, Risk & Telemetry Trends",
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsMetricPill(
                    title = "Allowed",
                    value = "${analyticsData.allowedCount}",
                    percentage = "82%",
                    color = CyberSecureGreen,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricPill(
                    title = "Challenged",
                    value = "${analyticsData.challengedCount}",
                    percentage = "11%",
                    color = CyberWarningAmber,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricPill(
                    title = "Denied",
                    value = "${analyticsData.deniedCount}",
                    percentage = "7%",
                    color = CyberCriticalRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Requests Ratio Visual Segment Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACCESS DECISION RATIO (PAST 30 DAYS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Multi-segment horizontal progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.82f).fillMaxHeight().background(CyberSecureGreen))
                        Box(modifier = Modifier.weight(0.11f).fillMaxHeight().background(CyberWarningAmber))
                        Box(modifier = Modifier.weight(0.07f).fillMaxHeight().background(CyberCriticalRed))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendItem("Allowed (82%)", CyberSecureGreen)
                        LegendItem("Step-Up (11%)", CyberWarningAmber)
                        LegendItem("Denied (7%)", CyberCriticalRed)
                    }
                }
            }
        }

        // Weekly Request Volume Trend Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LOGIN & ACCESS ATTEMPTS (7-DAY TREND)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    WeeklyActivityBarChart(weeklyTrend = analyticsData.weeklyTrend)
                }
            }
        }

        // Risk Level Distribution
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "RISK PROFILE DISTRIBUTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    RiskDistributionRow("Low Risk (0-30)", 68, CyberSecureGreen)
                    RiskDistributionRow("Medium Risk (31-60)", 22, CyberWarningAmber)
                    RiskDistributionRow("High Risk (61-80)", 8, Color(0xFFF87171))
                    RiskDistributionRow("Critical Risk (81-100)", 2, CyberCriticalRed)
                }
            }
        }

        // Device Trust Changes Log
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DEVICE TRUST SHIFT AUDIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    analyticsData.deviceTrustShifts.forEach { shift ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChangeCircle,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp).padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(shift, fontSize = 12.sp, color = CyberTextPrimary, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsMetricPill(
    title: String,
    value: String,
    percentage: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = CyberTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
            Text(percentage, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, color = CyberTextSecondary)
    }
}

@Composable
private fun RiskDistributionRow(label: String, percentage: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = CyberTextPrimary)
            Text("$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = CyberBorder
        )
    }
}

@Composable
private fun WeeklyActivityBarChart(weeklyTrend: List<DayActivityTrend>) {
    val maxRequests = weeklyTrend.maxOfOrNull { it.totalRequests } ?: 350

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width / (weeklyTrend.size * 2)
                val spacing = size.width / weeklyTrend.size

                weeklyTrend.forEachIndexed { i, day ->
                    val x = i * spacing + (spacing - barWidth) / 2
                    val barHeight = (day.totalRequests.toFloat() / maxRequests) * size.height

                    // Allowed portion
                    drawRoundRect(
                        color = CyberBlue,
                        topLeft = Offset(x, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Challenge/Block sub-marker
                    val challengeHeight = (day.challengesOrBlocks.toFloat() / maxRequests) * size.height
                    drawRoundRect(
                        color = CyberWarningAmber,
                        topLeft = Offset(x, size.height - challengeHeight),
                        size = Size(barWidth, challengeHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weeklyTrend.forEach { day ->
                Text(
                    text = day.day,
                    fontSize = 10.sp,
                    color = CyberTextMuted,
                    modifier = Modifier.width(36.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
