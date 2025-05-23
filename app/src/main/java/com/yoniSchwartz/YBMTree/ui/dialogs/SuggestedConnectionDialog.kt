package com.yoniSchwartz.YBMTree.ui.dialogs

import androidx.compose.runtime.Composable
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.FullConnection
import com.yoniSchwartz.YBMTree.data.Relations
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager
import com.yoniSchwartz.YBMTree.ui.HebrewText


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

    DialogWithButtons(
        title = title,
        text = "",
        onLeftButtonClick = {
            DatabaseManager.addConnectionToBothMembersInLocalMap(memberOne, memberTwo, relation)
            onDismiss()
        },
        textForLeftButton = HebrewText.YES,
        onRightButtonClick = userRejection,
        textForRightButton = HebrewText.NO
    )
}