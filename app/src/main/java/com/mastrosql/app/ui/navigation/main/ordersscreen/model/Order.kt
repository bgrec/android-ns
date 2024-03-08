package com.mastrosql.app.ui.navigation.main.ordersscreen.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * For GSON Serialization  use @SerializedName(value = "DESCRI")
 * For JSON Kotlin Serialization use @SerialName( value = "DESCRI")
 */

@Entity(tableName = "orders")
@Serializable
data class Order(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("NUME") val id: Int,
    @SerializedName("CODI") val clientId: Int,
    @SerializedName("ragSoc") val businessName: String,
    @SerializedName("VIA") val street: String,
    @SerializedName("CAP") val postalCode: String,
    @SerializedName("CITTA") val city: String,
    @SerializedName("PROV") val province: String,
    @SerializedName("NAZIONE") val nation: String,
    @SerializedName("DESTINA") val destinationId: Int,
    @SerializedName("destinaDescri") val destinationName: String,
    @SerializedName("DESCRI") val description: String,
    @SerializedName("nLav") val sequence: Int,
    @SerializedName("DATAI") val insertDate: String,
    @SerializedName("AGENTE") val agent: String,
    @SerializedName("TRASPO") val transportHandler: String,
    @SerializedName("COLLI") val parcels: Int,
    @SerializedName("vettNume") val carrierId: Int,
    @SerializedName("VETTORE") val carrierName: String,
    @SerializedName("PESO") val weight: Double,
    @SerializedName("numeOrdi") val port: String,
    @SerializedName("dataOrdi") val date: String,
    @SerializedName("NOTE") val notes: String,
    @SerializedName("dConsegna") val deliveryDate: String,
    @SerializedName("TASSATIVA") val deliveryDeadline: Boolean,
    @SerializedName("CONSEGNA") val deliveryType: Int,
    @SerializedName("statoCons") val deliveryState: Int,
    @SerializedName("URGENTE") val urgent: Boolean,
    @SerializedName("PARZIALE") val partial: Int,
    @SerializedName("NUMERO") val number: Int,

    @TypeConverters(OrderMetadataTypeConverter::class)
    @SerializedName("_metadata") val metadata: Metadata,

    @TypeConverters(OrderLinksTypeConverter::class)
    @SerializedName("links") val links: List<Link>,

    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
) {
        val insertDate2: Date?
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(insertDate)
    }



@Serializable
data class Metadata(
    @SerializedName("etag")
    val etag: String
)

@Serializable
data class Link(
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

@Serializable
data class OrdersResponseList(
    @SerializedName("items")
    val items: List<Order>
)

@Serializable
data class OrdersResponse(
    val items: List<Order>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)

