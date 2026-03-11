package com.skintrack.app.ui.screen.product

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class ProductManageScreen : Screen {

    @Composable
    override fun Content() {
        ProductScreen(showBackButton = true)
    }
}
