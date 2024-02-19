package com.mastrosql.app.ui.components.appdrawer

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class AppDrawerItemInfo<T>(
    val drawerOption: T,
    @StringRes val title: Int,
    val icon: ImageVector,
    @StringRes val descriptionId: Int
)