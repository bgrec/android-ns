package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model

import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents.WhOutboundState

object WhOutboundUtils {

    fun createNewWarehouseOutboundFromState(whOutboundState: WhOutboundState): WarehouseOutbound {
//        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date())
//
//        //val deliveryDate = DateHelper.formatDateToInput(orderState.deliveryDate.value.text)
//        val deliveryDate = orderState.deliveryDate.value.text

        return WarehouseOutbound(
            id = 0, // Set to 0 as it will be auto-generated
            customerId = whOutboundState.customerId.intValue,
            businessName = whOutboundState.customerName.value.text,
            street = null,
            postalCode = null,
            vat = null,
            city = null,
            province = null,
            nation = null,
            businessName2 = null,
            taxId = null,
            metadata = Metadata(etag = ""),
            links = listOf(),
            page = 0
        )
    }
}
