package com.example.familytree.ui.theme.homeScreen.functionForButtons

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.DatabaseManager.addConnectionToBothMembersInLocalMap
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.ChooseMemberToRelateToDialog
import com.example.familytree.ui.theme.dialogs.HowAreTheyRelatedDialog

/**
 * A Composable function that allows the user to connect two existing family members
 * by selecting a relationship between them.
 *
 * The function follows these steps:
 * 1. Select the first member.
 * 2. Choose the relationship from the first member's perspective.
 * 3. Select the second member.
 * 4. Add the connection between the two members.
 *
 * @param existingMembers The list of available family members.
 * @param onDismiss A callback function to execute when the process is canceled or completed.
 */
@Composable
fun ConnectTwoMembers(
    existingMembers: List<FamilyMember>,
    onDismiss:() -> Unit,
) {

    var memberOne by remember { mutableStateOf<FamilyMember?>(null) }
    var memberTwo by remember { mutableStateOf<FamilyMember?>(null) }
    var relationFromMemberOnePerspective by remember { mutableStateOf<Relations?>(null) }

    val context = LocalContext.current

    // Choose first member
    if (memberOne == null) {
        ChooseMemberToRelateToDialog(
            existingMembers = existingMembers,
            onMemberSelected = { memberOne = it },
            showPreviousButton = false,
            onDismiss = onDismiss
        )
    }

    // Choose the relation between the members
    else if (relationFromMemberOnePerspective == null) {

        HowAreTheyRelatedDialog(
            existingMember = memberOne ?: FamilyMember(),
            onRelationSelected = { relationFromMemberOnePerspective = it },
            onPrevious = { memberOne = null },
            onDismiss = onDismiss
        )
    }

    // Choose second member
    else if (memberTwo == null) {
        ChooseMemberToRelateToDialog(
            existingMembers = existingMembers.filter { it.getId() != memberOne?.getId() }, // Exclude memberOne
            onMemberSelected = { memberTwo = it },
            onPrevious = { relationFromMemberOnePerspective = null },
            onDismiss = onDismiss
        )
    }

    // Add the connection
    else {
        addConnectionToBothMembersInLocalMap(
            memberOne = memberOne ?: FamilyMember(),
            memberTwo = memberTwo ?: FamilyMember(),
            relationFromMemberOnePerspective = relationFromMemberOnePerspective
                ?: Relations.SON // arbitrary relation
        )

        // Success message
        Toast.makeText(context, HebrewText.SUCCESS_ADDING_CONNECTION, Toast.LENGTH_SHORT).show()
        onDismiss()
    }
}

