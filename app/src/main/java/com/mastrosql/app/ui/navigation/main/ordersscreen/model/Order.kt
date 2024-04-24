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
    @SerializedName("CODI") val clientId: Int?,
    @SerializedName("ragSoc") val businessName: String?,
    @SerializedName("VIA") val street: String?,
    @SerializedName("CAP") val postalCode: String?,
    @SerializedName("CITTA") val city: String?,
    @SerializedName("PROV") val province: String?,
    @SerializedName("NAZIONE") val nation: String?,
    @SerializedName("DESTINA") val destinationId: Int?,
    @SerializedName("destinaDescri") val destinationName: String?,
    @SerializedName("DESCRI") val description: String?,
    @SerializedName("nLav") val sequence: Int?,
    @SerializedName("DATAI") val insertDate: String?,
    @SerializedName("AGENTE") val agent: String?,
    @SerializedName("TRASPO") val transportHandler: String?,
    @SerializedName("COLLI") val parcels: Int?,
    @SerializedName("vettNume") val carrierId: Int?,
    @SerializedName("VETTORE") val carrierName: String?,
    @SerializedName("PESO") val weight: Double?,
    @SerializedName("numeOrdi") val port: String?,
    @SerializedName("dataOrdi") val date: String?,
    @SerializedName("NOTE") val notes: String?,
    @SerializedName("dConsegna") val deliveryDate: String?,
    @SerializedName("TASSATIVA") val deliveryDeadline: Boolean?,
    @SerializedName("CONSEGNA") val deliveryType: Int?,
    @SerializedName("statoCons") val deliveryState: Int?,
    @SerializedName("URGENTE") val urgent: Boolean?,
    @SerializedName("PARZIALE") val partial: Int?,
    @SerializedName("NUMERO") val number: Int?,

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

/*
* import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Define data classes to represent the JSON structure
@Serializable
data class ItemsContainer(
    @SerialName("items")
    val items: List<Item>
)

@Serializable
data class Item(
    @SerialName("type")
    val type: String,
    @SerialName("items")
    val items: List<Order>,
    @SerialName("_metadata")
    val metadata: Metadata
)

@Serializable
data class Order(
    @SerialName("NUME")
    val NUME: Int,
    @SerialName("CODI")
    val CODI: Int,
    @SerialName("RAG_SOC")
    val RAG_SOC: String,
    // Define other properties as per your JSON structure
)

@Serializable
data class Metadata(
    @SerialName("columns")
    val columns: List<Column>
)

@Serializable
data class Column(
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String
)

// JSON string to be serialized
val jsonString = """{"items":[{"type":"items0","items":[{"NUME":36477,"CODI":15480,"RAG_SOC":"Osteria Pian delle Viti,","VIA":"","CAP":"","CITTA":"","PROV":"","NAZIONE":"IT","DESCRI":"fff","DESTINA_DESCRI":"","N_LAV":0,"DATAI":"2024-04-24","AGENTE":0,"DESTINA":0,"TRASPO":"","COLLI":0,"VETTORE":"","VETT_NUME":0,"PESO":0.000,"NUME_ORDI":"","DATA_ORDI":null,"NOTE":null,"D_CONSEGNA":null,"TASSATIVA":false,"CONSEGNA":0,"STATO_CONS":0,"URGENTE":false,"PARZIALE":0,"NUMERO":0}],"_metadata":{"columns":[{"name":"NUME","type":" INTEGER"},{"name":"CODI","type":" INTEGER"},{"name":"RAG_SOC","type":"VARCHAR(60)"},{"name":"VIA","type":"VARCHAR(50)"},{"name":"CAP","type":"VARCHAR(10)"},{"name":"CITTA","type":"VARCHAR(50)"},{"name":"PROV","type":"VARCHAR(4)"},{"name":"NAZIONE","type":"VARCHAR(6)"},{"name":"DESCRI","type":"VARCHAR(80)"},{"name":"DESTINA_DESCRI","type":"VARCHAR(80)"},{"name":"N_LAV","type":" INTEGER"},{"name":"DATAI","type":"DATE"},{"name":"AGENTE","type":"SMALLINT"},{"name":"DESTINA","type":" INTEGER"},{"name":"TRASPO","type":"VARCHAR(15)"},{"name":"COLLI","type":"SMALLINT"},{"name":"VETTORE","type":"VARCHAR(60)"},{"name":"VETT_NUME","type":"SMALLINT"},{"name":"PESO","type":"DECIMAL(0,3)"},{"name":"NUME_ORDI","type":"VARCHAR(20)"},{"name":"DATA_ORDI","type":"DATE"},{"name":"NOTE","type":"VARCHAR(65535)"},{"name":"D_CONSEGNA","type":"DATE"},{"name":"TASSATIVA","type":"BIT(1)"},{"name":"CONSEGNA","type":"SMALLINT"},{"name":"STATO_CONS","type":"SMALLINT"},{"name":"URGENTE","type":"BIT(1)"},{"name":"PARZIALE","type":"SMALLINT"},{"name":"NUMERO","type":" INTEGER"}]}}]}"""

// Deserialize the JSON string into the defined data classes
val itemsContainer = Json.decodeFromString<ItemsContainer>(jsonString)

// Access the deserialized data
val firstOrder = itemsContainer.items.firstOrNull()?.items?.firstOrNull()
println(firstOrder)*/
