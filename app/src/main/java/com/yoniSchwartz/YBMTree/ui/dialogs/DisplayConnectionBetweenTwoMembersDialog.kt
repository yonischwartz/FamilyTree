package com.yoniSchwartz.YBMTree.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.Relations
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager.getRelationBetweenMemberAndOneOfHisConnections
import com.yoniSchwartz.YBMTree.ui.CustomizedText
import com.yoniSchwartz.YBMTree.ui.HebrewText

/**
 * Displays a dialog showing the shortest connection path between two yeshiva members.
 *
 * @param memberOne The first FamilyMember whose connections are being explored.
 * @param memberTwo The second FamilyMember to whom the connection is displayed.
 * @param onDismiss A lambda function to handle the dialog dismissal action.
 */
@Composable
fun DisplayConnectionBetweenTwoMembersDialog(
    memberOne: FamilyMember,
    memberTwo: FamilyMember,
    onDismiss: () -> Unit = {}
) {
    var firstMember by remember { mutableStateOf(memberOne) }
    var secondMember by remember { mutableStateOf(memberTwo) }

    val path = DatabaseManager.getShortestPathBetweenTwoMembers(firstMember, secondMember)
    val pathAsString = getPathAsString(path)

    val title: String = HebrewText.THE_CONNECTION_BETWEEN +
            " ${firstMember.getFullName()} " +
            HebrewText.TO +
            secondMember.getFullName()

    DialogWithButtons(
        title = title,
        onLeftButtonClick = onDismiss,
        textForLeftButton = HebrewText.OK,
        onRightButtonClick = {
            val temp = firstMember
            firstMember = secondMember
            secondMember = temp
        },
        textForRightButton = HebrewText.SHOW_REVERSE_CONNECTION,
        onDismiss = onDismiss,
        contentOfDialog = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                CustomizedText(text = pathAsString)
            }
        }
    )
}
/**
 * Constructs a descriptive string representing the path between family members.
 *
 * The function iterates through the list of family members (`pathAsList`) and builds a
 * string that shows each member's name along with the relationship to the next member.
 * The pronouns and relationship strings are generated in Hebrew using the `HebrewText`
 * and `Relations` classes.
 *
 * @param pathAsList A list of `FamilyMember` objects representing the path.
 *                   The list should be ordered to reflect the intended relationship path.
 * @return A `String` describing the full path between members, including names, pronouns,
 *         and relationships. If the list is empty, returns an empty string.
 */
private fun getPathAsString(pathAsList: List<FamilyMember>): String {

    var pathAsString = ""
    val listLength = pathAsList.size
    var currentMemberIndex = 0

    while (currentMemberIndex < listLength) {

        val member = pathAsList[currentMemberIndex]

        // Get member's pronouns in Hebrew
        val pronouns = if (member.getGender()) HebrewText.HE else HebrewText.SHE

        // Add the member's full name to the path string
        pathAsString += member.getFullName()

        // If not at the last member, add the relationship to the next member
        if (currentMemberIndex < listLength - 1) {

            // Retrieve the next member in the list
            val nextMember = pathAsList[currentMemberIndex + 1]

            // Determine the relationship between the current and the next member
            val relation: Relations? =
                getRelationBetweenMemberAndOneOfHisConnections(nextMember, member)

            // Convert the relation to a suitable Hebrew string
            val relationString = relation?.displayAsRelation(member.getGender())

            // Add a space or the Hebrew word for "that" before the relationship string
            if (currentMemberIndex == 0) { pathAsString += " " }
            else { pathAsString += " ${HebrewText.THAT}" }

            // Append the relationship and pronoun to the path string
            pathAsString += "$pronouns $relationString "
        }

        // Move to the next member in the list
        currentMemberIndex++
    }

    return pathAsString
}
