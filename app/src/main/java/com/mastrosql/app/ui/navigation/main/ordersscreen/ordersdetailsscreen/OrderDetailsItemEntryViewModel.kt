package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mastrosql.app.data.orderdetails.OrderDetailsRepository
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Metadata
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import java.text.NumberFormat

/**
 * ViewModel to validate and insert order details items in the Room database.
 */
class OrderDetailsItemEntryViewModel(private val orderDetailsRepository: OrderDetailsRepository) :
    ViewModel() {

    /**
     * Holds current item ui state
     */
    var orderDetailsUiState by mutableStateOf(OrderDetailsEUiState())
        private set

    /**
     * Updates the [orderDetailsUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(orderDetails: OrderDetails) {
        orderDetailsUiState =
            OrderDetailsEUiState(
                orderDetails = orderDetails,
                isEntryValid = validateInput(orderDetails)
            )
    }

    /**
     * Inserts an [OrderDetailsItem] in the Room database
     */
    suspend fun saveItem() {
        if (validateInput()) {
            orderDetailsRepository.insertOrderDetails(orderDetailsUiState.orderDetails.toOrderDetails())
        }
    }

    private fun validateInput(uiState: OrderDetails = orderDetailsUiState.orderDetails): Boolean {
        return with(uiState) {
            description.isNotBlank() //&& price.isNotBlank() //&& quantity.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class OrderDetailsEUiState(
    val orderDetails: OrderDetails = OrderDetails(),
    val isEntryValid: Boolean = false
)

data class OrderDetails(
    val id: Int = 0,
    val orderId: Int = 0,
    val orderRow: Int = 0,
    val confirmed: Boolean = false,
    val articleId: Int = 0,
    val sku: String = "",
    val vendorSku: String = "",
    val description: String = "",
    val completeDescription: String = "",
    val quantity: Double = 0.0,
    val tmpQuantity: Double = 0.0,
    val returnedQuantity: Double = 0.0,
    val weight: Double = 0.0,
    val cost: Double = 0.0,
    val price: Double = 0.0,
    val vat: String = "",
    val vatValue: Double = 0.0,
    val discount: Double = 0.0,
    val discount1: Double = 0.0,
    val discount2: Double = 0.0,
    val discount3: Double = 0.0,
    val catalogPrice: Double = 0.0,
    val measureUnit: String = "",
    val rowType: Int = 0,
    val packSize: String = "",
    val orderedQuantity: Double = 0.0,
    val shippedQuantity: Double = 0.0,
    val batch: String = "",
    val expirationDate: String = "",

    // @TypeConverters(MetadataTypeConverter::class)
    val metadata: Metadata = Metadata("")
)

/**
 * Extension function to convert [OrderDetailsItemUiState] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun OrderDetails.toOrderDetailsItem(): OrderDetailsItem = OrderDetailsItem(
    id = id,
    orderId = orderId,
    orderRow = orderRow,
    confirmed = confirmed,
    articleId = articleId,
    sku = sku,
    vendorSku = vendorSku,
    description = description,
    completeDescription = completeDescription,
    quantity = quantity,
    tmpQuantity = tmpQuantity,
    returnedQuantity = returnedQuantity,
    weight = weight,
    cost = cost.toString(),
    price = price.toString(),
    vat = vat,
    vatValue = vatValue,
    discount = discount,
    discount1 = discount1,
    discount2 = discount2,
    discount3 = discount3,
    catalogPrice = catalogPrice,
    measureUnit = measureUnit,
    rowType = rowType,
    packSize = packSize,
    orderedQuantity = orderedQuantity,
    shippedQuantity = shippedQuantity,
    batch = batch,
    expirationDate = expirationDate,
metadata = null,
    links = emptyList(),
    page = 0,
)

fun OrderDetails.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [OrderDetailsItem] to [OrderDetailsItemItemUiState]
 */
fun OrderDetails.toOrderDetailsUiState(isEntryValid: Boolean = false): OrderDetailsEUiState =
    OrderDetailsEUiState(
        orderDetails = this.toOrderDetails(),
        isEntryValid = isEntryValid
    )

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun OrderDetails.toOrderDetails(): OrderDetails = OrderDetails(
    id = id,
    sku = sku,
    vendorSku = vendorSku,
    description = description,

    cost = cost,
    vat = vat,
    measureUnit = measureUnit,

    price = price,

    metadata = metadata
)
