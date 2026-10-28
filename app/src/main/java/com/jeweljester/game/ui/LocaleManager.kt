package com.jeweljester.game.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Per-app language via AppCompatDelegate. "System" is an EMPTY locale list, not a concrete
 * language, so returning to it follows the system language again. AppCompatDelegate is the
 * single source of truth — the choice is persisted by the AppLocalesMetadataHolderService
 * declared in the manifest, so it survives a restart (including below API 33).
 */
object LocaleManager {

    fun applyLocale(tag: String?) {
        val locales =
            if (tag.isNullOrBlank()) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    /** Current choice for the UI: empty means "System", not "English". */
    fun currentTag(): String? =
        AppCompatDelegate.getApplicationLocales().toLanguageTags().takeIf { it.isNotBlank() }
}
