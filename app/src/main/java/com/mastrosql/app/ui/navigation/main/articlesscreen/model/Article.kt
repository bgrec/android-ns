package com.mastrosql.app.ui.navigation.main.articlesscreen.model

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

@Entity(tableName = "articles")
@Serializable
data class Article(
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
    @SerializedName("EAN_8") val ean8: String,
    @SerializedName("EAN_13") val ean13: String?,
    @SerializedName("EAN_ALT") val eanAlt: String?,

    @TypeConverters(ArticleMetadataTypeConverter::class)
    @SerializedName("_metadata") val metadata: Metadata,

    @TypeConverters(ArticleLinksTypeConverter::class)
    @SerializedName("links") val links: List<Link>,

    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
) {
    fun trimAllStrings(): Article {
        return Article(
            id = id,
            sku = sku.trim(),
            vendorSku = vendorSku.trim(),
            description = description.trim(),
            cost = cost,
            vat = vat.trim(),
            measureUnit = measureUnit.trim(),
            department = department.trim(),
            subDepartment = subDepartment.trim(),
            family = family.trim(),
            subFamily = subFamily.trim(),
            price = price,
            group = group.trim(),
            ean8 = ean8.trim(),
            ean13 = ean13?.trim(),
            eanAlt = eanAlt?.trim(),
            links = links,
            metadata = metadata,
            page = page,
            lastUpdated = lastUpdated
        )
    }
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
data class ArticlesResponseList(
    @SerializedName("items")
    val items: List<Article>
)

@Serializable
data class ArticlesResponse(
    val items: List<Article>,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val count: Int,
    val links: List<Link>
)

