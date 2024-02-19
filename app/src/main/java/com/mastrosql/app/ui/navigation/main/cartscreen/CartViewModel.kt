package com.mastrosql.app.ui.navigation.main.cartscreen

import androidx.lifecycle.ViewModel
import com.mastrosql.app.data.itemTest.ItemTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<ItemTest>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    fun addToCart(cartItemTest: ItemTest) {
        val updatedCartItems = _cartItems.value.toMutableList()
        updatedCartItems.add(cartItemTest)
        _cartItems.value = updatedCartItems
    }
}
