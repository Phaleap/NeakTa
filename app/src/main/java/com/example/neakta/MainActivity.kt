package com.example.neakta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.neakta.navigation.NeaktaNavGraph
import com.example.neakta.ui.core.LanguagePreference
import com.example.neakta.ui.core.LanguageState
import com.example.neakta.ui.core.LocalAppLanguage
import com.example.neakta.ui.theme.NeakTaTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val languageState = LanguageState()
        runBlocking {
            languageState.current = LanguagePreference.getLanguage(this@MainActivity).first()
        }

        setContent {
            CompositionLocalProvider(LocalAppLanguage provides languageState) {
                NeakTaTheme {
                    NeaktaNavGraph()
                }
            }
        }
    }
}