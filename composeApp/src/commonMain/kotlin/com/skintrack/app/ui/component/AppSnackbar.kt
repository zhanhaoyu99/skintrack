package com.skintrack.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.theme.Error500
import com.skintrack.app.ui.theme.Info500
import com.skintrack.app.ui.theme.Success500
import com.skintrack.app.ui.theme.Success600
import com.skintrack.app.ui.theme.Warning500
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

    /** Accent color for icon circles in non-toast contexts */
    val color: Color
        get() = when (this) {
            SUCCESS -> Success500
            ERROR -> Error500
            WARNING -> Warning500
            INFO -> Info500
        }

    /** Full-bleed container background matching HTML toast spec */
    val containerColor: Color
        get() = when (this) {
            SUCCESS -> Success600    // --success-600
            ERROR -> Error500        // --error-500
            WARNING -> Warning500    // --warning-500
            INFO -> Info500          // --info-500
        }

    /** Content/text color on the colored container */
    val onContainerColor: Color
        get() = when (this) {
            WARNING -> Color(0xFF1A1A1A)  // dark text on yellow
            else -> Color.White           // white text on dark bg
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
        containerColor = type.containerColor,
        contentColor = type.onContainerColor,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.listGap),
            modifier = Modifier,
        ) {
            // Icon: 20dp matching --icon-size-sm
            Text(
                text = type.icon,
                color = type.onContainerColor,
                fontSize = 20.sp,
                modifier = Modifier.size(20.dp),
            )

            // Message text: b2 = 14sp / weight 500
            Text(
                text = snackbarData.visuals.message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = type.onContainerColor,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
