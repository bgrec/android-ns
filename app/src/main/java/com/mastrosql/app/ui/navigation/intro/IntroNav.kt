package com.mastrosql.app.ui.navigation.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mastrosql.app.ui.navigation.intro.composables.IntroScreen
import com.mastrosql.app.ui.navigation.intro.composables.NewsScreen
import com.mastrosql.app.ui.navigation.main.NavRoutes

/**
 * Builds the navigation graph for the intro screens
 */
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
fun NavGraphBuilder.introGraph(navController: NavController) {
    navigation(
        startDestination = IntroNavOption.IntroScreen.name,
        route = NavRoutes.IntroRoute.name
    ) {
        composable(IntroNavOption.IntroScreen.name) {
            IntroScreen(navController)
        }
        composable(IntroNavOption.NewsScreen.name) {
            NewsScreen(navController)
        }
    }
}

/**
 * Represents the navigation options for the intro screens
 */
enum class IntroNavOption {
    /**
     * Represents the intro screen
     */
    IntroScreen,

    /**
     * Represents the news screen
     */
    NewsScreen
}
