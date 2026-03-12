package com.skintrack.app.ui.screen.product

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.model.displayName
import com.skintrack.app.ui.component.EmptyContent
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.component.LoadingContent
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
    val navigator = LocalNavigator.currentOrThrow

    var productToDelete by remember { mutableStateOf<SkincareProduct?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("我的护肤") },
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
                    products = state.products,
                    todayUsedProductIds = state.todayUsedProductIds,
                    onToggleUsage = viewModel::toggleUsage,
                    onLongClickProduct = { productToDelete = it },
                )
            }
        }
    }

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
    products: List<SkincareProduct>,
    todayUsedProductIds: Set<String>,
    onToggleUsage: (SkincareProduct) -> Unit,
    onLongClickProduct: (SkincareProduct) -> Unit,
) {
    val spacing = MaterialTheme.spacing
    val checkedCount = products.count { it.id in todayUsedProductIds }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = spacing.md,
            vertical = spacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        item(key = "checkin_header") {
            SectionHeader("今日护肤 ($checkedCount/${products.size})")
        }

        itemsIndexed(products, key = { _, it -> "check_${it.id}" }) { index, product ->
            CheckInRow(
                product = product,
                checked = product.id in todayUsedProductIds,
                onToggle = { onToggleUsage(product) },
                modifier = Modifier.animateListItem(index),
            )
        }

        item(key = "product_header") {
            SectionHeader(
                title = "产品库",
                modifier = Modifier.padding(top = spacing.sm),
            )
        }

        itemsIndexed(products, key = { _, it -> "card_${it.id}" }) { index, product ->
            ProductCard(
                product = product,
                onLongClick = { onLongClickProduct(product) },
                modifier = Modifier.animateListItem(index),
            )
        }
    }
}

@Composable
private fun CheckInRow(
    product: SkincareProduct,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onToggle,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (checked) {
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
                    start = MaterialTheme.spacing.xs,
                    end = MaterialTheme.spacing.md,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        ) {
            Checkbox(checked = checked, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (checked) FontWeight.Medium else FontWeight.Normal,
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
                text = product.category.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductCard(
    product: SkincareProduct,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
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
                text = product.category.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
