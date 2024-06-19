package com.mastrosql.app.data.datasource

import com.mastrosql.app.data.itemTest.ItemTest
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData

/**
 * Data source class for loading items and clients.
 */
class DataSourceTest_cancel_it_Later {

    /**
     * Loads a list of test items [ItemTest].
     */
    fun loadItems(): List<ItemTest> {
        return listOf(
            ItemTest(1, "ean1", "description1"),
            ItemTest(2, "ean2", "description2"),
            ItemTest(3, "ean3", "description3"),
            ItemTest(4, "ean4", "description4"),
            ItemTest(5, "ean5", "description5"),
            ItemTest(6, "ean6", "description6"),
            ItemTest(7, "ean7", "description7"),
            ItemTest(8, "ean8", "description8"),
            ItemTest(9, "ean9", "description9"),
            ItemTest(10, "ean10", "description10"),
            ItemTest(11, "ean11", "description11"),
            ItemTest(12, "ean12", "description12"),
            ItemTest(13, "ean13", "description13"),
            ItemTest(14, "ean14", "description14"),
            ItemTest(15, "ean15", "description15"),
            ItemTest(16, "ean16", "description16"),
            ItemTest(17, "ean17", "description17"),
            ItemTest(18, "ean18", "description18"),
            ItemTest(19, "ean19", "description19"),
            ItemTest(20, "ean20", "description20"),
            ItemTest(21, "ean21", "description21"),
            ItemTest(22, "ean22", "description22"),
            ItemTest(23, "ean23", "description23"),
            ItemTest(24, "ean24", "description24"),
            ItemTest(25, "ean25", "description25"),
            ItemTest(26, "ean26", "description26"),
            ItemTest(27, "ean27", "description27"),
            ItemTest(28, "ean28", "description28"),
            ItemTest(29, "ean29", "description29"),
            ItemTest(30, "ean30", "description30"),
            ItemTest(31, "ean31", "description31"),
            ItemTest(32, "ean32", "description32"),
            ItemTest(33, "ean33", "description33"),
            ItemTest(34, "ean34", "description34"),
            ItemTest(35, "ean35", "description35"),
            ItemTest(36, "ean36", "description36"),
            ItemTest(37, "ean37", "description37"),
            ItemTest(38, "ean38", "description38"),
            ItemTest(39, "ean39", "description39"),
            ItemTest(40, "ean40", "description40"),
        )
    }

    /**
     * Filters a list of [ItemTest] objects based on the provided query string.
     */
    private fun filterItems(list: List<ItemTest>, query: String): List<ItemTest> {
        // Filter the list
        return list.filter { it.description?.contains(query, ignoreCase = true) ?: false }
    }

    /**
     * Loads and filters a list of [ItemTest] objects based on the provided query string.
     *
     * This function retrieves a list of [ItemTest] objects, filters them based on the given query string,
     * and returns the filtered list where the description of each item contains the query string,
     * ignoring case.
     */
    fun loadFilteredItemsByDescription(query: String): List<ItemTest> {
        return filterItems(loadItems(), query)
    }

    /**
     * Loads a list of [CustomerMasterData] objects.
     *
     * This function returns a static list of [CustomerMasterData] objects.
     * Uncomment the lines to include specific instances of [CustomerMasterData] with their respective descriptions.
     */
    private fun loadCustomers(): List<CustomerMasterData> {
        return listOf(
            /* CustomerMasterData(1, "description1"),
             CustomerMasterData(2, "description2"),
             CustomerMasterData(3, "description3"),
             CustomerMasterData(4, "description4"),
             CustomerMasterData(5, "description5"),
             CustomerMasterData(6, "description6"),
             CustomerMasterData(7, "description7"),
             CustomerMasterData(8, "description8"),
             CustomerMasterData(9, "description9"),
             CustomerMasterData(10, "description10"),
             CustomerMasterData(11, "description11"),
             CustomerMasterData(12, "description12"),
             CustomerMasterData(13, "description13"),
             CustomerMasterData(14, "description14"),
             CustomerMasterData(15, "description15"),
             CustomerMasterData(16, "description16"),
             CustomerMasterData(17, "description17"),
             CustomerMasterData(18, "description18"),
             CustomerMasterData(19, "description19"),
             CustomerMasterData(20, "description20"),
             CustomerMasterData(21, "description21"),
             CustomerMasterData(22, "description22"),
             CustomerMasterData(23, "description23"),
             CustomerMasterData(24, "description24"),
             CustomerMasterData(25, "description25"),
             CustomerMasterData(26, "description26"),
             CustomerMasterData(27, "description27"),
             CustomerMasterData(28, "description28"),
             CustomerMasterData(29, "description29"),
             CustomerMasterData(30, "description30"),
             CustomerMasterData(31, "description31"),
             CustomerMasterData(32, "description32"),
             CustomerMasterData(33, "description33"),
             CustomerMasterData(34, "description34"),
             CustomerMasterData(35, "description35"),
             CustomerMasterData(36, "description36"),
             CustomerMasterData(37, "description37"),
             CustomerMasterData(38, "description38"),
             CustomerMasterData(39, "description39"),
             CustomerMasterData(40, "description40"),*/
        )
    }

    /**
     * Filters a list of [CustomerMasterData] based on a query string.
     *
     * This function filters the provided [list] of [CustomerMasterData] objects
     * based on whether their business name contains the [query] string, ignoring case.
     */
    private fun filterClients(
        list: List<CustomerMasterData>,
        query: String
    ):

            /**
             * Filters a list of [CustomerMasterData] based on a query string.
             *
             * This function filters the provided [list] of [CustomerMasterData] objects
             * based on whether their business name contains the [query] string, ignoring case.
             * If [query] is empty or null, all items are returned.
             */
            List<CustomerMasterData> {
        // Filter the list
        //return list.filter { it.description?.contains(query, ignoreCase = true) ?: false }
        return list.filter { it.businessName?.contains(query, ignoreCase = true) ?: true }
    }

    /**
     * Loads filtered list of [CustomerMasterData] based on a description query.
     *
     * This function retrieves a list of all customer master data entries,
     * filters them based on whether their business name contains the provided [query] string,
     * and returns the filtered list.
     */
    fun loadFilteredClientsByDescription(query: String): List<CustomerMasterData> {
        return filterClients(loadCustomers(), query)
    }

}