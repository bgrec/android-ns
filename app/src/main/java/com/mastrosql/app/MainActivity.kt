package com.mastrosql.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mastrosql.app.ui.navigation.ProvideAppNavigationViewModel
import com.mastrosql.app.ui.navigation.main.MainCompose
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the app.
 */
@AndroidEntryPoint
@ExperimentalAnimationApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    @ExperimentalMaterial3Api
    //@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

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
                    //TODO - Add permissions request here

                    /*
                        val multiplePermissionsState = rememberMultiplePermissionsState(
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
                             Button(onClick = {
                                multiplePermissionsState.launchMultiplePermissionRequest() }) {
                                 Text(buttonText)
                             }
                         }
                     */
                }
            }
        }
    }
}

