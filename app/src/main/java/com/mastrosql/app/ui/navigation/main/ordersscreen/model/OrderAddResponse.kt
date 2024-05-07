package com.mastrosql.app.ui.navigation.main.ordersscreen.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


/**
 * Insert new order response data class for JSON serialization
 *
 */

@Serializable
data class Column(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String
)

@Serializable
data class ResponseItem(
    @SerializedName("type")
    val type: String,
    @SerializedName("items")
    val items: List<Order>,
    @SerializedName("_metadata")
    val metadata: MetadataOfResponseItem
)

@Serializable
data class MetadataOfResponseItem(
    @SerializedName("columns")
    val columns: List<Column>
)

@Serializable
data class OrderAddResponse(
    @SerializedName("items")
    val items: List<ResponseItem>
) {
    /**
     * Retrieves the first order from the response, or returns null if no orders are present.
     */
    fun getAddedOrder(): Order? {
        return items.firstOrNull()?.items?.firstOrNull()
    }
}
/*
Content-Length: 1638
2024-05-06 18:12:53.234 17974-18182 okhttp.OkHttpClient     com.mastrosql.app.dev                I  {"items":[{"type":"items0","items":[{"NUME":36535,"CODI":15060,"RAG_SOC":"\"OLDERIGO\" DI MAGGI MAURO","VIA":"PIAZZA DELLA REPUBBLICA, 7","CAP":"58043","CITTA":"CASTIGLIONE DELLA PESCAIA","PROV":"GR","NAZIONE":"IT","DESCRI":"dfffggg","DESTINA_DESCRI":"","N_LAV":0,"DATAI":"2024-05-06","AGENTE":0,"DESTINA":0,"TRASPO":"","COLLI":0,"VETTORE":"","VETT_NUME":0,"PESO":0.000,"NUME_ORDI":"","DATA_ORDI":null,"NOTE":null,"D_CONSEGNA":null,"TASSATIVA":false,"CONSEGNA":0,"STATO_CONS":0,"URGENTE":false,"PARZIALE":0,"NUMERO":36496}],"_metadata":{"columns":[{"name":"NUME","type":" INTEGER"},{"name":"CODI","type":" INTEGER"},{"name":"RAG_SOC","type":"VARCHAR(60)"},{"name":"VIA","type":"VARCHAR(50)"},{"name":"CAP","type":"VARCHAR(10)"},{"name":"CITTA","type":"VARCHAR(50)"},{"name":"PROV","type":"VARCHAR(4)"},{"name":"NAZIONE","type":"VARCHAR(6)"},{"name":"DESCRI","type":"VARCHAR(80)"},{"name":"DESTINA_DESCRI","type":"VARCHAR(80)"},{"name":"N_LAV","type":" INTEGER"},{"name":"DATAI","type":"DATE"},{"name":"AGENTE","type":"SMALLINT"},{"name":"DESTINA","type":" INTEGER"},{"name":"TRASPO","type":"VARCHAR(15)"},{"name":"COLLI","type":"SMALLINT"},{"name":"VETTORE","type":"VARCHAR(60)"},{"name":"VETT_NUME","type":"SMALLINT"},{"name":"PESO","type":"DECIMAL(0,3)"},{"name":"NUME_ORDI","type":"VARCHAR(20)"},{"name":"DATA_ORDI","type":"DATE"},{"name":"NOTE","type":"VARCHAR(65535)"},{"name":"D_CONSEGNA","type":"DATE"},{"name":"TASSATIVA","type":"BIT(1)"},{"name":"CONSEGNA","type":"SMALLINT"},{"name":"STATO_CONS","type":"SMALLINT"},{"name":"URGENTE","type":"BIT(1)"},{"name":"PARZIALE","type":"SMALLINT"},{"name":"NUMERO","type":" INTEGER"}]}}]}
2024-05-06 18:12:53.234 17974-18182 okhttp.OkHttpClient     com.mastrosql.app.dev                I  <-- END HTTP (1638-byte body)
*/