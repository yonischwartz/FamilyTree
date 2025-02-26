package com.example.familytree

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.familytree.ui.FamilyTreeTheme
import com.example.familytree.ui.pages.homeScreenPage.FamilyTreeScreen
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.familytree.ui.pages.memberListPage.MemberListPage


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamilyTreeTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "familyTreeScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("familyTreeScreen") {
                            FamilyTreeScreen(navController = navController)
                        }
                        composable("memberListPage") {
                            MemberListPage(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
