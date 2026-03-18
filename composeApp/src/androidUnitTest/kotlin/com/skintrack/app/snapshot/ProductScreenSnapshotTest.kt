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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.Apricot300
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Mint400
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.Rose500
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
    val frequency: String = "每天",
)

private val sampleProducts = listOf(
    MockProduct("CeraVe 洁面乳", "CeraVe \u00B7 适乐肤", "洁面", Color(0xFF2563EB), Color(0xFFDBEAFE), "\uD83E\uDDF4", true, "AM"),
    MockProduct("薇诺娜 特护精华液", "薇诺娜 \u00B7 Winona", "精华", Lavender300, Lavender50, "\u2728", true, "AM"),
    MockProduct("安耐晒 金瓶防晒", "资生堂 \u00B7 Anessa", "防晒", Apricot300, Apricot50, "\u2600\uFE0F", false, "AM"),
    MockProduct("珂润 保湿面霜", "花王 \u00B7 Curel", "面霜", Color(0xFF2E7D32), Color(0xFFE8F5E9), "\uD83E\uDED9", false, "PM"),
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
            // Check-in progress card with gradient background
            item(key = "progress") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Mint400.copy(alpha = 0.08f),
                            shape = MaterialTheme.shapes.extraLarge,
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colorStops = arrayOf(
                                        0f to Mint50,
                                        0.5f to Color(0xFFF0FAF6),
                                        1f to Lavender50,
                                    ),
                                ),
                            )
                            .padding(spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        // Progress ring
                        ProgressRing(
                            checked = checkedCount,
                            total = totalCount,
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = "今日打卡进度",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.2).sp,
                            )
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
                                trackColor = Color.Black.copy(alpha = 0.06f),
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
                                            .then(
                                                if (index < checkedCount) {
                                                    Modifier.background(MaterialTheme.colorScheme.primary)
                                                } else {
                                                    Modifier.border(
                                                        2.dp,
                                                        MaterialTheme.colorScheme.outlineVariant,
                                                        CircleShape,
                                                    )
                                                },
                                            ),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reminder banner with gradient
            if (uncheckedCount > 0) {
                item(key = "reminder") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Rose400.copy(alpha = 0.12f),
                                shape = MaterialTheme.shapes.large,
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent,
                        ),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Rose50, Color(0xFFFFF8F5)),
                                    ),
                                )
                                .padding(horizontal = 14.dp, vertical = spacing.sm),
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
                                text = buildAnnotatedString {
                                    append("今天还有 ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Rose500)) {
                                        append("${uncheckedCount} 个")
                                    }
                                    append(" 护肤品没记录哦~")
                                },
                                style = MaterialTheme.typography.bodyMedium,
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
                        modifier = Modifier.padding(top = 14.dp),
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
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = count,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
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
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (product.checked) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = Mint400.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.large,
                    )
                } else {
                    Modifier.border(
                        width = 0.5.dp,
                        color = Color.Black.copy(alpha = 0.03f),
                        shape = MaterialTheme.shapes.large,
                    )
                },
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (product.checked) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (product.checked) {
                        Modifier.background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFF0FAF6), Color(0xFFF5FFFA)),
                            ),
                        )
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = 14.dp, vertical = spacing.sm),
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
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = (-0.1).sp,
                )
                product.brand?.let { brand ->
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
                // Category pill + frequency pill
                Row(
                    modifier = Modifier.padding(top = spacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = product.categoryFg,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(product.categoryBg)
                            .padding(horizontal = spacing.sm, vertical = 2.dp),
                    )
                    Text(
                        text = "\u23F0 ${product.frequency}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    )
                }
            }

            // Check indicator
            if (product.checked) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            ambientColor = Mint400.copy(alpha = 0.3f),
                            spotColor = Mint400.copy(alpha = 0.3f),
                        )
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

@Composable
private fun ProgressRing(
    checked: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val progress = if (total > 0) checked.toFloat() / total else 0f
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = Color.Black.copy(alpha = 0.06f)

    Box(
        modifier = modifier.size(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            val strokeWidth = 4.dp.toPx()
            val arcSize = size.width - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            // Progress
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$checked",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp,
            )
            Text(
                text = "/$total",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 10.sp,
            )
        }
    }
}
