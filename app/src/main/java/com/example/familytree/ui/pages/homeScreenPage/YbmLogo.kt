package com.example.familytree.ui.pages.homeScreenPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.familytree.R

/**
 * A Composable function that displays the YBM logo image.
 */
@Composable
fun YbmLogo() {
    Image(
        painter = painterResource(id = R.drawable.ybm),
        contentDescription = "Family Tree Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}
