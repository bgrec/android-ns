package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.ui.graphics.Color
import com.mastrosql.app.R

//dialog delivery types
data class DeliveryState(
    val state: Int,
    val nameState: Int,
    val color: Color
)

object DeliveryStates {
    val deliveryStates = listOf(
        DeliveryState(0, R.string.order_deliveryState_value0, Color.Red),
        DeliveryState(1, R.string.order_deliveryState_value1, Color.Green),
        DeliveryState(2, R.string.order_deliveryState_value2, Color.Black),
        DeliveryState(3, R.string.order_deliveryState_value3, Color.Magenta)
    )
}