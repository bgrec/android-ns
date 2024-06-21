package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


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
    val items: List<WarehouseOutbound>,
    @SerializedName("_metadata")
    val metadata: MetadataOfResponseItem
)

@Serializable
data class MetadataOfResponseItem(
    @SerializedName("columns")
    val columns: List<Column>
)

@Serializable
data class WhOutboundAddResponse(
    @SerializedName("items")
    val items: List<ResponseItem>
) {

    fun getAddedWhOutbound(): WarehouseOutbound? {
        return items.firstOrNull()?.items?.firstOrNull()
    }
}
