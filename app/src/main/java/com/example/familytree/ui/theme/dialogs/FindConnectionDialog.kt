package com.example.familytree.ui.theme.dialogs

import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.theme.DialogTitle
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.TwoConfirmButtons
import com.example.familytree.ui.theme.graphicTreeDisplay.FamilyMemberCube

@Composable
fun ChooseTwoMembersToFindTheirConnectionDialog(
    onDismiss: () -> Unit,
    onFindConnection: (FamilyMember, FamilyMember) -> Unit = { _, _ -> }
) {

    val members = DatabaseManager.getAllMembers()
    var selectedMembers by remember { mutableStateOf(listOf<FamilyMember>()) }

    val title: String =
        HebrewText.CHOOSE_TWO_MEMBERS_WHOM_YOU_WOULD_LIKE_TO_FIND_THEIR_CONNECTION

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(title) },
        text = {
            BoxWithConstraints {
                val containerWidth = maxWidth
                val cubeWidth = calculateCubeWidth(containerWidth)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.chunked(3).forEach { rowMembers ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowMembers.forEach { member ->
                                val isSelected = selectedMembers.contains(member)
                                FamilyMemberCube(
                                    member = member,
                                    isSelected = isSelected,
                                    length = 80.dp,
                                    width = cubeWidth,
                                    onClick = {
                                        selectedMembers = if (isSelected) {
                                            selectedMembers - member
                                        } else {
                                            if (selectedMembers.size < 2) selectedMembers + member else selectedMembers
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TwoConfirmButtons(
                textForLeftButton = HebrewText.FIND_CONNECTION,
                onClickForLeftButton = {
                    onFindConnection(selectedMembers[0], selectedMembers[1])
                },
                enabledForLeftButton = selectedMembers.size == 2,
                textForRightButton = HebrewText.CANCEL,
                onClickForRightButton = onDismiss
            )
        }
    )
}

/**
 * Calculates the optimal width for a FamilyMemberCube to fit three in a row within a given container width.
 *
 * @param containerWidth The total width of the container (e.g., dialog width).
 * @param spacing The spacing between cubes in dp.
 * @return The calculated width for each cube in dp.
 */
fun calculateCubeWidth(containerWidth: Dp, spacing: Dp = 8.dp): Dp {
    val totalSpacing = spacing * 4 // 3 spaces between cubes + padding on both sides
    return (containerWidth - totalSpacing) / 3
}
