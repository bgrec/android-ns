package com.mastrosql.app.ui.navigation.main.itemsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mastrosql.app.data.item.ItemsRepository
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Metadata
import java.text.NumberFormat

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    /**
     * Inserts an [Item] in the Room database
     */
    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            description.isNotBlank() && price.isNotBlank() //&& quantity.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val sku: String = "",
    val vendorSku: String = "",
    val description: String = "",
    val cost: String = "",
    val vat: String = "",
    val measureUnit: String = "",
    val department: String = "",
    val subDepartment: String = "",
    val family: String = "",
    val subFamily: String = "",
    val price: String = "",
    val group: String = "",
    val ean8: String = "",
    val ean13: String? = "",
    val eanAlt1: String? = "",
    val eanAlt2: String? = "",
    val eanAlt3: String? = "",
    val eanAlt4: String? = "",
    val eanAlt5: String? = "",
    val eanAlt6: String? = "",
    val eanAlt7: String? = "",
    val eanAlt8: String? = "",
    val eanAlt9: String? = "",
    val eanAlt10: String? = "",
    //quantity = quantity.toIntOrNull() ?: 0

    // @TypeConverters(MetadataTypeConverter::class)
    val metadata: Metadata = Metadata("")
)

/**
 * Extension function to convert [ItemUiState] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    sku = sku,
    vendorSku = vendorSku,
    description = description,
    cost = cost.toDoubleOrNull() ?: 0.0,
    vat = vat,
    measureUnit = measureUnit,
    department = department,
    subDepartment = subDepartment,
    family = family,
    subFamily = subFamily,
    price = price.toDoubleOrNull() ?: 0.0,
    group = group,
    ean8 = ean8,
    ean13 = ean13,
    eanAlt1 = eanAlt1,
    eanAlt2 = eanAlt2,
    eanAlt3 = eanAlt3,
    eanAlt4 = eanAlt4,
    eanAlt5 = eanAlt5,
    eanAlt6 = eanAlt6,
    eanAlt7 = eanAlt7,
    eanAlt8 = eanAlt8,
    eanAlt9 = eanAlt9,
    eanAlt10 = eanAlt10,
    metadata = metadata
)

fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    sku = sku,
    vendorSku = vendorSku,
    description = description,
    cost = cost.toString(),
    vat = vat,
    measureUnit = measureUnit,
    department = department,
    subDepartment = subDepartment,
    family = family,
    subFamily = subFamily,
    price = price.toString(),
    group = group,
    ean8 = ean8,
    ean13 = ean13,
    eanAlt1 = eanAlt1,
    eanAlt2 = eanAlt2,
    eanAlt3 = eanAlt3,
    eanAlt4 = eanAlt4,
    eanAlt5 = eanAlt5,
    eanAlt6 = eanAlt6,
    eanAlt7 = eanAlt7,
    eanAlt8 = eanAlt8,
    eanAlt9 = eanAlt9,
    eanAlt10 = eanAlt10,
    metadata = metadata
)
