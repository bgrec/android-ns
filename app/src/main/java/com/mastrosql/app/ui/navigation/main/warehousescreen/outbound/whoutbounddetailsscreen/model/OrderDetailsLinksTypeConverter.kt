package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Type converter for links list
 */
class OrderDetailLinksTypeConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromLinks(links: List<Link>): String {
        return json.encodeToString(links)
    }

    @TypeConverter
    fun toLinks(linksJson: String): List<Link> {
        return json.decodeFromString(linksJson)
    }
}