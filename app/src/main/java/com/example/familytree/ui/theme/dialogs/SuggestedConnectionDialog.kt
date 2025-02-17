package com.example.familytree.ui.theme.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FullConnection
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.theme.HebrewText


/**
 * A dialog that suggests a potential family connection and asks the user to confirm or dismiss it.
 *
 * @param suggestedConnection The suggested connection between two family members.
 * @param onDismiss A callback function to handle the dismissal of the dialog.
 */
@Composable
fun SuggestConnectionDialog(
    suggestedConnection: FullConnection,
    onDismiss: () -> Unit
) {
    // Get members and relation
    val memberOne: FamilyMember = suggestedConnection.memberOne
    val memberTwo: FamilyMember = suggestedConnection.memberTwo
    val relation: Relations = suggestedConnection.relationship

    // Check if connection already exist
    if (checkIfConnectionExistAlready(memberOne, memberTwo)) {
        onDismiss()
    }

    // Get gender of memberTwo
    val memberTwoGender = suggestedConnection.memberTwo.getGender()

    // Get Strings
    val pronouns = if (memberTwoGender) HebrewText.HE else HebrewText.SHE
    val memberOneName: String = suggestedConnection.memberOne.getFullName()
    val memberTwoName: String = suggestedConnection.memberTwo.getFullName()
    val relationString: String = suggestedConnection.relationship.displayAsConnections(memberTwoGender)

    val title: String =
        HebrewText.DOES + " " + memberTwoName + " " +
                pronouns + " " + HebrewText.THE +
                relationString + memberOneName + "?"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { },
        confirmButton = {
            TextButton(
                onClick = {
                    DatabaseManager.addConnectionToBothMembersInLocalMap(memberOne, memberTwo, relation)
                    onDismiss()
                }
            ) {
                Text(HebrewText.YES)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(HebrewText.NO)
            }
        }
    )
}

/**
 * Checks if a suggested connection already exists between two family members.
 *
 * @param suggestedConnection The suggested connection to check.
 * @return `true` if the connection already exists, otherwise `false`.
 * @throws NullPointerException if `suggestedConnection` or its members are null.
 */
private fun checkIfConnectionExistAlready(
    memberOne: FamilyMember,
    memberTwo: FamilyMember
): Boolean {

    for (connection in memberOne.getConnections()) {

        val connectedMemberId = connection.memberId
        if (connectedMemberId == memberTwo.getId()) {
            // Connection exist
            return true
        }
    }
    // Connection doesn't exist
    return false
}
