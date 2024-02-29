package com.mastrosql.app

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mastrosql.app.ui.navigation.ProvideAppNavigationViewModel
import com.mastrosql.app.ui.navigation.main.MainCompose
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import dagger.hilt.android.AndroidEntryPoint


// Constants
// The authority for the sync adapter's content provider
const val AUTHORITY = "com.example.android.datasync.provider"

// An account type, in the form of a domain name
const val ACCOUNT_TYPE = "example.com"

// The account name
const val ACCOUNT = "placeholderaccount"

@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    // Instance fields
    private lateinit var mAccount: Account


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // Create the placeholder account
        mAccount = createSyncAccount()

        setContent {
            MastroAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    //modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Provide AppNavigationViewModel to the entire app using CompositionLocal

                    ProvideAppNavigationViewModel {

                        // MainCompose is the main entry point for the app
                        MainCompose()

                    }

                    //TODO - Add permissions request
                    /* val multiplePermissionsState = rememberMultiplePermissionsState(
                         listOf(
                             android.Manifest.permission.POST_NOTIFICATIONS,
                             android.Manifest.permission.INTERNET,
                             android.Manifest.permission.ACCESS_COARSE_LOCATION,
                             android.Manifest.permission.ACCESS_FINE_LOCATION
                         )
                     )

                     if (multiplePermissionsState.allPermissionsGranted) {
                         Text("All permissions granted")
                     } else {
                         val allPermissionsRevoked =
                             multiplePermissionsState.permissions.size ==
                                     multiplePermissionsState.revokedPermissions.size
                         val textToShow = if (!allPermissionsRevoked) {
                             // If not all the permissions are revoked, it's because the user accepted the COARSE
                             // location permission, but not the FINE one.
                             "Yay! Thanks for letting me access your approximate location. " +
                                     "But you know what would be great? If you allow me to know where you " +
                                     "exactly are. Thank you!"
                         } else if (multiplePermissionsState.shouldShowRationale) {
                             // Both location permissions have been denied
                             "Getting your exact location is important for this app. " +
                                     "Please grant us fine location. Thank you :D"
                         } else {
                             // First time the user sees this feature or the user doesn't want to be asked again
                             "This feature requires location permission"
                         }

                         val buttonText = if (!allPermissionsRevoked) {
                             "Allow precise location"
                         } else {
                             "Request permissions"
                         }

                         Text(text = textToShow)
                         Spacer(modifier = Modifier.height(8.dp))
                         Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                             Text(buttonText)
                         }
                     }*/

                }

            }

        }
    }

    /**
     * Create a new placeholder account for the sync adapter
     */
    private fun createSyncAccount(): Account {
        val accountManager = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        return Account(ACCOUNT, ACCOUNT_TYPE).also { newAccount ->
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {/*
                 * If you don't set android:syncable="true" in
                 * in your <provider> element in the manifest,
                 * then call context.setIsSyncable(account, AUTHORITY, 1)
                 * here.
                 */
            } else {/*
                 * The account exists or some other error occurred. Log this, report it,
                 * or handle it internally.
                 */
            }
        }
    }
}

