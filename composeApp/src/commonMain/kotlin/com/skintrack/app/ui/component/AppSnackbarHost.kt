package com.skintrack.app.ui.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Wraps [SnackbarHost] with [AppSnackbar] visual styling.
 */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { snackbarData ->
        AppSnackbar(snackbarData = snackbarData)
    }
}

/**
 * Extension to show a typed snackbar message.
 * The [SnackbarType] is encoded via the actionLabel field.
 */
suspend fun SnackbarHostState.showTyped(
    message: String,
    type: SnackbarType = SnackbarType.SUCCESS,
    duration: SnackbarDuration = if (type == SnackbarType.ERROR) SnackbarDuration.Long else SnackbarDuration.Short,
): SnackbarResult {
    return showSnackbar(
        message = message,
        actionLabel = type.name,
        duration = duration,
    )
}
