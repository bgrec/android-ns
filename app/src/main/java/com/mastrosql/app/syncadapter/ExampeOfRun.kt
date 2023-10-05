package com.mastrosql.app.syncadapter

/*
...
// Constants
// Content provider authority
const val AUTHORITY = "com.example.android.datasync.provider"
// Account type
const val ACCOUNT_TYPE = "com.example.android.datasync"
// Account
const val ACCOUNT = "default_account"
// Incoming Intent key for extended data
const val KEY_SYNC_REQUEST = "com.example.android.datasync.KEY_SYNC_REQUEST"
...
class GcmBroadcastReceiver : BroadcastReceiver() {
    ...
    override fun onReceive(context: Context, intent: Intent) {
        // Get a GCM object instance
        val gcm: GoogleCloudMessaging = GoogleCloudMessaging.getInstance(context)
        // Get the type of GCM message
        val messageType: String? = gcm.getMessageType(intent)
        /*
         * Test the message type and examine the message contents.
         * Since GCM is a general-purpose messaging system, you
         * may receive normal messages that don't require a sync
         * adapter run.
         * The following code tests for a a boolean flag indicating
         * that the message is requesting a transfer from the device.
         */
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE == messageType
            && intent.getBooleanExtra(KEY_SYNC_REQUEST, false)) {
            /*
             * Signal the framework to run your sync adapter. Assume that
             * app initialization has already created the account.
             */
            ContentResolver.requestSync(mAccount, AUTHORITY, null)
            ...
        }
        ...
    }
    ...
}


// Constants
// Content provider scheme
const val SCHEME = "content://"
// Content provider authority
const val AUTHORITY = "com.example.android.datasync.provider"
// Path for the content provider table
const val TABLE_PATH = "data_table"
...
class MainActivity : FragmentActivity() {
    ...
    // A content URI for the content provider's data table
    private lateinit var uri: Uri
    // A content resolver for accessing the provider
    private lateinit var mResolver: ContentResolver
    ...
    inner class TableObserver(...) : ContentObserver(...) {
        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         * This method signature is provided for compatibility with
         * older platforms.
         */
        override fun onChange(selfChange: Boolean) {
            /*
             * Invoke the method signature available as of
             * Android platform version 4.1, with a null URI.
             */
            onChange(selfChange, null)
        }

        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         */
        override fun onChange(selfChange: Boolean, changeUri: Uri?) {
            /*
             * Ask the framework to run your sync adapter.
             * To maintain backward compatibility, assume that
             * changeUri is null.
             */
            ContentResolver.requestSync(account, AUTHORITY, null)
        }
        ...
    }
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        // Get the content resolver object for your app
        mResolver = contentResolver
        // Construct a URI that points to the content provider data table
        uri = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .path(TABLE_PATH)
                .build()
        /*
         * Create a content observer object.
         * Its code does not mutate the provider, so set
         * selfChange to "false"
         */
        val observer = TableObserver(false)
        /*
         * Register the observer for the data table. The table's path
         * and any of its subpaths trigger the observer.
         */
        mResolver.registerContentObserver(uri, true, observer)
        ...
    }
    ...
}

// Content provider authority
const val AUTHORITY = "com.example.android.datasync.provider"
// Account
const val ACCOUNT = "default_account"
// Sync interval constants
const val SECONDS_PER_MINUTE = 60L
const val SYNC_INTERVAL_IN_MINUTES = 60L
const val SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE
...
class MainActivity : FragmentActivity() {
    ...
    // A content resolver for accessing the provider
    private lateinit var mResolver: ContentResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        // Get the content resolver for your app
        mResolver = contentResolver
        /*
         * Turn on periodic syncing
         */
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL)
        ...
    }
    ...
}

// Constants
// Content provider authority
val AUTHORITY = "com.example.android.datasync.provider"
// Account type
val ACCOUNT_TYPE = "com.example.android.datasync"
// Account
val ACCOUNT = "default_account"
...
class MainActivity : FragmentActivity() {
    ...
    // Instance fields
    private lateinit var mAccount: Account
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        /*
         * Create the placeholder account. The code for CreateSyncAccount
         * is listed in the lesson Creating a Sync Adapter
         */

        mAccount = createSyncAccount()
        ...
    }

    /**
     * Respond to a button click by calling requestSync(). This is an
     * asynchronous operation.
     *
     * This method is attached to the refresh button in the layout
     * XML file
     *
     * @param v The View associated with the method call,
     * in this case a Button
     */
    fun onRefreshButtonClick(v: View) {
        // Pass the settings flags by inserting them in a bundle
        val settingsBundle = Bundle().apply {
            putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        }
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle)
    }



 */
