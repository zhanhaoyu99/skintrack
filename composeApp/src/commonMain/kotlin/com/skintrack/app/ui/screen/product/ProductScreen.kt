package com.skintrack.app.ui.screen.product

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.ProductCategory
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.model.displayName
import com.skintrack.app.ui.component.AppSnackbarHost
import com.skintrack.app.ui.component.EmptyContent
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.component.showTyped
import com.skintrack.app.ui.theme.Secondary300
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Primary400
import com.skintrack.app.ui.theme.Primary50
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose500
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    showBackButton: Boolean = false,
    viewModel: ProductViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val showAddSheet by viewModel.showAddSheet.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val navigator = LocalNavigator.currentOrThrow
    val spacing = MaterialTheme.spacing

    var productToDelete by remember { mutableStateOf<SkincareProduct?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { event ->
            snackbarHostState.showTyped(event.message, event.type)
        }
    }

    val categoryOptions = remember {
        listOf(
            null to "全部",
            ProductCategory.CLEANSER to "洁面",
            ProductCategory.SERUM to "精华",
            ProductCategory.CREAM to "面霜",
            ProductCategory.SUNSCREEN to "防晒",
            ProductCategory.TONER to "化妆水",
            ProductCategory.MASK to "面膜",
            ProductCategory.OTHER to "其他",
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("护肤品管理") },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            },
            actions = {
                IconButton(onClick = { viewModel.showAddSheet() }) {
                    Icon(Icons.Default.Add, contentDescription = "添加产品")
                }
            },
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::setSearchQuery,
            placeholder = { Text("搜索产品名称或品牌") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "清除")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(percent = 50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md),
        )

        // Category filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.iconGap),
            modifier = Modifier.padding(vertical = spacing.sm),
        ) {
            items(categoryOptions) { (category, label) ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.setCategory(category) },
                    label = { Text(label) },
                )
            }
        }

        when (val state = uiState) {
            ProductUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingContent()
                }
            }

            ProductUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        EmptyContent(message = "还没有护肤品，点击右上角添加")
                        TextButton(onClick = { viewModel.showAddSheet() }) {
                            Text("添加护肤品")
                        }
                    }
                }
            }

            is ProductUiState.Content -> {
                ProductContent(
                    state = state,
                    onToggleUsage = viewModel::toggleUsage,
                    onLongClickProduct = { productToDelete = it },
                )
            }
        }
    }

    AppSnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.TopCenter),
    )
    } // Box

    if (showAddSheet) {
        AddProductSheet(
            onDismiss = { viewModel.hideAddSheet() },
            onSave = viewModel::saveProduct,
        )
    }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("删除产品") },
            text = { Text("确定删除「${product.name}」吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(product)
                    productToDelete = null
                }) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("取消")
                }
            },
        )
    }
}

@Composable
private fun ProductContent(
    state: ProductUiState.Content,
    onToggleUsage: (SkincareProduct) -> Unit,
    onLongClickProduct: (SkincareProduct) -> Unit,
) {
    val spacing = MaterialTheme.spacing
    val progress = if (state.totalCount > 0) {
        state.checkedCount.toFloat() / state.totalCount
    } else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = spacing.md,
            vertical = spacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        // -- Progress Section --
        item(key = "progress") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Primary400.copy(alpha = 0.08f),
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
                                    0f to Primary50,
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
                        checked = state.checkedCount,
                        total = state.totalCount,
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
                        if (state.uncheckedCount > 0) {
                            Text(
                                text = "还有 ${state.uncheckedCount} 个产品等待记录",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
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
                            repeat(state.totalCount.coerceAtMost(10)) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .then(
                                            if (index < state.checkedCount) {
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

        // -- Unchecked reminder --
        if (state.uncheckedCount > 0) {
            item(key = "reminder") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Rose400.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium,
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Rose50, Color(0xFFFFF8F5)),
                                ),
                            )
                            .padding(horizontal = spacing.listGap, vertical = spacing.compact),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
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
                                    append("${state.uncheckedCount} 个")
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

        // -- AM Section --
        if (state.amProducts.isNotEmpty()) {
            val amChecked = state.amProducts.count { it.id in state.todayUsedProductIds }
            item(key = "am_header") {
                RoutineSectionHeader(
                    icon = "\u2600\uFE0F",
                    iconBg = Secondary300.copy(alpha = 0.12f),
                    label = "AM 早间护肤",
                    count = "$amChecked/${state.amProducts.size} 已打卡",
                )
            }

            itemsIndexed(state.amProducts, key = { _, it -> "am_${it.id}" }) { index, product ->
                ProductCard(
                    product = product,
                    checked = product.id in state.todayUsedProductIds,
                    onToggle = { onToggleUsage(product) },
                    onLongClick = { onLongClickProduct(product) },
                    modifier = Modifier.animateListItem(index),
                )
            }
        }

        // -- PM Section --
        if (state.pmProducts.isNotEmpty()) {
            val pmChecked = state.pmProducts.count { it.id in state.todayUsedProductIds }
            item(key = "pm_header") {
                RoutineSectionHeader(
                    icon = "\uD83C\uDF19",
                    iconBg = Lavender300.copy(alpha = 0.12f),
                    label = "PM 晚间护肤",
                    count = "$pmChecked/${state.pmProducts.size} 已打卡",
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }

            itemsIndexed(state.pmProducts, key = { _, it -> "pm_${it.id}" }) { index, product ->
                ProductCard(
                    product = product,
                    checked = product.id in state.todayUsedProductIds,
                    onToggle = { onToggleUsage(product) },
                    onLongClick = { onLongClickProduct(product) },
                    modifier = Modifier.animateListItem(index),
                )
            }
        }
    }
}

@Composable
private fun RoutineSectionHeader(
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
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.1).sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = count,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.2.sp,
        )
    }
}

