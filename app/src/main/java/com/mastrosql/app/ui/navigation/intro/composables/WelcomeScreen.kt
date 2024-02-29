package com.mastrosql.app.ui.navigation.intro.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun WelcomeScreen(navController: NavController = rememberNavController()) = IntroScreen(
    navController = navController
)


@Preview(apiLevel = 33)
@Composable
fun WelcomeScreenPreview() {
    MastroAndroidTheme {
        WelcomeScreen()
    }
}

