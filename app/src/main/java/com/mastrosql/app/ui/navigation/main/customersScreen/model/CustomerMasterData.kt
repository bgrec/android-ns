package com.mastrosql.app.ui.navigation.main.customersScreen.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * This data class defines a Customer Master Data which includes an ID, and for the moment only the description.
 */

/**
 * For GSON Serialization  use @SerializedName(value = "DESCRI")
 * For JSON Kotlin Serialization use @SerialName( value = "DESCRI")
 */

@Serializable
data class CustomerMasterData(
    @SerializedName(value = "CODI") val id: Int,
    @SerializedName("DESCRI") val businessName: String,
    @SerializedName("VIA") val street: String,
    @SerializedName("CAP") val postalCode: String,
    @SerializedName("PIVA") val vat: String,
    @SerializedName("CITTA") val city: String,
    @SerializedName("PROV") val province: String,
    @SerializedName("NAZIONE") val nation: String,
    @SerializedName("DESC2") val businessName2: String,
    @SerializedName("links") val links: List<Link>,
    @SerializedName("codFis") val taxId: String,
    @SerializedName("_metadata") val metadata: Metadata
) {
    fun trimAllStrings(): CustomerMasterData {
        return CustomerMasterData(
            id = id,
            businessName = businessName.trim(),
            street = street.trim(),
            postalCode = postalCode.trim(),
            vat = vat.trim(),
            city = city.trim(),
            province = province.trim(),
            nation = nation.trim(),
            businessName2 = businessName2.trim(),
            links = links,
            taxId = taxId.trim(),
            metadata = metadata
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

