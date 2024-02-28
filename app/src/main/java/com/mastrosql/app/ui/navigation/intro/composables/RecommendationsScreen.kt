package com.mastrosql.app.ui.navigation.intro.composables

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.UserPreferencesViewModel
import com.mastrosql.app.ui.navigation.main.NavRoutes
import com.mastrosql.app.ui.previews.AllScreenPreview
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun RecommendationScreen(
    navController: NavController,
    viewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) = IntroCompose(
    navController = navController,
    text = "Raccomandazione",
    buttonText = R.string.start_app,
    ) {
    /*
    * When the user clicks on the button, we set the onBoardingCompleted to true
     */
    viewModel.onBoardingCompleted(true)
    navController.navigate(NavRoutes.MainRoute.name) {
        popUpTo(NavRoutes.IntroRoute.name)
    }
}

@AllScreenPreview
@Composable
fun RecommendationScreenPreview() {
    val navController = rememberNavController()
    MastroAndroidTheme {
        RecommendationScreen(navController = navController)
    }

}