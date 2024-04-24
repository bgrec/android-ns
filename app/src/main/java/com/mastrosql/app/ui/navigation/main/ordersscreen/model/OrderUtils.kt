package com.mastrosql.app.ui.navigation.main.ordersscreen.model

import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrderState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OrderUtils {

    fun createNewOrderFromState(orderState: OrderState): Order {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date())

        return Order(
            id = 0, // Set to 0 as it will be auto-generated
            clientId = orderState.customerId.intValue,
            businessName = orderState.customerName.value.text,
            street = null,
            postalCode = null,
            city = null,
            province = null,
            nation = null,
            destinationId = orderState.destinationId.intValue,
            destinationName = orderState.destinationName.value.text,
            description = orderState.orderDescription.value.text,
            sequence = 0,
            insertDate = currentDate,
            agent = null,
            transportHandler = null,
            parcels = null,
            carrierId = null,
            carrierName = null,
            weight = null,
            port = null,
            date = currentDate,
            notes = null,
            deliveryDate = orderState.deliveryDate.value.text,
            deliveryDeadline = null,
            deliveryType = null,
            deliveryState = 0,
            urgent = null,
            partial = null,
            number = null,
            metadata = Metadata(etag = ""),
            links = listOf(),
            page = 0
        )
    }
}