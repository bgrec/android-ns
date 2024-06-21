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
@Entity(tableName = "warehouse_outbound_operations")
@Serializable
data class Order(

    @PrimaryKey(autoGenerate = false) @SerializedName("NUME") val id: Int,
    @SerializedName("CODI") val clientId: Int?,
    @SerializedName("RAGIONESOCIALE") val businessName: String?,
    @SerializedName("VIA") val street: String?,
    @SerializedName("CAP") val postalCode: String?,
    @SerializedName("CITTA") val city: String?,
    @SerializedName("PROV") val province: String?,
    @SerializedName("NAZIONE") val nation: String?,
    @SerializedName("DESTINA") val destinationId: Int?,
    @SerializedName("DESTINAZIONEDESCRI") val destinationName: String?,
    @SerializedName("DESCRI") val description: String?,
    @SerializedName("NUMEROLAV") val sequence: Int?,
    @SerializedName("DATAI") val insertDate: String?,
    @SerializedName("AGENTE") val agent: String?,
    @SerializedName("TRASPO") val transportHandler: String?,
    @SerializedName("COLLI") val parcels: Int?,
    @SerializedName("VETTORENUME") val carrierId: Int?,
    @SerializedName("VETTORE") val carrierName: String?,
    @SerializedName("PESO") val weight: Double?,
    @SerializedName("NUMEROORDI") val port: String?,
    @SerializedName("DATAORDI") val date: String?,
    @SerializedName("NOTE") val notes: String?,
    @SerializedName("DATACONSEGNA") val deliveryDate: String?,
    @SerializedName("TASSATIVA") val deliveryDeadline: Boolean?,
    @SerializedName("CONSEGNA") val deliveryType: Int?,
    @SerializedName("STATOCONSEGNA") val deliveryState: Int?,
    @SerializedName("URGENTE") val urgent: Boolean?,
    @SerializedName("PARZIALE") val partial: Int?,
    @SerializedName("NUMERO") val number: Int?,

    @TypeConverters(OrderMetadataTypeConverter::class) @SerializedName("_metadata") val metadata: Metadata?,

    @TypeConverters(OrderLinksTypeConverter::class) @SerializedName("links") val links: List<Link>?,

    @ColumnInfo(name = "page") var page: Int?,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
) {/*val insertDate2: Date?
        get() = insertDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(it) }*/
}


/**
 * Metadata class of [Order]
 */
@Suppress("KDocMissingDocumentation")
@Serializable
data class Metadata(
    @SerializedName("etag") val etag: String
)

/**
 * Link class of [Order]
 */
@Suppress("KDocMissingDocumentation")
@Serializable
data class Link(
    @SerializedName("rel") val rel: String, @SerializedName("href") val href: String
)

/**
 * Order response list
 */
@Suppress("KDocMissingDocumentation")
@Serializable
data class OrdersResponseList(
    @SerializedName("items") val items: List<Order>
)

/**
 * Order response
 */
@Suppress("KDocMissingDocumentation")
@Serializable
data class OrdersResponse(
    val items: List<Order>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)
