package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents

import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.itemsScreen.NavigationDestination


//Object to hold the destination of the WhOutDetailsScreen
object WhOutDetailsDestination : NavigationDestination {
    override val route = "whout_details"
    override val titleRes = R.string.whout_details_edit
    const val WHOUT_ID_ARG = "whOutId"
    const val WHOUT_DESCRIPTION_ARG = "whOutDescription"
    val routeWithArgs = "$route/{$WHOUT_ID_ARG}?whOutDescription={$WHOUT_DESCRIPTION_ARG}"
}