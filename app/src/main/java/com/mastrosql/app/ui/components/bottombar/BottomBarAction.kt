package com.mastrosql.app.ui.components.bottombar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomBarAction(

    @DrawableRes val icon: ImageVector,
    @StringRes val description: Int,
    val onClick: () -> Unit
)

