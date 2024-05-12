package com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DestinationsLinksTypeConverter {
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