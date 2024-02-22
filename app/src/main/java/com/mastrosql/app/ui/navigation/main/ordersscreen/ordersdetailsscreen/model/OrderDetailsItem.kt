package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model

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

@Entity(tableName = "order_details")
@Serializable
data class OrderDetailsItem(

    @PrimaryKey(autoGenerate = false) @SerializedName("NUMEPRO") val id: Int,
    @SerializedName("NUME") val orderId: Int,
    @SerializedName("RIGA") val orderRow: Int,
    @SerializedName("ELABORATA") val confirmed: Boolean,
    @SerializedName("CORTO") val articleId: Int,
    @SerializedName("ART_CODI") val sku: String,
    @SerializedName("ART_CFOR") val vendorSku: String,
    @SerializedName("DESCRI") val description: String,
    @SerializedName("LIBE") val completeDescription: String,
    @SerializedName("QUAN") val quantity: Double,
    @SerializedName("QUAN_T") val tmpQuantity: Double,
    @SerializedName("RESO") val returnedQuantity: Double,
    @SerializedName("PESO") val weight: Double,
    @SerializedName("COSTO") val cost: String,
    @SerializedName("VEND") val price: String,
    @SerializedName("IVA") val vat: String,
    @SerializedName("IVA_PERC") val vatValue: Double,
    @SerializedName("SCON") val discount: Double,
    @SerializedName("SCON_1") val discount1: Double,
    @SerializedName("SCON_2") val discount2: Double,
    @SerializedName("SCON_3") val discount3: Double,
    @SerializedName("LISTINO") val catalogPrice: Double,
    @SerializedName("MISU") val measureUnit: String,
    @SerializedName("COLL") val rowType: Int,
    @SerializedName("QT_CONF") val packSize: String,
    @SerializedName("ORD_QT_ORD") val orderedQuantity: Double,
    @SerializedName("ORD_QT_CON") val shippedQuantity: Double,
    @SerializedName("LOTTO") val batch: String,
    @SerializedName("DATA_SCA") val expirationDate: String,

    @TypeConverters(OrderDetailsMetadataTypeConverter::class) @SerializedName("_metadata") val metadata: Metadata?,

    @TypeConverters(OrderDetailLinksTypeConverter::class) @SerializedName("links") val links: List<Link>,

    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
) {
    val expirationDate2: Date?
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(expirationDate)
}

@Serializable
data class Metadata(
    @SerializedName("etag") val etag: String
)

@Serializable
data class Link(
    @SerializedName("rel") val rel: String, @SerializedName("href") val href: String
)

@Serializable
data class OrderDetailsResponseList(
    @SerializedName("items") val items: List<OrderDetailsItem>
)

@Serializable
data class OrderDetailsResponse(
    val items: List<OrderDetailsItem>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)


