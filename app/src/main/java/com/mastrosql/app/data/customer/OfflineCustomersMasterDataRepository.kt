package com.mastrosql.app.data.customer

import com.mastrosql.app.data.item.ItemsRepository
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDao
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import kotlinx.coroutines.flow.Flow
/*
class OfflineCustomersMasterDataRepository {
        override fun getAllCustomersStream(): Flow<List<Item>> = itemDao.getAllItems()

        override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

        override suspend fun insertItem(item: Item) = itemDao.insert(item)

        override suspend fun deleteItem(item: Item) = itemDao.delete(item)

        override suspend fun updateItem(item: Item) = itemDao.update(item)
    }
}*/