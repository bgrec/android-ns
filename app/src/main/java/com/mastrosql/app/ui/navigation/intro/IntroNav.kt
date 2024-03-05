package com.mastrosql.app.ui.navigation.intro

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mastrosql.app.ui.navigation.intro.composables.IntroScreen
import com.mastrosql.app.ui.navigation.intro.composables.NewsScreen
import com.mastrosql.app.ui.navigation.main.NavRoutes

//Navigation graph for the intro screens

fun NavGraphBuilder.introGraph(navController: NavController) {
    navigation(
        startDestination = IntroNavOption.IntroScreen.name,
        route = NavRoutes.IntroRoute.name
    ) {
        composable(IntroNavOption.IntroScreen.name){
            IntroScreen(navController)
        }
        composable(IntroNavOption.NewsScreen.name){
            NewsScreen(navController)
        }
    }
}

enum class IntroNavOption {
    IntroScreen,
    NewsScreen
}
