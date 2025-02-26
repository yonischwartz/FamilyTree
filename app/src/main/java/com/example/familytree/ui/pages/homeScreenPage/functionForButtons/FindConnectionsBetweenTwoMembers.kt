package com.example.familytree.ui.pages.homeScreenPage.functionForButtons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.dialogs.ChooseTwoMembersToFindTheirConnectionDialog
import com.example.familytree.ui.dialogs.DisplayConnectionBetweenTwoMembersDialog
import com.example.familytree.ui.dialogs.errorAndSuccessDialogs.NotEnoughMembersInTreeDialog

/**
 * Composable function to find and display the connection between two yeshiva members.
 *
 * This function guides the user through three steps:
 * 1. Checks if there are enough members in the family tree.
 * 2. Allows the user to select two members.
 * 3. Displays the connection between the selected members.
 *
 * @param onDismiss Lambda function to handle dialog dismissal.
 */
@Composable
fun FindConnectionsBetweenTwoMembers(
    onDismiss: () -> Unit,
    givenFirstMember: FamilyMember? = null,
) {

    var memberOne by remember { mutableStateOf(givenFirstMember) }
    var memberTwo by remember { mutableStateOf<FamilyMember?>(null) }

    // Step 1: Check if there are more than two Yeshiva FamilyMembers in the tree
    if (DatabaseManager.getAllYeshivaMembers().size < 2) {
        // Display an error message
        NotEnoughMembersInTreeDialog(onDismiss = onDismiss)
    }

    // Step 2.1: Choose two members
    else if (memberOne == null && memberTwo == null) {
        ChooseTwoMembersToFindTheirConnectionDialog(
            onDismiss = onDismiss,
            onFindConnection = { member1, member2 ->
                memberOne = member1
                memberTwo = member2
            }
        )
    }

    // Step 2.2: Choose one member in case, one is already given
    else if (memberTwo == null) {
        ChooseTwoMembersToFindTheirConnectionDialog(
            onDismiss = onDismiss,
            onFindConnection = { _, member2 ->
                memberTwo = member2
            }
        )
    }

    // Step 3: Find and display the connection between the two members
    else {

        DisplayConnectionBetweenTwoMembersDialog(memberOne!!, memberTwo!!, onDismiss)
    }
}