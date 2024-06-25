package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model

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

@Entity(tableName = "warehouse_outbound_details")
@Serializable
data class WhOutDetailsItem(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("numePro") val id: Int,
    @SerializedName("NUME") val whOutId: Int,
    @SerializedName("RIGA") val whOutRow: Int?,
    @SerializedName("ELABORATA") val confirmed: Boolean?,
    @SerializedName("CORTO") val articleId: Int?,
    @SerializedName("artCodi") val sku: String?,
    @SerializedName("artCfor") val vendorSku: String?,
    @SerializedName("DESCRI") val description: String?,
    @SerializedName("LIBE") val completeDescription: String?,
    @SerializedName("QUAN") val quantity: Double?,
    @SerializedName("quanT") val tmpQuantity: Double?,
    @SerializedName("RESO") val returnedQuantity: Double?,
    @SerializedName("PESO") val weight: Double?,
    @SerializedName("COSTO") val cost: String?,
    @SerializedName("VEND") val price: String?,
    @SerializedName("IVA") val vat: String?,
    @SerializedName("ivaPerc") val vatValue: Double?,
    @SerializedName("SCON") val discount: Double?,
    @SerializedName("scon_1") val discount1: Double?,
    @SerializedName("scon_2") val discount2: Double?,
    @SerializedName("scon_3") val discount3: Double?,
    @SerializedName("LISTINO") val catalogPrice: Double?,
    @SerializedName("MISU") val measureUnit: String?,
    @SerializedName("COLL") val rowType: Int?,
    @SerializedName("qtConf") val packSize: String?,
    @SerializedName("ordQtOrd") val orderedQuantity: Double?,
    @SerializedName("ordQtCon") val shippedQuantity: Double?,
    @SerializedName("LOTTO") val batch: String?,
    @SerializedName("dataSca") val expirationDate: String?,
    @SerializedName("VARIE") val various: String?,

    @TypeConverters(WhOutDetailsMetadataTypeConverter::class) @SerializedName("_metadata") val metadata: Metadata?,
    @TypeConverters(WhOutDetailLinksTypeConverter::class) @SerializedName("links") val links: List<Link>,

    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
) {
    val expirationDate2: Date?
        get() = expirationDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(it) }
}

/**
 *  Metadata from the API
 */
@Serializable
data class Metadata(
    @SerializedName("etag") val etag: String
)

@Serializable
data class Link(
    @SerializedName("rel") val rel: String, @SerializedName("href") val href: String
)

@Serializable
data class WhOutDetailsResponseList(
    @SerializedName("items") val items: List<WhOutDetailsItem>
)

@Serializable
data class WhOutDetailsResponse(
    val items: List<WhOutDetailsItem>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)



