package com.mastrosql.app.ui.navigation.intro.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.previews.AllScreenPreview
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun AboutScreen(drawerState: DrawerState) {
    Scaffold(
        topBar = { AppBar(drawerState = drawerState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("About")

        }
    }
}

@AllScreenPreview
@Composable
fun AboutScreenPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    MastroAndroidTheme {
        AboutScreen(drawerState)
    }
}