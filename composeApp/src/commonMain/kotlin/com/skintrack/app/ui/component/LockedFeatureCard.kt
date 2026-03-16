package com.skintrack.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.dimens
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
    tags: List<String> = emptyList(),
    showTrialHint: Boolean = true,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        ) {
            // Lock icon: 56dp circle with Apricot-50 background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Apricot50,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\uD83D\uDD12",
                    fontSize = 24.sp,
                )
            }

            // Message text
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Tag pills with outline border
            if (tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.sm,
                        Alignment.CenterHorizontally,
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    tags.forEach { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .background(
                                    color = Color.White,
                                    shape = FullRoundedShape,
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = FullRoundedShape,
                                )
                                .padding(
                                    horizontal = MaterialTheme.spacing.md,
                                    vertical = MaterialTheme.spacing.xs + 2.dp,
                                ),
                        )
                    }
                }
            }

            // Gold/Amber gradient upgrade button (pill shape)
            val buttonShape = FullRoundedShape
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeight)
                    .shadow(
                        elevation = 8.dp,
                        shape = buttonShape,
                        ambientColor = UpgradeGradientEnd.copy(alpha = 0.35f),
                        spotColor = UpgradeGradientEnd.copy(alpha = 0.35f),
                    )
                    .clip(buttonShape)
                    .background(
                        brush = Brush.horizontalGradient(
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Trial hint: green pill
            if (showTrialHint) {
                Text(
                    text = "\u24D8 新用户享 14 天免费试用",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669),
                    modifier = Modifier
                        .background(
                            color = Color(0xFFECFDF5),
                            shape = FullRoundedShape,
                        )
                        .padding(
                            horizontal = MaterialTheme.spacing.md,
                            vertical = MaterialTheme.spacing.xs + 1.dp,
                        ),
                )
            }
        }
    }
}
