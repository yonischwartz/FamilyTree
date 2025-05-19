package com.yoniSchwartz.YBMTree.ui.graphicTreeDisplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.yoniSchwartz.YBMTree.data.PositionedMember

/**
 * A composable function that graphically displays a family tree structure
 * using a dynamically sized [Canvas]. Each family member is represented as a labeled rectangle,
 * and connections (relationships) are drawn as lines between the nodes.
 *
 * The canvas is automatically sized based on the farthest positions of the members,
 * ensuring the entire family tree is visible. The container is scrollable in both
 * horizontal and vertical directions to allow exploration of large layouts.
 *
 * @param members A list of [PositionedMember] objects, each containing a family member
 * and their assigned (x, y) position in the graphical layout.
 */
@Composable
fun FamilyTreeGraphicDisplay(members: List<PositionedMember>) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, _ ->
            scale = (scale * zoom).coerceIn(0.3f, 5f)
            offset += pan
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(gestureModifier)
    ) {
        Canvas(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .fillMaxSize()
        ) {
            val drawnConnections = mutableSetOf<Pair<String, String>>()

            // Draw all non-marriage connections
            members.forEach { member ->
                member.member.getConnections().forEach { connection ->
                    if (connection.relationship.name != "MARRIAGE") {
                        val target = members.find { it.member.getId() == connection.memberId }
                        if (target != null) {
                            drawLine(
                                color = Color.Black,
                                start = Offset(member.x, member.y),
                                end = Offset(target.x, target.y),
                                strokeWidth = 2f
                            )
                        }
                    }
                }
            }

            // Draw members and handle marriages
            members.forEach { member ->
                // Skip if already rendered as part of a marriage
                val alreadyDrawnAsSpouse = members.any { other ->
                    val ids = listOf(member.member.getId(), other.member.getId()).sorted()
                    drawnConnections.contains(Pair(ids[0], ids[1]))
                }
                if (alreadyDrawnAsSpouse) return@forEach

                val spouseConnection = member.member.getConnections().find {
                    it.relationship.name == "MARRIAGE"
                }

                val spouse = spouseConnection?.let {
                    members.find { other -> other.member.getId() == it.memberId }
                }

                if (spouse != null) {
                    // Mark this marriage as drawn
                    val ids = listOf(member.member.getId(), spouse.member.getId()).sorted()
                    drawnConnections.add(Pair(ids[0], ids[1]))

                    // Draw married pair side-by-side
                    val leftX = minOf(member.x, spouse.x)
                    val centerY = (member.y + spouse.y) / 2
                    val boxSize = Size(200f, 80f)
                    val boxTop = centerY - boxSize.height / 2

                    // First box
                    drawRoundRect(
                        color = Color.LightGray,
                        topLeft = Offset(leftX, boxTop),
                        size = boxSize,
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    // Second box
                    drawRoundRect(
                        color = Color.LightGray,
                        topLeft = Offset(leftX + boxSize.width, boxTop),
                        size = boxSize,
                        cornerRadius = CornerRadius(10f, 10f)
                    )

                    // Text in each box
                    drawContext.canvas.nativeCanvas.drawText(
                        member.member.getFullName(),
                        leftX + boxSize.width / 2,
                        centerY + 10,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 28f
                            color = android.graphics.Color.BLACK
                        }
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        spouse.member.getFullName(),
                        leftX + boxSize.width + boxSize.width / 2,
                        centerY + 10,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 28f
                            color = android.graphics.Color.BLACK
                        }
                    )

                    // Marriage label
                    drawContext.canvas.nativeCanvas.drawText(
                        "Marriage",
                        leftX + boxSize.width,
                        boxTop - 12,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 26f
                            isFakeBoldText = true
                            color = android.graphics.Color.DKGRAY
                        }
                    )
                } else {
                    // Draw this member alone
                    drawRoundRect(
                        color = Color.LightGray,
                        topLeft = Offset(member.x - 100, member.y - 40),
                        size = Size(200f, 80f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        member.member.getFullName(),
                        member.x,
                        member.y,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 32f
                            color = android.graphics.Color.BLACK
                        }
                    )
                }
            }
        }
    }
}
