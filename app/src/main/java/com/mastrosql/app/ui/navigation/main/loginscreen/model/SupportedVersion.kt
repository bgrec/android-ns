package com.mastrosql.app.ui.navigation.main.loginscreen.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Suppress("KDocMissingDocumentation")
@Serializable
data class Column(
    @SerializedName("name") val name: String, @SerializedName("type") val type: String
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class VersionItem(
    @SerializedName("version") val version: String
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class ResponseItems(
    @SerializedName("type") val type: String,
    @SerializedName("items") val items: List<VersionItem>,
    @SerializedName("_metadata") val metadata: MetadataOfSupportedVersionResponse
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class MetadataOfSupportedVersionResponse(
    @SerializedName("columns") val columns: List<Column>
)

@Suppress("KDocMissingDocumentation")
@Serializable
data class SupportedVersionResponse(
    @SerializedName("items") val items: List<ResponseItems>
) {
    fun getServerVersion(): VersionItem? {
        return items.firstOrNull()?.items?.firstOrNull()
    }
}
