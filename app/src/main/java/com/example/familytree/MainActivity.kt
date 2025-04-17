package com.example.familytree

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.familytree.data.dataManagement.DatabaseManager.downloadFamilyTreeImageToCache
import com.example.familytree.data.dataManagement.DatabaseManager.loadMembersFromFirebaseIntoLocalMap
import com.example.familytree.ui.FamilyTreeViewModel
import com.example.familytree.ui.pages.AdminPage
import com.example.familytree.ui.pages.FamilyTreeGraphPage
import com.example.familytree.ui.pages.MemberListPage


class MainActivity : ComponentActivity() {

    private val viewModel: FamilyTreeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load family members and image URL from local storage before setting the UI
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
                                    FamilyTreeGraphPage(navController = navController, viewModel)
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

    /**
     * Loads necessary data (family members and family tree graph image) before initializing the UI.
     *
     * This function:
     * - Loads the last saved family tree image from local storage.
     * - Fetches all family members from Firestore and populates the MemberMap.
     * - Once both tasks are completed, it calls [onComplete] to proceed with UI setup.
     *
     * @param onComplete A callback function that runs once data is fully loaded.
     */
    private fun loadDataBeforeUI(onComplete: () -> Unit) {

        // Initialize image from local storage
        downloadFamilyTreeImageToCache(applicationContext) { file ->
            file?.let {
                viewModel.setImageFile(it)
            }
        }

        // Load family members from Firestore
        loadMembersFromFirebaseIntoLocalMap { success ->
            if (success) {
                // Proceed with UI setup only after data is loaded
                onComplete()
            } else {
                // Even if family members fail to load, proceed with UI to avoid blocking the app
                onComplete()
            }
        }
    }
}