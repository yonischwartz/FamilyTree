package com.example.familytree.ui.theme.homeScreen.functionForButtons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.theme.dialogs.ChooseTwoMembersToFindTheirConnectionDialog
import com.example.familytree.ui.theme.dialogs.DisplayConnectionBetweenTwoMembersDialog
import com.example.familytree.ui.theme.dialogs.errorAndSuccessDialogs.NotEnoughMembersInTreeDialog

@Composable
fun FindConnectionsBetweenTwoMembers(
    onDismiss: () -> Unit
) {

    var memberOne by remember { mutableStateOf<FamilyMember?>(null) }
    var memberTwo by remember { mutableStateOf<FamilyMember?>(null) }

    // Step 1: Check if there are more than two members in the tree
    if (DatabaseManager.getAllMembers().size < 2) {
        // Display an error message
        NotEnoughMembersInTreeDialog(onDismiss = onDismiss)
    }

    // Step 2: Choose two members
    else if (memberOne == null && memberTwo == null) {
        ChooseTwoMembersToFindTheirConnectionDialog(
            onDismiss = onDismiss,
            onFindConnection = { member1, member2 ->
                memberOne = member1
                memberTwo = member2
            }
        )
    }

    // Step 3: Find and display the connection between the two members
    else {

        DisplayConnectionBetweenTwoMembersDialog(memberOne!!, memberTwo!!, onDismiss)
    }
}