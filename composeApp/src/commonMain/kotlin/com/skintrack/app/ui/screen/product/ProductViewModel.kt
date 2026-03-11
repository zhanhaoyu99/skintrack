package com.skintrack.app.ui.screen.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.DailyProductUsage
import com.skintrack.app.domain.model.ProductCategory
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProductViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val userId = "local-user"

    val uiState: StateFlow<ProductUiState> = combine(
        productRepository.getAllProducts(),
        productRepository.getUsageByDate(userId, today),
    ) { products, usages ->
        if (products.isEmpty()) ProductUiState.Empty
        else ProductUiState.Content(
            products = products,
            todayUsedProductIds = usages.map { it.productId }.toSet(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProductUiState.Loading)

    private val _showAddSheet = MutableStateFlow(false)
    val showAddSheet: StateFlow<Boolean> = _showAddSheet.asStateFlow()

    fun showAddSheet() {
        _showAddSheet.value = true
    }

    fun hideAddSheet() {
        _showAddSheet.value = false
    }

    @OptIn(ExperimentalUuidApi::class)
    fun toggleUsage(product: SkincareProduct) {
        val state = uiState.value
        if (state !is ProductUiState.Content) return

        viewModelScope.launch {
            if (product.id in state.todayUsedProductIds) {
                productRepository.removeUsage(userId, product.id, today)
            } else {
                productRepository.logUsage(
                    DailyProductUsage(
                        id = Uuid.random().toString(),
                        userId = userId,
                        productId = product.id,
                        usedDate = today,
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun saveProduct(name: String, brand: String?, category: ProductCategory) {
        viewModelScope.launch {
            val product = SkincareProduct(
                id = Uuid.random().toString(),
                name = name,
                brand = brand?.takeIf { it.isNotBlank() },
                category = category,
            )
            productRepository.saveProduct(product)
            _showAddSheet.value = false
        }
    }

    fun deleteProduct(product: SkincareProduct) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }
}

sealed interface ProductUiState {
    data object Loading : ProductUiState
    data object Empty : ProductUiState
    data class Content(
        val products: List<SkincareProduct>,
        val todayUsedProductIds: Set<String>,
    ) : ProductUiState
}
