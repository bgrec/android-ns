package com.mastrosql.app.data.item

import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDao
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import kotlinx.coroutines.flow.Flow

/**
 * Repository class for managing offline storage of items using Room database.
 * Implements [ItemsRepository] interface to provide methods for CRUD operations on items.
 */
class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {

    /**
     * Retrieves a reactive stream of all items from the database.
     */
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()

    /**
     * Retrieves a reactive stream of a single item based on its ID from the database.
     */
    override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

    /**
     * Inserts a new item into the offline database.
     */
    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    /**
     * Deletes an item from the offline database.
     */
    override suspend fun deleteItem(item: Item) = itemDao.delete(item)

    /**
     * Updates an item in the offline database.
     */
    override suspend fun updateItem(item: Item) = itemDao.update(item)
}