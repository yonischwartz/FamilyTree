package com.example.familytree.ui.pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.CenteredLoadingIndicator
import com.example.familytree.ui.FamilyTreeTopBar
import com.example.familytree.ui.FamilyTreeViewModel
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.ZoomableFamilyTreeImage
import java.io.File


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FamilyTreeGraphPage(navController: NavController, viewModel: FamilyTreeViewModel) {

    var isBackButtonEnabled by remember { mutableStateOf(true) }


//    var downloadProgress by remember { mutableIntStateOf(0) }
//
//
//    var localImageFile by remember { mutableStateOf<File?>(null) }
//
//    // Start downloading image once
//    LaunchedEffect(Unit) {
//        DatabaseManager.downloadFamilyTreeImageWithProgress(
//            onProgress = { progress ->
//                downloadProgress = progress
//            },
//            onFinished = { file ->
//                localImageFile = file
//            }
//        )
//    }

    // Get the image file from ViewModel
    val localImageFile = viewModel.imageFile.value

    Column(modifier = Modifier.fillMaxSize()) {

        FamilyTreeTopBar(
            text = HebrewText.FAMILY_TREE,
            onClickBack = {
                if (isBackButtonEnabled) {
                    isBackButtonEnabled = false
                    navController.popBackStack()
                }
            }
        )

        when {
            localImageFile != null -> {

                ZoomableFamilyTreeImage(localImageFile)
            }

            else -> {

                CenteredLoadingIndicator()

//                Text(
//                    text = "טוען תמונה... $downloadProgress%",
//                    modifier = Modifier.padding(16.dp)
//                )
            }
        }
    }
}