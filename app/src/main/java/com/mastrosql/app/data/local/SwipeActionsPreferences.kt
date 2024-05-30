package com.mastrosql.app.data.local

import javax.inject.Singleton

/**
 * Preferences for swipe actions.
 */
@Suppress("KDocMissingDocumentation")
@Singleton
class SwipeActionsPreferences(
    isDeleteDisabled: Boolean = false,
    isDuplicateDisabled: Boolean = false
) {
    private var isSwipeActionsDisabled: Boolean = false

    var isDuplicateDisabled: Boolean = isDuplicateDisabled
        set(value) {
            field = value
            updateSwipeActionsEnabled()
        }

    var isDeleteDisabled: Boolean = isDeleteDisabled
        set(value) {
            field = value
            updateSwipeActionsEnabled()
        }

    init {
        updateSwipeActionsEnabled()
    }

    private fun updateSwipeActionsEnabled() {
        isSwipeActionsDisabled = isDuplicateDisabled || isDeleteDisabled
    }
}
