package com.mastrosql.app.ui.navigation.intro.composables

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.ui.navigation.intro.IntroNavOption
import com.mastrosql.app.ui.previews.AllScreenPreview
import com.mastrosql.app.ui.theme.MastroAndroidTheme


@Composable
fun MotivationScreen(navController: NavController) = IntroCompose(
    navController = navController,
    text = "Motivation"
) {
    navController.navigate(IntroNavOption.RecommendationScreen.name)
}

@AllScreenPreview
@Composable
fun MotivationPrivacyPreview() {
    val navController = rememberNavController()
    MastroAndroidTheme {
        MotivationScreen(navController = navController)
    }
}

