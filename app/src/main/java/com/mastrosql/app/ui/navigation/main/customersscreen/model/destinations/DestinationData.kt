package com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * For GSON Serialization  use @SerializedName(value = "DESCRI")
 * For JSON Kotlin Serialization use @SerialName( value = "DESCRI")
 */

@Entity(tableName = "customers_destinations")
@Serializable
data class DestinationData(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("progTutto") val id: Int,
    @SerializedName("CODI") val customerId: Int,
    @SerializedName("DESCRI") val destinationName: String?,
    @SerializedName("VIA") val street: String?,
    @SerializedName("CAP") val postalCode: String?,
    @SerializedName("CITTA") val city: String?,
    @SerializedName("PROV") val province: String?,

    @TypeConverters(DestinationsLinksTypeConverter::class)
    @SerializedName("links") val links: List<Link>,

    @TypeConverters(DestinationsMetadataTypeConverter::class)
    @SerializedName("_metadata") val metadata: Metadata,

    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()

) {
    fun trimAllStrings(): DestinationData {
        return DestinationData(
            id = id,
            customerId = customerId,
            destinationName = (destinationName?.trim() ?: ""),
            street = (street?.trim() ?: ""),
            postalCode = (postalCode?.trim() ?: ""),
            city = (city?.trim() ?: ""),
            province = (province?.trim() ?: ""),
            links = links,
            metadata = metadata,
            page = page,
            lastUpdated = lastUpdated
        )
    }
}

@Serializable
data class Link(
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

@Serializable
data class Metadata(
    @SerializedName("etag") val etag: String
)

@Serializable
data class DestinationsDataResponse(
    val items: List<DestinationData>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)
