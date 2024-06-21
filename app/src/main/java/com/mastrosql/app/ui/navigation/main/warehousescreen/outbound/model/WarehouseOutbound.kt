package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model

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

@Suppress("KDocMissingDocumentation")
@Entity(tableName = "warehouse_outbound")
@Serializable
data class WarehouseOutbound(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("NUME") val id: Int,
    @SerializedName("CODI") val customerId: Int,
    @SerializedName("DESCRI") val businessName: String?,
    @SerializedName("VIA") val street: String?,
    @SerializedName("CAP") val postalCode: String?,
    @SerializedName("PIVA") val vat: String?,
    @SerializedName("CITTA") val city: String?,
    @SerializedName("PROV") val province: String?,
    @SerializedName("NAZIONE") val nation: String?,
    @SerializedName("DESC2") val businessName2: String?,
    @SerializedName("CODFIS") val taxId: String?,

    @TypeConverters(WhOutboundLinksTypeConverter::class)
    @SerializedName("links") val links: List<Link>,

    @TypeConverters(WhOutboundMetadataTypeConverter::class)
    @SerializedName("_metadata") val metadata: Metadata,

    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class Metadata(
    @SerializedName("etag") val etag: String
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class Link(
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class WhOutboundResponseList(
    @SerializedName("items") val items: List<WarehouseOutbound>
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class WhOutboundResponse(
    val items: List<WarehouseOutbound>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)
