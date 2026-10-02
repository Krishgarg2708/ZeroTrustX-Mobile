package com.example.presentation.rbac

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RbacRole
import com.example.ui.theme.*

@Composable
fun RbacScreen(
    roles: List<RbacRole>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp)
            .testTag("rbac_screen"),
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
                        text = "RBAC & Least Privilege",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = "Zero Standing Privileges • Role Matrix",
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        // Philosophy Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberSecureGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LEAST PRIVILEGE ARCHITECTURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ZeroTrustX enforces Just-In-Time (JIT) access tokens. Users only receive the narrowest permissions necessary for their approved role, expiring within 30 to 60 minutes.",
                        fontSize = 12.sp,
                        color = CyberTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "ENTERPRISE ROLE DIRECTORY (${roles.size})",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted,
                letterSpacing = 1.sp
            )
        }

        items(roles, key = { it.id }) { role ->
            RbacRoleCard(role = role)
        }
    }
}

@Composable
fun RbacRoleCard(role: RbacRole) {
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = role.roleName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = role.privilegeLevel,
                        fontSize = 12.sp,
                        color = CyberCyan,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurface)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${role.permissionCount} Perms",
                        fontSize = 10.sp,
                        color = CyberTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = role.description,
                fontSize = 12.sp,
                color = CyberTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CyberBorderSubtle)
            Spacer(modifier = Modifier.height(10.dp))

            // Allowed vs Restricted
            Text("ALLOWED ENTITLEMENTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberSecureGreen)
            Spacer(modifier = Modifier.height(4.dp))
            role.allowedResources.forEach { res ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = CyberSecureGreen, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(res, fontSize = 11.sp, color = CyberTextPrimary)
                }
            }

            if (role.restrictedResources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("RESTRICTED PERIMETER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCriticalRed)
                Spacer(modifier = Modifier.height(4.dp))
                role.restrictedResources.forEach { res ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Icon(Icons.Default.Block, contentDescription = null, tint = CyberCriticalRed, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(res, fontSize = 11.sp, color = CyberTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurface)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Least Privilege: ${role.leastPrivilegeAdherence}",
                        fontSize = 11.sp,
                        color = CyberCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
