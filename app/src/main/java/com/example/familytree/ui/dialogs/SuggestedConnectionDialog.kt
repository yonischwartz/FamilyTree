package com.example.familytree.ui.dialogs

import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FullConnection
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.HebrewText


/**
 * A dialog that suggests a potential family connection and asks the user to confirm or dismiss it.
 *
 * If the user confirms, the connection is added. If he rejects the suggestion and an alternative
 * connection exists, the alternative connection is automatically applied instead.
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

    // Get gender of memberTwo
    val memberTwoGender = suggestedConnection.memberTwo.getGender()

    // Retrieve names and relationship strings
    val pronouns = if (memberTwoGender) HebrewText.HE else HebrewText.SHE
    val memberOneName: String = suggestedConnection.memberOne.getFullName()
    val memberTwoName: String = suggestedConnection.memberTwo.getFullName()
    val relationString: String = suggestedConnection.relationship.displayAsRelation(memberTwoGender)

    // Construct the question title for the dialog
    val title: String =
        HebrewText.DOES + " " + memberTwoName + " " +
                pronouns + " " + relationString + " " + memberOneName + "?"

    // Define what happens when the user rejects the suggestion
    val userRejection: () -> Unit = if (suggestedConnection.alternativeConnection != null) {
        {
            // If an alternative connection exists, apply it automatically
            DatabaseManager.addConnectionToBothMembersInLocalMap(
                memberOne,
                memberTwo,
                suggestedConnection.alternativeConnection.relationship
            )
            onDismiss()
        }
    } else {
        {
            // Otherwise, simply close the dialog
            onDismiss()
        }
    }

    DialogWithTwoButtons(
        title = title,
        text = "",
        onClickForLeft = {
            DatabaseManager.addConnectionToBothMembersInLocalMap(memberOne, memberTwo, relation)
            onDismiss()
        },
        textForLeft = HebrewText.YES,
        onClickForRight = userRejection,
        textForRight = HebrewText.NO
    )
}