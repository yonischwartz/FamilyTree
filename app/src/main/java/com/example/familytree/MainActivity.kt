package com.example.familytree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.familytree.ui.theme.FamilyTreeTheme
import com.example.familytree.ui.theme.FamilyTreeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamilyTreeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Call your FamilyTreeScreen composable here
                    FamilyTreeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

