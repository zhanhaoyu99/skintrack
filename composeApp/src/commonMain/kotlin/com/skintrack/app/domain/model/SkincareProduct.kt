package com.skintrack.app.domain.model

data class SkincareProduct(
    val id: String,
    val userId: String = "",
    val name: String,
    val brand: String? = null,
    val category: ProductCategory,
    val usagePeriod: UsagePeriod = UsagePeriod.BOTH,
    val imageUrl: String? = null,
    val barcode: String? = null,
    val synced: Boolean = false,
)

enum class ProductCategory {
    CLEANSER, TONER, SERUM, EMULSION, CREAM, SUNSCREEN, MASK, EYE_CREAM, EXFOLIATOR, OTHER
}

enum class UsagePeriod {
    AM, PM, BOTH;

    val displayName: String
        get() = when (this) {
            AM -> "早间"
            PM -> "晚间"
            BOTH -> "早晚"
        }
}
