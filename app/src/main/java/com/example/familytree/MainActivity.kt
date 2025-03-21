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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.familytree.ui.FamilyTreeTheme
import com.example.familytree.ui.pages.homeScreenPage.HomeScreenPage
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.familytree.data.dataManagement.DatabaseManager.loadMembersFromFirebaseIntoLocalMap
import com.example.familytree.ui.pages.adminPage.AdminPage
import com.example.familytree.ui.pages.memberListPage.MemberListPage


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load members from Firebase before setting the UI
        loadMembersFromFirebaseIntoLocalMap {
            runOnUiThread {
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
                                    HomeScreenPage(navController = navController)
                                }
                                composable("memberListPage") {
                                    MemberListPage(navController = navController)
                                }
                                composable("adminPage") {
                                    AdminPage(navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
