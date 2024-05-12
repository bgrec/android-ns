package com.mastrosql.app.ui.navigation.main.customersscreen.model

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

@Entity(tableName = "customers")
@Serializable
data class CustomerMasterData(
    @PrimaryKey(autoGenerate = true)
    @SerializedName(value = "CODI") val id: Int,
    @SerializedName("DESCRI") val businessName: String?,
    @SerializedName("VIA") val street: String?,
    @SerializedName("CAP") val postalCode: String?,
    @SerializedName("PIVA") val vat: String?,
    @SerializedName("CITTA") val city: String?,
    @SerializedName("PROV") val province: String?,
    @SerializedName("NAZIONE") val nation: String?,
    @SerializedName("DESC2") val businessName2: String?,
    @SerializedName("CODFIS") val taxId: String?,

    @TypeConverters(CustomerLinksTypeConverter::class)
    @SerializedName("links") val links: List<Link>,

    @TypeConverters(CustomerMetadataTypeConverter::class)
    @SerializedName("_metadata") val metadata: Metadata,

    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()

) {
    fun trimAllStrings(): CustomerMasterData {
        return CustomerMasterData(
            id = id,
            businessName = (businessName?.trim() ?: ""),
            street = (street?.trim() ?: ""),
            postalCode = (postalCode?.trim() ?: ""),
            vat = (vat?.trim() ?: ""),
            city = (city?.trim() ?: ""),
            province = (province?.trim() ?: ""),
            nation = (nation?.trim() ?: ""),
            businessName2 = (businessName2?.trim() ?: ""),
            taxId = (taxId?.trim() ?: ""),
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
data class CustomersMasterDataResponse(
    val items: List<CustomerMasterData>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)
