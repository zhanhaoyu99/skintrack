package com.skintrack.app.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skintrack.app.ui.theme.spacing

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val resolvedPadding = contentPadding ?: PaddingValues(MaterialTheme.spacing.md)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(resolvedPadding),
            content = content,
        )
    }
}
