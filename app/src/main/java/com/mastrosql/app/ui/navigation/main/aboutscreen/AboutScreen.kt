package com.mastrosql.app.ui.navigation.main.aboutscreen

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.BuildConfig
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.previews.AllScreenPreview
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController = rememberNavController(), context: Context) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.drawer_about)) },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate(MainNavOption.SettingsScreen.name) {
                        popUpTo(MainNavOption.LoginScreen.name)
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        stringResource(R.string.back_button)
                    )
                }
            })
    }) {
        BackHandler(true) {
            navController.navigate(MainNavOption.SettingsScreen.name) {
                popUpTo(MainNavOption.LoginScreen.name)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("App: MastroSQL")

            // Get app version dynamically
            val version = getAppVersion(context)
            Text(text = "Version: $version")

            // Get build number
            val buildNumber = getBuildNumber(context)
            Text(text = "Build: $buildNumber")

            // Get build date
            val buildDate = getBuildDate(context)
            Text(text = "Build Date: $buildDate")

            //Get build configuration
            val buildConfig = "Build Config: ${BuildConfig.BUILD_TYPE}"
            Text(text = buildConfig)


            // Get device information
            val deviceInfo = "Device: ${Build.MANUFACTURER} ${Build.MODEL}"
            Text(text = deviceInfo)

            // Get Android version
            val androidVersion = "Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
            Text(text = androidVersion)

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// navController.navigate(com.mastrosql.app.ui.navigation.intro.IntroNavOption.MotivationScreen.name)

// Function to get the app version
private fun getAppVersion(context: Context): String {
    return try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("AboutScreen", "Error getting app version", e)
        "N/A"
    }
}

// Function to get the build number
private fun getBuildNumber(context: Context): Int {
    return try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.longVersionCode.toInt()
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("AboutScreen", "Error getting build number", e)
        -1
    }
}

// Function to get the build date
private fun getBuildDate(context: Context): String {
    return try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val buildDateMillis = packageInfo.lastUpdateTime
        sdf.format(Date(buildDateMillis))
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("AboutScreen", "Error getting build date", e)
        "N/A"
    }
}

@AllScreenPreview
@Composable
fun AboutScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    MastroAndroidTheme {
        AboutScreen(navController, context)
    }
}