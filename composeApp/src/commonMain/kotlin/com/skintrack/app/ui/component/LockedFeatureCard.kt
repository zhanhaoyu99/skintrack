package com.skintrack.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing

// Gold/Amber gradient for upgrade button (matching design spec)
private val UpgradeGradientStart = Color(0xFFF59E0B)
private val UpgradeGradientEnd = Color(0xFFD97706)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LockedFeatureCard(
    message: String,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    tags: List<String> = emptyList(),
    showTrialHint: Boolean = true,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFD97706).copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.large,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF9F0),
                            Color(0xFFFFF3E0),
                            Color(0xFFFEF3C7),
                        ),
                    ),
                )
                .padding(MaterialTheme.spacing.cardInner),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Crown icon: 56dp circle with golden gradient background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = Color(0xFFFFD700).copy(alpha = 0.2f),
                        spotColor = Color(0xFFFFAA00).copy(alpha = 0.3f),
                    )
                    .background(
                        brush = MaterialTheme.gradients.vipBadge,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\uD83D\uDC51",
                    fontSize = 24.sp,
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.listGap))

            // Title: h4 = 16sp/600/-0.1
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.1).sp,
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

            // Subtitle description: b3 = 13sp/400
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.listGap))

            // Tag pills: c2 = 10sp/600, gap=6dp, warm tint bg
            if (tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.iconGap,
                        Alignment.CenterHorizontally,
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    tags.forEach { tag ->
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE07B3C),
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF4A261).copy(alpha = 0.08f),
                                    shape = FullRoundedShape,
                                )
                                .padding(
                                    horizontal = MaterialTheme.spacing.compact,
                                    vertical = MaterialTheme.spacing.xs,
                                ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            // Gold/Amber gradient upgrade button: btn-sm = 40dp/14sp/600
            val buttonShape = FullRoundedShape
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeightSm)
                    .shadow(
                        elevation = 16.dp,
                        shape = buttonShape,
                        ambientColor = UpgradeGradientEnd.copy(alpha = 0.35f),
                        spotColor = UpgradeGradientEnd.copy(alpha = 0.35f),
                    )
                    .clip(buttonShape)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(UpgradeGradientStart, UpgradeGradientEnd),
                        ),
                        shape = buttonShape,
                    )
                    .clickable(onClick = onUpgrade),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u2B50 升级 Pro 解锁",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // Trial hint: c2 = 10sp/600, green pill
            if (showTrialHint) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.compact))
                Text(
                    text = "\u24D8 新用户享 14 天免费试用",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF059669),
                    modifier = Modifier
                        .background(
                            color = Color(0xFFECFDF5),
                            shape = FullRoundedShape,
                        )
                        .padding(
                            horizontal = MaterialTheme.spacing.compact,
                            vertical = MaterialTheme.spacing.xxs,
                        ),
                )
            }
        }
    }
}
