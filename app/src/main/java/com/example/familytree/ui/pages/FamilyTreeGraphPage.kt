package com.example.familytree.ui.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.familytree.ui.FamilyTreeTopBar
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.ScrollableFamilyTreeGraph

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FamilyTreeGraphPage(navController: NavController) {

    var isBackButtonEnabled by remember { mutableStateOf(true) }

    Column() {

        FamilyTreeTopBar(
            text = HebrewText.FAMILY_TREE,
            onClickBack = {
                if (isBackButtonEnabled) {
                    isBackButtonEnabled = false
                    navController.popBackStack()
                }
            }
        )

        ScrollableFamilyTreeGraph()
    }
}