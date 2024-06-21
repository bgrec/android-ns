package com.mastrosql.app.data.local

/**
 * Preferences for swipe actions.
 */
@Suppress("KDocMissingDocumentation")
//@Singleton
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

    /**
     * Initializes swipe actions configuration upon object creation.
     */
    init {
        updateSwipeActionsEnabled()
    }

    /**
     * Updates the state of swipe actions based on the current flags [isDuplicateDisabled] and [isDeleteDisabled].
     * Sets [isSwipeActionsDisabled] to `true` if either [isDuplicateDisabled] or [isDeleteDisabled] is `true`;
     * otherwise, sets it to `false`.
     */
    private fun updateSwipeActionsEnabled() {
        isSwipeActionsDisabled = isDuplicateDisabled || isDeleteDisabled
    }
}
