package com.mastrosql.app.ui.navigation.main.ordersscreen.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


/**
 * Insert new order response data class for JSON serialization
 *
 */

@Serializable
data class Column(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String
)

@Serializable
data class ResponseItem(
    @SerializedName("type")
    val type: String,
    @SerializedName("items")
    val items: List<Order>,
    @SerializedName("_metadata")
    val metadata: MetadataOfResponseItem
)

@Serializable
data class MetadataOfResponseItem(
    @SerializedName("columns")
    val columns: List<Column>
)

@Serializable
data class OrderAddResponse(
    @SerializedName("items")
    val items: List<ResponseItem>
) {
    /**
     * Retrieves the first order from the response, or returns null if no orders are present.
     */
    fun getAddedOrder(): Order? {
        return items.firstOrNull()?.items?.firstOrNull()
    }
}


