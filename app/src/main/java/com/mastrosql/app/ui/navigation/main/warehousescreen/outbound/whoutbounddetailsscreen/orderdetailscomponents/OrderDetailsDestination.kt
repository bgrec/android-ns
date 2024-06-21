package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.orderdetailscomponents

import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.itemsScreen.NavigationDestination


//Object to hold the destination of the OrderDetailsScreen
object OrderDetailsDestination : NavigationDestination {
    override val route = "order_details"
    override val titleRes = R.string.order_details_edit
    const val ORDER_ID_ARG = "orderId"
    const val ORDER_DESCRIPTION_ARG = "orderDescription"
    val routeWithArgs = "$route/{$ORDER_ID_ARG}?orderDescription={$ORDER_DESCRIPTION_ARG}"
}