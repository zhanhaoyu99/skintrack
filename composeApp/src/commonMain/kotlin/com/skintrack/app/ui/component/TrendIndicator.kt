package com.skintrack.app.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.skintrack.app.ui.theme.extendedColors

@Composable
fun TrendIndicator(
    value: Number,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val numValue = value.toDouble()
    val functional = MaterialTheme.extendedColors.functional

    val color: Color
    val text: String

    when {
        numValue > 0 -> {
            color = functional.success
            text = "↑ +$value"
        }
        numValue < 0 -> {
            color = functional.error
            text = "↓ $value"
        }
        else -> {
            color = MaterialTheme.colorScheme.onSurfaceVariant
            text = "± 0"
        }
    }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier,
    )
}
