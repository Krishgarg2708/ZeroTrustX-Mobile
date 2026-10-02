package com.example.presentation.scenario

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.ScenarioSimulationStage
import com.example.presentation.components.RiskBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SecurityScenarioScreen(
    stages: List<ScenarioSimulationStage>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentStageIndex by remember { mutableIntStateOf(0) }
    var isAutoPlaying by remember { mutableStateOf(false) }

    val currentStage = stages[currentStageIndex.coerceIn(0, stages.size - 1)]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("security_scenario_screen"),
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
                        text = "Zero Trust Demo Scenario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "PRESENTATION DEMO WALKTHROUGH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }
        }

        // Demo Disclaimer Notice (Requirement 10)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Slideshow, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMULATED FRONTEND SCENARIO • Demonstrates automated zero trust containment during credential compromise.",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        // Stepper Navigation Indicator
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                stages.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (i == currentStageIndex) CyberCyan
                                else if (i < currentStageIndex) CyberBlue
                                else CyberBorder
                            )
                    )
                }
            }
        }

        // Active Stage Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(
                    1.5.dp,
                    when (currentStage.riskLevel) {
                        RiskLevel.LOW -> CyberSecureGreen
                        RiskLevel.MEDIUM -> CyberWarningAmber
                        RiskLevel.HIGH, RiskLevel.CRITICAL -> CyberCriticalRed
                    }
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STAGE ${currentStage.stageNumber} OF ${stages.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                        RiskBadge(riskLevel = currentStage.riskLevel)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentStage.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentStage.description,
                        fontSize = 13.sp,
                        color = CyberTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CyberBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simulated Posture Score:", fontSize = 12.sp, color = CyberTextSecondary)
                        Text(
                            text = "${currentStage.postureScore} / 100",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentStage.postureScore >= 80) CyberSecureGreen else if (currentStage.postureScore >= 60) CyberWarningAmber else CyberCriticalRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("DETECTED CONTEXT VARIANCE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(currentStage.detectedChange, fontSize = 12.sp, color = CyberTextPrimary)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("AUTOMATED ZERO TRUST ACTION:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentStage.policyAction,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (currentStage.policyAction.contains("DENIED")) CyberCriticalRed else if (currentStage.policyAction.contains("STEP-UP")) CyberWarningAmber else CyberSecureGreen
                    )
                }
            }
        }

        // Scenario Controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStageIndex > 0) currentStageIndex--
                    },
                    enabled = currentStageIndex > 0,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberTextPrimary),
                    border = BorderStroke(1.dp, CyberBorder)
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = {
                        if (currentStageIndex < stages.size - 1) {
                            currentStageIndex++
                        } else {
                            currentStageIndex = 0 // loop
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                ) {
                    Text(if (currentStageIndex == stages.size - 1) "Restart" else "Next Stage")
                }
            }
        }

        // Auto-play button
        item {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        isAutoPlaying = true
                        for (i in currentStageIndex until stages.size) {
                            currentStageIndex = i
                            delay(2500)
                        }
                        isAutoPlaying = false
                    }
                },
                enabled = !isAutoPlaying,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isAutoPlaying) "Auto-Playing Scenario..." else "Auto-Play Full Scenario Demo")
            }
        }
    }
}
