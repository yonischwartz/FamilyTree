package com.yoniSchwartz.YBMTree.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.yoniSchwartz.YBMTree.R
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager.loadMembersFromFirebaseIntoLocalMap

/**
 * Composable function that displays the splash screen while loading family member data from Firebase.
 *
 * This screen is shown at app startup and remains visible until the data loading process completes.
 * Once loading finishes, it navigates to the home screen (`HomeScreenPage`) and removes itself
 * from the back stack to prevent users from navigating back to it.
 *
 * @param navController The NavController for handling navigation.
 * @param onLoadingFinished A callback function that is triggered when loading completes,
 *                          allowing `MainActivity` to dismiss the system splash screen.
 */
@Composable
fun SplashScreenPage(navController: NavController, onLoadingFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        loadMembersFromFirebaseIntoLocalMap {
            onLoadingFinished() // Notify that loading is finished, so the splash screen can disappear
            navController.navigate("familyTreeScreen") {
                popUpTo("splashScreen") { inclusive = true } // Remove splash from back stack
            }
        }
    }

    // Display the app's default splash screen UI while loading data
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ybm),
            contentDescription = "Splash Screen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
