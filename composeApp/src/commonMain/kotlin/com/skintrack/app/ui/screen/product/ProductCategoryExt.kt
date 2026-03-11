package com.skintrack.app.ui.screen.product

import com.skintrack.app.domain.model.ProductCategory

val ProductCategory.displayName: String
    get() = when (this) {
        ProductCategory.CLEANSER -> "洁面"
        ProductCategory.TONER -> "化妆水"
        ProductCategory.SERUM -> "精华"
        ProductCategory.EMULSION -> "乳液"
        ProductCategory.CREAM -> "面霜"
        ProductCategory.SUNSCREEN -> "防晒"
        ProductCategory.MASK -> "面膜"
        ProductCategory.EYE_CREAM -> "眼霜"
        ProductCategory.EXFOLIATOR -> "去角质"
        ProductCategory.OTHER -> "其他"
    }
