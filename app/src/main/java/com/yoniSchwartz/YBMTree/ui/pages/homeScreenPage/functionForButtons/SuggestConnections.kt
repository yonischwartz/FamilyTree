package com.yoniSchwartz.YBMTree.ui.pages.homeScreenPage.functionForButtons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.FullConnection
import com.yoniSchwartz.YBMTree.data.Relations
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager
import com.yoniSchwartz.YBMTree.ui.dialogs.SuggestConnectionDialog

/**
 * A Composable function that suggests family connections based on predefined logic.
 * It fetches the next suggested connection from DatabaseManager and checks if it's redundant
 * before displaying it in a dialog.
 *
 * @param onDismiss Callback function that is triggered when no more suggestions remain.
 */
@Composable
fun SuggestConnections(
    onDismiss: () -> Unit
) {
    var suggestedConnection by remember { mutableStateOf(DatabaseManager.popNextSuggestedConnection()) }

    while (suggestedConnection != null) {
        // A check to see maybe suggestion is redundant
        if (checkIfConnectionAlreadyExists(suggestedConnection!!)) {
            suggestedConnection = DatabaseManager.popNextSuggestedConnection()
            continue
        }

        // Another check to see maybe suggestion is redundant
        if (suggestedConnection!!.relationship == Relations.FATHER ||
            suggestedConnection!!.relationship == Relations.MOTHER) {

            if (checkIfChildHasMarriageConnectionWhichIsAlreadyChildOfParent(suggestedConnection!!)) {
                suggestedConnection = DatabaseManager.popNextSuggestedConnection()
                continue
            }
        }

        // If a valid suggestion is found, show the dialog
        SuggestConnectionDialog(
            suggestedConnection = suggestedConnection!!,
            onDismiss = {
                suggestedConnection = DatabaseManager.popNextSuggestedConnection()
            }
        )

        return
    }

    // If no valid suggestions remain, dismiss
    onDismiss()
}

/**
 * Before suggesting a child-parent connection, this function checks if the child is
 * married to someone who is already a child of the proposed parent. This ensures
 * that married couples are not mistakenly added as siblings.
 *
 * If the child has a spouse who is already a recognized child of the parent,
 * then the child cannot also be added as a child to the same parent.
 *
 * @param suggestedConnection FullConnection - holds: child, parent, relation (FATHER / MOTHER).
 * @return Boolean - true if child has a spouse who is already a child of parent, false otherwise.
 */
private fun checkIfChildHasMarriageConnectionWhichIsAlreadyChildOfParent(
    suggestedConnection: FullConnection
): Boolean {

    val child: FamilyMember = suggestedConnection.memberOne
    val parent: FamilyMember = suggestedConnection.memberTwo

    // Check if child is married
    if (child.getConnections().find { it.relationship == Relations.MARRIAGE } != null) {

        // Child is married. find the corresponding MARRIAGE connection
        val childSpouseId: String =
            child.getConnections().find { it.relationship == Relations.MARRIAGE }!!.memberId

        val childSpouse: FamilyMember = DatabaseManager.getMemberById(childSpouseId)!!

        // Check if childSpouse is already a child of the parent
        return childSpouse.getConnections().any { it.memberId == parent.getId() }
    }

    // Child is not married
    return false
}

/**
 * Ensures that no redundant connection suggestions are made.
 *
 * A connection might be added to the queue and, before being processed, gets
 * approved and added in another way. This leads to an unnecessary suggestion,
 * since the connection already exists.
 *
 * Example:
 * - Two siblings are suggested to have a specific family member as their parent.
 * - If the user approves the connection for one sibling, the parent is
 *   automatically added to the other sibling.
 * - This makes it redundant to suggest the same parent-child connection again.
 *
 * This function checks if the proposed connection already exists to prevent
 * duplicate suggestions.
 *
 * @param fullConnection FullConnection - the connection to validate.
 * @return Boolean - true if the connection already exists, false otherwise.
 */
private fun checkIfConnectionAlreadyExists(
    fullConnection: FullConnection
): Boolean {

    val memberOne = fullConnection.memberOne
    val memberTwo = fullConnection.memberTwo

    return memberOne.getConnections().any { it.memberId == memberTwo.getId() }
}
