package com.jeweljester.game.navigation

object Routes {
    const val MAIN = "main"
    const val MENU = "menu"
    const val QUIZ_MENU = "quiz_menu"
    const val QUIZ = "quiz/{categoryId}"
    const val QUIZ_RESULTS = "quiz_results/{categoryId}/{score}"
    const val LEVELS = "levels"
    const val PAIRS = "pairs/{level}"
    const val PAIRS_RESULTS = "pairs_results/{level}/{matched}/{won}"
    const val SETTINGS = "settings"
    const val RESULTS = "results"
    const val RULES = "rules"

    fun quiz(categoryId: String) = "quiz/$categoryId"
    fun quizResults(categoryId: String, score: Int) = "quiz_results/$categoryId/$score"
    fun pairs(level: Int) = "pairs/$level"
    fun pairsResults(level: Int, matched: Int, won: Boolean) =
        "pairs_results/$level/$matched/$won"
}
