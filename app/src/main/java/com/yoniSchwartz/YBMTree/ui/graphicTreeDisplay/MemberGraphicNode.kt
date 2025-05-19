package com.yoniSchwartz.YBMTree.ui.graphicTreeDisplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import com.yoniSchwartz.YBMTree.data.FamilyMember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun MemberGraphicNode(
    familyMember: FamilyMember,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rectWidth = 200f
    val rectHeight = 100f
    val rect = Rect(Offset.Zero, Size(rectWidth, rectHeight))

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                if (rect.contains(offset)) {
                    onClick()
                }
            }
        }
    ) {
        drawRect(
            color = Color.Blue,
            topLeft = rect.topLeft,
            size = rect.size
        )
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 32f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val text = familyMember.getFullName()
            canvas.nativeCanvas.drawText(
                text,
                rect.center.x,
                rect.center.y + paint.textSize / 2,
                paint
            )
        }
    }
}
