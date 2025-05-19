package com.yoniSchwartz.YBMTree.ui.graphicTreeDisplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

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
