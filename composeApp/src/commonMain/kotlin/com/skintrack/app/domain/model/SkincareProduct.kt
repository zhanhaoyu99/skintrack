package com.skintrack.app.domain.model

data class SkincareProduct(
    val id: String,
    val name: String,
    val brand: String? = null,
    val category: ProductCategory,
    val imageUrl: String? = null,
    val barcode: String? = null,
    val synced: Boolean = false,
)

enum class ProductCategory {
    CLEANSER, TONER, SERUM, EMULSION, CREAM, SUNSCREEN, MASK, EYE_CREAM, EXFOLIATOR, OTHER
}
