package com.skintrack.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.theme.ErrorLight
import com.skintrack.app.ui.theme.InfoLight
import com.skintrack.app.ui.theme.SuccessLight
import com.skintrack.app.ui.theme.WarningLight
import com.skintrack.app.ui.theme.spacing

enum class SnackbarType {
    SUCCESS, ERROR, WARNING, INFO;

    val icon: String
        get() = when (this) {
            SUCCESS -> "\u2713"
            ERROR -> "\u2717"
            WARNING -> "\u26A0"
            INFO -> "\u2139"
        }

    val color: Color
        get() = when (this) {
            SUCCESS -> SuccessLight
            ERROR -> ErrorLight
            WARNING -> WarningLight
            INFO -> InfoLight
        }
}

data class SnackbarMessage(
    val message: String,
    val type: SnackbarType = SnackbarType.SUCCESS,
)

/**
 * Custom Snackbar visual that matches the design spec with type-specific icon prefix.
 *
 * Usage: Pass the [SnackbarType] to control icon and accent color.
 * The type is encoded in the snackbar's actionLabel as "SUCCESS", "ERROR", etc.
 */
@Composable
fun AppSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val type = snackbarData.visuals.actionLabel
        ?.let { runCatching { SnackbarType.valueOf(it) }.getOrNull() }
        ?: SnackbarType.SUCCESS

    val spacing = MaterialTheme.spacing

    Snackbar(
        modifier = modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(type.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = type.icon,
                    color = type.color,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Text(
                text = snackbarData.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
