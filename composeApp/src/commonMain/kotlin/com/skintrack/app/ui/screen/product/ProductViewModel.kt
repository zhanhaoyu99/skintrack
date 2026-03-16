package com.skintrack.app.ui.screen.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.DailyProductUsage
import com.skintrack.app.domain.model.ProductCategory
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.model.UsagePeriod
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.ui.component.SnackbarMessage
import com.skintrack.app.ui.component.SnackbarType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private var userId = "local-user"

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<SnackbarMessage>(extraBufferCapacity = 1)
    val snackbarEvent: SharedFlow<SnackbarMessage> = _snackbarEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: StateFlow<ProductCategory?> = _selectedCategory.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: ProductCategory?) {
        _selectedCategory.value = category
    }

    init {
        viewModelScope.launch {
            userId = authRepository.currentUser()?.userId ?: "local-user"
            combine(
                productRepository.getAllProducts(),
                productRepository.getUsageByDate(userId, today),
                _searchQuery,
                _selectedCategory,
            ) { products, usages, query, category ->
                if (products.isEmpty()) ProductUiState.Empty
                else {
                    val filtered = products.filter { product ->
                        val matchesQuery = query.isBlank() ||
                            product.name.contains(query, ignoreCase = true) ||
                            (product.brand?.contains(query, ignoreCase = true) == true)
                        val matchesCategory = category == null || product.category == category
                        matchesQuery && matchesCategory
                    }
                    ProductUiState.Content(
                        products = filtered,
                        todayUsedProductIds = usages.map { it.productId }.toSet(),
                    )
                }
            }.collect { _uiState.value = it }
        }
    }

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
    fun saveProduct(name: String, brand: String?, category: ProductCategory, usagePeriod: UsagePeriod = UsagePeriod.BOTH) {
        viewModelScope.launch {
            val product = SkincareProduct(
                id = Uuid.random().toString(),
                userId = userId,
                name = name,
                brand = brand?.takeIf { it.isNotBlank() },
                category = category,
                usagePeriod = usagePeriod,
            )
            productRepository.saveProduct(product)
            _showAddSheet.value = false
            _snackbarEvent.tryEmit(
                SnackbarMessage("产品已添加到护肤方案", SnackbarType.SUCCESS)
            )
        }
    }

    fun deleteProduct(product: SkincareProduct) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
            _snackbarEvent.tryEmit(
                SnackbarMessage("产品已删除", SnackbarType.SUCCESS)
            )
        }
    }
}

sealed interface ProductUiState {
    data object Loading : ProductUiState
    data object Empty : ProductUiState
    data class Content(
        val products: List<SkincareProduct>,
        val todayUsedProductIds: Set<String>,
    ) : ProductUiState {
        val amProducts: List<SkincareProduct>
            get() = products.filter { it.usagePeriod == UsagePeriod.AM || it.usagePeriod == UsagePeriod.BOTH }
        val pmProducts: List<SkincareProduct>
            get() = products.filter { it.usagePeriod == UsagePeriod.PM || it.usagePeriod == UsagePeriod.BOTH }
        val checkedCount: Int
            get() = products.count { it.id in todayUsedProductIds }
        val totalCount: Int
            get() = products.size
        val uncheckedCount: Int
            get() = totalCount - checkedCount
    }
}
