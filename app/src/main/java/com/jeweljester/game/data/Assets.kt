package com.jeweljester.game.data

import androidx.annotation.DrawableRes
import com.jeweljester.game.R

/**
 * Соответствие «роль -> ресурс drawable». Чтобы заменить графику,
 * положи файл с тем же именем в res/drawable-nodpi.
 *
 * bg_main.jpg   — фон
 * logo.png      — логотип
 * banner.png    — золотой баннер (заголовок)
 * jester_main.png — маскот-джокер
 * btn_plate.png — пустая плашка кнопки (текст рисуется поверх)
 * plate_round.png — круглая плашка (кнопки уровней)
 * panel_bg.png  — фон панели (пауза/результаты/правила)
 * icon_back.png / icon_pause.png — иконки
 * jewel_01..jewel_12 — 12 предметов для «найди пару»
 */
object Assets {
    @DrawableRes val bgMain = R.drawable.bg_main
    @DrawableRes val logo = R.drawable.logo
    @DrawableRes val banner = R.drawable.banner
    @DrawableRes val jester = R.drawable.jester_main
    @DrawableRes val jesterA = R.drawable.jester_a
    @DrawableRes val jesterB = R.drawable.jester_b
    @DrawableRes val btnPlate = R.drawable.btn_plate
    @DrawableRes val btnMenu = R.drawable.btn_menu
    @DrawableRes val plateRound = R.drawable.plate_round
    @DrawableRes val panelBg = R.drawable.panel_bg
    @DrawableRes val iconBack = R.drawable.icon_back
    @DrawableRes val iconPause = R.drawable.icon_pause

    val jewels: List<Int> = listOf(
        R.drawable.jewel_01, R.drawable.jewel_02, R.drawable.jewel_03,
        R.drawable.jewel_04, R.drawable.jewel_05, R.drawable.jewel_06,
        R.drawable.jewel_07, R.drawable.jewel_08, R.drawable.jewel_09,
        R.drawable.jewel_10, R.drawable.jewel_11, R.drawable.jewel_12,
    )
}
