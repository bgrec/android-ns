package com.mastrosql.app.ui.components.bottombar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BottomBarAction(

    @DrawableRes val icon: Int,
    @StringRes val description: Int,
    val onClick: () -> Unit
)

