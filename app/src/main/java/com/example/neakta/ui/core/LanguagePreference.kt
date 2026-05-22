package com.example.neakta.ui.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object LanguagePreference {
    private val LANGUAGE_KEY = stringPreferencesKey("app_language")

    fun getLanguage(context: Context): Flow<AppLanguage> =
        context.dataStore.data.map { prefs ->
            val saved = prefs[LANGUAGE_KEY] ?: AppLanguage.ENGLISH.name
            AppLanguage.valueOf(saved)
        }

    suspend fun saveLanguage(context: Context, language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language.name
        }
    }
}