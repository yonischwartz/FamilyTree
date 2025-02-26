package com.example.familytree.ui.dialogs

import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.graphicTreeDisplay.FamilyMemberCube

/**
 * Displays a dialog that allows the user to select two yeshiva members
 * and find their connection within the yeshiva's family tree.
 *
 * @param onDismiss A callback triggered when the dialog is dismissed.
 * @param onFindConnection A callback with two selected [FamilyMember]s to find their connection.
 */
@Composable
fun ChooseTwoMembersToFindTheirConnectionDialog(
    onDismiss: () -> Unit,
    onFindConnection: (FamilyMember, FamilyMember) -> Unit
) {

    val members = DatabaseManager.getAllYeshivaMembers()
    var selectedMembers by remember { mutableStateOf(listOf<FamilyMember>()) }

    val title: String =
        HebrewText.CHOOSE_TWO_MEMBERS_WHOM_YOU_WOULD_LIKE_TO_FIND_THEIR_CONNECTION

    DialogWithTwoButtons(
        title = title,
        onClickForLeft = { onFindConnection(selectedMembers[0], selectedMembers[1]) },
        textForLeft = HebrewText.FIND_CONNECTION,
        enabledForLeftButton = selectedMembers.size == 2,
        onClickForRight = onDismiss,
        textForRight = HebrewText.CANCEL,
        contentOfDialog = {
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
