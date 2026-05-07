package com.example.neakta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.neakta.navigation.NeaktaNavGraph
import com.example.neakta.ui.theme.NeakTaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeakTaTheme {
                NeaktaNavGraph()
            }
        }
    }
}