/**
 * Returns category-specific color pair (foreground, background).
 */
private fun categoryColors(category: ProductCategory): Pair<Color, Color> = when (category) {
    ProductCategory.CLEANSER -> Pair(Color(0xFF2563EB), Color(0xFFDBEAFE))
    ProductCategory.SERUM -> Pair(Color(0xFF7B1FA2), Color(0xFFF3E5F5))
    ProductCategory.CREAM -> Pair(Color(0xFF2E7D32), Color(0xFFE8F5E9))
    ProductCategory.SUNSCREEN -> Pair(Color(0xFFE65100), Color(0xFFFFF3E0))
    ProductCategory.TONER -> Pair(Color(0xFF00838F), Color(0xFFE0F7FA))
    ProductCategory.MASK -> Pair(Color(0xFFF57F17), Color(0xFFFFF8E1))
    ProductCategory.EYE_CREAM -> Pair(Color(0xFF8B5CF6), Color(0xFFF3E8FF))
    else -> Pair(Color(0xFF6B7B73), Color(0xFFF1F5F2))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductCard(
    product: SkincareProduct,
    checked: Boolean,
    onToggle: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val (categoryFg, categoryBg) = remember(product.category) { categoryColors(product.category) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (checked) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = Primary400.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.large,
                    )
                } else {
                    Modifier.border(
                        width = 0.5.dp,
                        color = Color.Black.copy(alpha = 0.03f),
                        shape = MaterialTheme.shapes.large,
                    )
                },
            )
            .combinedClickable(
                onClick = onToggle,
                onLongClick = onLongClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (checked) {
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
                    if (checked) {
                        Modifier.background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFF0FAF6), Color(0xFFF5FFFA)),
                            ),
                        )
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = spacing.md, vertical = spacing.listGap),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.listGap),
        ) {
            // Category icon with colored background
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(categoryBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = categoryIcon(product.category),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            // Product info
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = 0.05.sp,
                )
                product.brand?.let { brand ->
                    Text(
                        text = brand,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500,
                        letterSpacing = 0.2.sp,
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
                        text = product.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = categoryFg,
                        fontSize = 9.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(categoryBg)
                            .padding(horizontal = spacing.sm, vertical = 1.dp),
                    )
                    // Frequency pill
                    Text(
                        text = "\u23F0 ${product.usagePeriod.displayName}",
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

            // Check-in indicator
            if (checked) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            ambientColor = Primary400.copy(alpha = 0.3f),
                            spotColor = Primary400.copy(alpha = 0.3f),
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
                        .size(28.dp)
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

private fun categoryIcon(category: ProductCategory): String = when (category) {
    ProductCategory.CLEANSER -> "\uD83E\uDDF4" // soap
    ProductCategory.TONER -> "\uD83D\uDCA7" // droplet
    ProductCategory.SERUM -> "\u2728" // sparkles
    ProductCategory.EMULSION -> "\uD83E\uDDF4"
    ProductCategory.CREAM -> "\uD83E\uDED9" // jar
    ProductCategory.SUNSCREEN -> "\u2600\uFE0F" // sun
    ProductCategory.MASK -> "\uD83C\uDFAD" // mask
    ProductCategory.EYE_CREAM -> "\uD83D\uDC41\uFE0F" // eye
    ProductCategory.EXFOLIATOR -> "\uD83E\uDDF9" // broom
    ProductCategory.OTHER -> "\uD83E\uDDF4"
}
