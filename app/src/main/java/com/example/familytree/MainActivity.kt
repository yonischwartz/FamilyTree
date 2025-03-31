package com.example.familytree

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.familytree.ui.FamilyTreeTheme
import com.example.familytree.ui.pages.homeScreenPage.HomeScreenPage
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.data.dataManagement.DatabaseManager.loadMembersFromFirebaseIntoLocalMap
import com.example.familytree.ui.pages.AdminPage
import com.example.familytree.ui.pages.FamilyTreeGraphPage
import com.example.familytree.ui.pages.MemberListPage


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load family members and image URL before setting the UI
        loadDataBeforeUI {
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
                                composable("familyTreeGraphPage") {
                                    FamilyTreeGraphPage(navController = navController)
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

/**
 * Loads both family members and the family tree graph image from Firebase,
 * ensuring both are available before launching the UI.
 */
private fun loadDataBeforeUI(onComplete: () -> Unit) {
    var isMembersLoaded = false
    var isImageLoaded = false

    fun checkIfReady() {
        if (isMembersLoaded && isImageLoaded) {
            onComplete()
        }
    }

    // Load family members
    loadMembersFromFirebaseIntoLocalMap {
        isMembersLoaded = true
        checkIfReady()
    }

    // Load image URL
    DatabaseManager.loadFamilyTreeGraphImageWithUrlFromFirebase {
        isImageLoaded = true
        checkIfReady()
    }
}