package com.skintrack.app.snapshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class ProductScreenSnapshotTest : SnapshotTestBase() {

    @Test
    fun product_light() = captureLight {
        ProductPreview()
    }

    @Test
    fun product_dark() = captureDark {
        ProductPreview()
    }
}

private data class MockProduct(
    val name: String,
    val brand: String?,
    val category: String,
    val checked: Boolean,
)

private val sampleProducts = listOf(
    MockProduct("温和洁面乳", "芙丽芳丝", "洁面", true),
    MockProduct("烟酰胺精华", "OLAY", "精华", true),
    MockProduct("水乳套装", "珂润", "乳液", false),
    MockProduct("防晒霜 SPF50+", "安热沙", "防晒", false),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductPreview() {
    val spacing = MaterialTheme.spacing
    val checkedCount = sampleProducts.count { it.checked }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("我的护肤") })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            item {
                SectionHeader("今日护肤 ($checkedCount/${sampleProducts.size})")
            }

            itemsIndexed(sampleProducts) { index, product ->
                Card(
                    modifier = Modifier.fillMaxWidth().animateListItem(index),
                    colors = CardDefaults.cardColors(
                        containerColor = if (product.checked) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerLow
                        },
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = spacing.xs,
                                end = spacing.md,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        Checkbox(checked = product.checked, onCheckedChange = null)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (product.checked) FontWeight.Medium else FontWeight.Normal,
                            )
                            product.brand?.let { brand ->
                                Text(
                                    text = brand,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                SectionHeader(
                    title = "产品库",
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }

            itemsIndexed(sampleProducts) { index, product ->
                Card(
                    modifier = Modifier.fillMaxWidth().animateListItem(index),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            product.brand?.let { brand ->
                                Text(
                                    text = brand,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
