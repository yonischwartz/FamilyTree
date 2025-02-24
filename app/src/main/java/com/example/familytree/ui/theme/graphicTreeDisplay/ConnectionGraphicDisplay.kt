package com.example.familytree.ui.theme.graphicTreeDisplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember

@Composable
fun ClickableShapesCanvas() {
    var isRectangleClicked by remember { mutableStateOf(false) }



    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val rectWidth = 200f
                    val rectHeight = 100f
                    val rect = Rect(
                        Offset(
                            (canvasWidth - rectWidth) / 2,
                            (canvasHeight - rectHeight) / 2
                        ),
                        Size(rectWidth, rectHeight)
                    )
                    if (rect.contains(offset)) {
                        isRectangleClicked = !isRectangleClicked
                    }
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val rectWidth = 200f
        val rectHeight = 100f

        val rect = Rect(
            Offset(
                (canvasWidth - rectWidth) / 2,
                (canvasHeight - rectHeight) / 2
            ),
            Size(rectWidth, rectHeight)
        )

        drawRect(
            color = if (isRectangleClicked) Color.Red else Color.Blue,
            topLeft = rect.topLeft,
            size = rect.size
        )
    }
}
