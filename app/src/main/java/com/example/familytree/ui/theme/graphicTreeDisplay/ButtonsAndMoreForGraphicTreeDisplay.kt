package com.example.familytree.ui.theme.graphicTreeDisplay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember


/**
 * A composable that displays a clickable rectangle representing a family member.
 *
 * @param member The family member to display.
 * @param isSelected Whether the rectangle is currently selected.
 * @param length The length of the rectangle (height).
 * @param width The width of the rectangle.
 * @param onClick Action to perform when the rectangle is clicked.
 */
@Composable
fun FamilyMemberCube(
    member: FamilyMember,
    isSelected: Boolean,
    length: Dp,
    width: Dp,
    onClick: () -> Unit
) {
    val lightGreen = Color(0xFF90EE90)
    val color = if (isSelected) lightGreen else Color.Gray

    Box(
        modifier = Modifier
            .size(width, length)
            .clip(RoundedCornerShape(8.dp)) // Rounded edges
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = member.getFullName(),
            textAlign = TextAlign.Center,
        )
    }
}
