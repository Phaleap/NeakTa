package com.example.neakta.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ── Token ────────────────────────────────────────────────
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    // ── Onboarding ───────────────────────────────────────────
    fun setOnboardingSeen() {
        prefs.edit().putBoolean(KEY_ONBOARDING, true).apply()
    }

    fun hasSeenOnboarding(): Boolean = prefs.getBoolean(KEY_ONBOARDING, false)

    // ── Logout ───────────────────────────────────────────────
    fun clearSession() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .apply()
        // NOTE: KEY_ONBOARDING is intentionally kept —
        // user shouldn't see onboarding again after logout
    }

    companion object {
        private const val PREF_NAME      = "neakta_prefs"
        private const val KEY_TOKEN      = "auth_token"
        private const val KEY_ONBOARDING = "has_seen_onboarding"
    }
}
