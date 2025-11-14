package com.aryanspatel.droidwire.presentation.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.aryanspatel.droidwire.presentation.screens.DetailScreen
import com.aryanspatel.droidwire.presentation.screens.HomeScreen
import com.aryanspatel.droidwire.presentation.screens.SettingsScreen
import com.aryanspatel.droidwire.presentation.viewmodels.DetailViewModel
import com.aryanspatel.droidwire.presentation.viewmodels.HomeViewModel

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = {fadeIn(animationSpec = tween(0))},
        exitTransition = {fadeOut(animationSpec = tween(0))},
        popEnterTransition = {fadeIn(animationSpec = tween(0))},
        popExitTransition = {fadeOut(animationSpec = tween(0))}
    ){
        composable(Routes.HOME) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetail = {id, url ->
                    Log.d("DetailDebug", "NavGraph: normal id and url : $id, $url")
                   val encoded = Uri.encode(url)
                    navController.navigate("detail/$id/$encoded")},
                onNavigateToSettings =  { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id"){type = NavType.StringType},
                navArgument(name = "url"){type = NavType.StringType}),
            deepLinks = listOf(navDeepLink { uriPattern = Routes.DETAIL_DEEPLINK })
        ){ backstackEntry ->
            val id = backstackEntry.arguments?.getString("id")!!
            val encodedUrl = backstackEntry.arguments?.getString("url")!!
            val url = Uri.decode(encodedUrl)

            val viewModel = hiltViewModel<DetailViewModel>()
            DetailScreen(
                id, url,
                onBackPressed = {navController.popBackStack()},
                viewModel = viewModel
            )
        }
        composable(route = Routes.SETTINGS) {
            SettingsScreen()
        }
    }


}