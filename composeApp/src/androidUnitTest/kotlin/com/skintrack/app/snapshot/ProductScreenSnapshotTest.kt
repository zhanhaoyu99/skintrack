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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.Apricot300
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Mint300
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose50
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
    val categoryFg: Color,
    val categoryBg: Color,
    val icon: String,
    val checked: Boolean,
    val period: String, // "AM" or "PM"
)

private val sampleProducts = listOf(
    MockProduct("CeraVe 洁面乳", "CeraVe \u00B7 适乐肤", "洁面", Color(0xFF2563EB), Color(0xFFDBEAFE), "\uD83E\uDDF4", true, "AM"),
    MockProduct("薇诺娜 特护精华液", "薇诺娜 \u00B7 Winona", "精华", Lavender300, Lavender50, "\u2728", true, "AM"),
    MockProduct("安耐晒 金瓶防晒", "资生堂 \u00B7 Anessa", "防晒", Apricot300, Apricot50, "\u2600\uFE0F", false, "AM"),
    MockProduct("珂润 保湿面霜", "花王 \u00B7 Curel", "面霜", Mint300, Mint50, "\uD83E\uDED9", false, "PM"),
    MockProduct("SK-II 神仙水", "SK-II", "化妆水", Color(0xFF00838F), Color(0xFFE0F7FA), "\uD83D\uDCA7", false, "PM"),
)

private val filterCategories = listOf("全部", "洁面", "精华", "面霜", "防晒", "化妆水", "面膜")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductPreview() {
    val spacing = MaterialTheme.spacing
    val amProducts = sampleProducts.filter { it.period == "AM" }
    val pmProducts = sampleProducts.filter { it.period == "PM" }
    val checkedCount = sampleProducts.count { it.checked }
    val totalCount = sampleProducts.size
    val uncheckedCount = totalCount - checkedCount
    val progress = checkedCount.toFloat() / totalCount

    Column(modifier = Modifier.fillMaxSize()) {
        // TopAppBar with back + title + add
        TopAppBar(
            title = { Text("护肤品管理") },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )

        // Search bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("搜索产品名称或品牌") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(percent = 50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md),
        )

        // Category filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.padding(vertical = spacing.sm),
        ) {
            items(filterCategories) { label ->
                FilterChip(
                    selected = label == "全部",
                    onClick = {},
                    label = { Text(label) },
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Check-in progress card
            item(key = "progress") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Mint50,
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.md),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "今日打卡进度",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "$checkedCount/$totalCount",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = "还有 $uncheckedCount 个产品等待记录",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacing.sm)
                                .height(6.dp)
                                .clip(RoundedCornerShape(percent = 50)),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        )
                        // Progress dots
                        Row(
                            modifier = Modifier.padding(top = spacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            repeat(totalCount) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index < checkedCount)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.outlineVariant,
                                        ),
                                )
                            }
                        }
                    }
                }
            }

            // Reminder banner
            if (uncheckedCount > 0) {
                item(key = "reminder") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Rose50,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.md, vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            // Bell icon placeholder
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Rose400.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "\uD83D\uDD14",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            Text(
                                text = "今天还有 $uncheckedCount 个护肤品没记录哦~",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // AM Section header
            if (amProducts.isNotEmpty()) {
                val amChecked = amProducts.count { it.checked }
                item(key = "am_header") {
                    RoutineHeader(
                        icon = "\u2600\uFE0F",
                        iconBg = Apricot300.copy(alpha = 0.12f),
                        label = "AM 早间护肤",
                        count = "$amChecked/${amProducts.size} 已打卡",
                    )
                }

                itemsIndexed(amProducts, key = { _, it -> "am_${it.name}" }) { index, product ->
                    ProductCard(
                        product = product,
                        modifier = Modifier.animateListItem(index),
                    )
                }
            }

            // PM Section header
            if (pmProducts.isNotEmpty()) {
                val pmChecked = pmProducts.count { it.checked }
                item(key = "pm_header") {
                    RoutineHeader(
                        icon = "\uD83C\uDF19",
                        iconBg = Lavender300.copy(alpha = 0.12f),
                        label = "PM 晚间护肤",
                        count = "$pmChecked/${pmProducts.size} 已打卡",
                        modifier = Modifier.padding(top = spacing.xs),
                    )
                }

                itemsIndexed(pmProducts, key = { _, it -> "pm_${it.name}" }) { index, product ->
                    ProductCard(
                        product = product,
                        modifier = Modifier.animateListItem(index),
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutineHeader(
    icon: String,
    iconBg: Color,
    label: String,
    count: String,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, style = MaterialTheme.typography.labelSmall)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = count,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProductCard(
    product: MockProduct,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (product.checked) {
                Mint50
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Category icon with colored background
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(product.categoryBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = product.icon,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            // Product info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                product.brand?.let { brand ->
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // Category pill
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = product.categoryFg,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(product.categoryBg)
                        .padding(horizontal = spacing.sm, vertical = 2.dp),
                )
            }

            // Check indicator
            if (product.checked) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "已打卡",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                )
            }
        }
    }
}
