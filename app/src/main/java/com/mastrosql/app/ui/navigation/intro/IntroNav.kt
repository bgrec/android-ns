package com.mastrosql.app.ui.navigation.intro

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mastrosql.app.ui.navigation.intro.composables.AboutScreen
import com.mastrosql.app.ui.navigation.intro.composables.MotivationScreen
import com.mastrosql.app.ui.navigation.intro.composables.RecommendationScreen
import com.mastrosql.app.ui.navigation.intro.composables.WelcomeScreen
import com.mastrosql.app.ui.navigation.main.NavRoutes

//Navigation graph for the intro screens

fun NavGraphBuilder.introGraph(navController: NavController) {
    navigation(
        startDestination = IntroNavOption.WelcomeScreen.name,
        route = NavRoutes.IntroRoute.name
    ) {
        composable(IntroNavOption.AboutScreen.name) {
            AboutScreen(LocalContext.current)
        }
        composable(IntroNavOption.WelcomeScreen.name) {
            WelcomeScreen(navController)
        }
        composable(IntroNavOption.MotivationScreen.name) {
            MotivationScreen(navController)
        }
        composable(IntroNavOption.RecommendationScreen.name) {
            RecommendationScreen(navController)
        }
    }
}

enum class IntroNavOption {
    WelcomeScreen,
    MotivationScreen,
    RecommendationScreen,
    AboutScreen
}
