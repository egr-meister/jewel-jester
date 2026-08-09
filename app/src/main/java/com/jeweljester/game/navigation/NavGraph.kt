package com.jeweljester.game.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jeweljester.game.screens.GameRulesScreen
import com.jeweljester.game.screens.LevelsScreen
import com.jeweljester.game.screens.MainScreen
import com.jeweljester.game.screens.MenuScreen
import com.jeweljester.game.screens.PairsGameScreen
import com.jeweljester.game.screens.PairsResultsScreen
import com.jeweljester.game.screens.QuizMenuScreen
import com.jeweljester.game.screens.QuizResultsScreen
import com.jeweljester.game.screens.QuizScreen
import com.jeweljester.game.screens.ResultsScreen
import com.jeweljester.game.screens.SettingsScreen

@Composable
fun JewelJesterRoot() {
    val nav = rememberNavController()
    NavGraph(nav)
}

@Composable
private fun NavGraph(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Routes.MAIN) {

        composable(Routes.MAIN) { MainScreen(nav) }
        composable(Routes.MENU) { MenuScreen(nav) }
        composable(Routes.QUIZ_MENU) { QuizMenuScreen(nav) }
        composable(Routes.SETTINGS) { SettingsScreen(nav) }
        composable(Routes.RESULTS) { ResultsScreen(nav) }
        composable(Routes.LEVELS) { LevelsScreen(nav) }
        composable(Routes.RULES) { GameRulesScreen(nav) }

        composable(
            route = Routes.QUIZ,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { entry ->
            QuizScreen(nav, entry.arguments?.getString("categoryId").orEmpty())
        }

        composable(
            route = Routes.QUIZ_RESULTS,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType },
            )
        ) { entry ->
            QuizResultsScreen(
                nav,
                entry.arguments?.getString("categoryId").orEmpty(),
                entry.arguments?.getInt("score") ?: 0
            )
        }

        composable(
            route = Routes.PAIRS,
            arguments = listOf(navArgument("level") { type = NavType.IntType })
        ) { entry ->
            PairsGameScreen(nav, entry.arguments?.getInt("level") ?: 1)
        }

        composable(
            route = Routes.PAIRS_RESULTS,
            arguments = listOf(
                navArgument("level") { type = NavType.IntType },
                navArgument("matched") { type = NavType.IntType },
                navArgument("won") { type = NavType.BoolType },
            )
        ) { entry ->
            PairsResultsScreen(
                nav,
                entry.arguments?.getInt("level") ?: 1,
                entry.arguments?.getInt("matched") ?: 0,
                entry.arguments?.getBoolean("won") ?: false
            )
        }
    }
}
