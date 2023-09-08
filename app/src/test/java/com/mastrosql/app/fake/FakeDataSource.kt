package com.mastrosql.app.fake

import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataResponse

object FakeDataSource {

    private const val idOne = 1
    private const val idTwo = 2
    private const val businessNameOne = "Ragione sociale 1"
    private const val businessNameTwo = "Ragione sociale 2"
    private const val streetOne = "Via 1"
    private const val streetTwo = "Via 2"
    private const val postalCodeOne = "12345"
    private const val postalCodeTwo = "54321"
    private const val vatOne = "12345678901"
    private const val vatTwo = "10987654321"
    private const val cityOne = "Città 1"
    private const val cityTwo = "Città 2"
    private const val provinceOne = "Provincia 1"
    private const val provinceTwo = "Provincia 2"
    private const val nationOne = "Nazione 1"
    private const val nationTwo = "Nazione 2"
    private const val taxIdOne = "12345678901"
    private const val taxIdTwo = "10987654321"

    private val customerMasterDataList = listOf(
        CustomerMasterData(
            id = idOne,
            businessName = businessNameOne,
            street = streetOne,
            postalCode = postalCodeOne,
            vat = vatOne,
            city = cityOne,
            province = provinceOne,
            nation = nationOne,
            businessName2 = businessNameTwo,
            links = emptyList(),
            taxId = taxIdOne,
            metadata = Metadata("etag")
        ),
        CustomerMasterData(
            id = idTwo,
            businessName = businessNameTwo,
            street = streetTwo,
            postalCode = postalCodeTwo,
            vat = vatTwo,
            city = cityTwo,
            province = provinceTwo,
            nation = nationTwo,
            businessName2 = businessNameTwo,
            links = emptyList(),
            taxId = taxIdTwo,
            metadata = Metadata("etag")
        )
    )

    val customersMasterDataResponse = CustomersMasterDataResponse(
        customerMasterDataList,
        limit = 100,
        offset = 0,
        hasMore = true,
        count = 2,
        links = emptyList()
    )
}
