package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class PaywallScreenSnapshotTest : SnapshotTestBase() {

    @Test
    fun paywallScreen_yearlySelected_light() = captureLight {
        PaywallPreview(yearlySelected = true)
    }

    @Test
    fun paywallScreen_monthlySelected_light() = captureLight {
        PaywallPreview(yearlySelected = false)
    }

    @Test
    fun paywallScreen_yearlySelected_dark() = captureDark {
        PaywallPreview(yearlySelected = true)
    }
}

@Composable
private fun PaywallPreview(yearlySelected: Boolean) {
    val spacing = MaterialTheme.spacing

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.gradients.primary)
                    .padding(spacing.xl),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Text(
                        text = "SkinTrack 会员",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        text = "解锁全部功能，科学护肤",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    listOf("无限拍照记录", "详细 AI 分析报告", "归因分析报告", "分享对比卡片").forEach { text ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                            Text(text, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                PlanCardPreview("月度", "¥19.9", "/月", !yearlySelected, Modifier.weight(1f))
                PlanCardPreview("年度（推荐）", "¥168", "/年", yearlySelected, Modifier.weight(1f), "省¥70.8 · ¥14/月")
            }
        }

        item {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(spacing.section),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("立即开通", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = {}) { Text("恢复购买", style = MaterialTheme.typography.bodySmall) }
                Text(" | ", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically))
                TextButton(onClick = {}) { Text("服务条款", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun PlanCardPreview(
    title: String, price: String, period: String,
    isSelected: Boolean, modifier: Modifier, subtitle: String? = null,
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier.border(
            width = MaterialTheme.spacing.xs,
            color = borderColor,
            shape = MaterialTheme.shapes.medium,
        ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
            Text(price, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(period, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
