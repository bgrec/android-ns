package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.ordersdetailsscreen.model

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Type converter for [Metadata] class.
 */
class OrderDetailsMetadataTypeConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromMetadata(metadata: Metadata): String {
        return json.encodeToString(metadata)
    }

    @TypeConverter
    fun toMetadata(metadataString: String): Metadata {
        return json.decodeFromString(metadataString)
    }
}
