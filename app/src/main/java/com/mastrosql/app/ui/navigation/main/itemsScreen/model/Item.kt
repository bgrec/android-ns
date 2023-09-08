package com.mastrosql.app.ui.navigation.main.itemsScreen.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
@Serializable
data class Item(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("CORTO") val id: Int = 0,
    /*
     @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
     */
    @SerializedName("CODI") val sku: String,
    @SerializedName("CFOR") val vendorSku: String,
    @SerializedName("DESCRI") val description: String,
    @SerializedName("COST") val cost: Double,
    @SerializedName("IVA") val vat: String,
    @SerializedName("MISU") val measureUnit: String,
    @SerializedName("REPA") val department: String,
    @SerializedName("repaSotto") val subDepartment: String,
    @SerializedName("TIPO") val family: String,
    @SerializedName("TIPO_SOTTO") val subFamily: String,
    @SerializedName("VEND") val price: Double,
    @SerializedName("GRUPPO") val group: String,
    @SerializedName("ean_8") val ean8: String,
    @SerializedName("ean_13") val ean13: String?,
    @SerializedName("codAlt1") val eanAlt1: String?,
    @SerializedName("codAlt2") val eanAlt2: String?,
    @SerializedName("codAlt3") val eanAlt3: String?,
    @SerializedName("codAlt4") val eanAlt4: String?,
    @SerializedName("codAlt5") val eanAlt5: String?,
    @SerializedName("codAlt6") val eanAlt6: String?,
    @SerializedName("codAlt7") val eanAlt7: String?,
    @SerializedName("codAlt8") val eanAlt8: String?,
    @SerializedName("codAlt9") val eanAlt9: String?,
    @SerializedName("codAlt10") val eanAlt10: String?,

    @TypeConverters(MetadataTypeConverter::class)
    @SerializedName("_metadata") val metadata: Metadata
)
@Serializable
data class Metadata(
    @SerializedName("etag")
    val etag: String
)
@Serializable
data class ItemsResonseList(
    @SerializedName("items")
    val items: List<Item>
)