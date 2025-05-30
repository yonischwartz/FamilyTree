package com.yoniSchwartz.YBMTree.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.MemberType
import com.yoniSchwartz.YBMTree.data.Relations
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager.getRelationBetweenMemberAndOneOfHisConnections
import com.yoniSchwartz.YBMTree.ui.CustomizedText
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.intToMachzor

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

    var displayGraphicConnection by remember { mutableStateOf(false) }

    val path = DatabaseManager.getShortestPathBetweenTwoMembers(firstMember, secondMember)

    val pathAsString = getPathAsAnnotatedString(path)
//    val pathAsString = getPathAsString(path)

    val title = getConnectionTitle(firstMember, secondMember)

    DialogWithButtons(
        title = title,
        onRightButtonClick = {
            val temp = firstMember
            firstMember = secondMember
            secondMember = temp
        },
        textForRightButton = HebrewText.SHOW_REVERSE_CONNECTION,
//        onLeftButtonClick = {displayGraphicConnection = true},
//        textForLeftButton = HebrewText.DISPLAY_CONNECTION_GRAPHICALLY,
        onDismiss = onDismiss,
        contentOfDialog = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                CustomizedText(
                    text = pathAsString,
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        stackButtonsVertically = true
    )

    if (displayGraphicConnection) {
        GraphicalConnectionDisplayDialog(
            title = getConnectionTitle(firstMember, secondMember),
            memberPath = path,
            onDismiss = onDismiss,
            onPreviousClick = { displayGraphicConnection = false }
        )
    }
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

/**
 * Constructs a title string describing the connection between two family members.
 *
 * @param memberOne The first [FamilyMember].
 * @param memberTwo The second [FamilyMember].
 * @return A [String] like "הקשר בין {memberOne} ל{memberTwo}" or
 *         "קשר בין {memberOne} ל{memberTwo}" if memberTwo is a rabbi.
 */
fun getConnectionTitle(memberOne: FamilyMember, memberTwo: FamilyMember): String {
    val memberTwoFullName = if (memberTwo.getIsRabbi()) {
        memberTwo.getFullName().removePrefix(HebrewText.THE)
    } else {
        memberTwo.getFullName()
    }

    return HebrewText.THE_CONNECTION_BETWEEN +
            " ${memberOne.getFullName()} " +
            HebrewText.TO +
            memberTwoFullName
}

/**
 * Constructs an annotated string representing the path between family members,
 * where each yeshiva member's name is displayed in red font.
 *
 * The function iterates through the given list of [FamilyMember] objects and
 * builds an [AnnotatedString] showing each member's full name and their
 * relationship to the next member. Names of yeshiva members are styled in red.
 *
 * @param pathAsList An ordered list of [FamilyMember] objects representing the connection path.
 * @return An [AnnotatedString] suitable for displaying stylized text in a Compose `Text` element.
 */
private fun getPathAsAnnotatedString(pathAsList: List<FamilyMember>): AnnotatedString {
    return buildAnnotatedString {
        val listLength = pathAsList.size
        var currentMemberIndex = 0

        while (currentMemberIndex < listLength) {
            val member = pathAsList[currentMemberIndex]
            val isYeshivaMember = member.getMemberType() == MemberType.Yeshiva

            // Style red for yeshiva members, default otherwise
            withStyle(
                style = SpanStyle(color = if (isYeshivaMember) Color.Red else Color.Unspecified)
            ) {
                // Member full name
                append(member.getFullName())

                // Add machzor information if the member is a yeshiva member
                if (isYeshivaMember) {
                    val machzor = member.getMachzor()
                    val machzorLabel = if (machzor == 0) {
                        HebrewText.STAFF
                    } else {
                        intToMachzor[machzor] ?: "${HebrewText.MACHZOR} $machzor"
                    }
                    append(" ($machzorLabel)")
                }
            }

            // Add relationship if not last member
            if (currentMemberIndex < listLength - 1) {
                val nextMember = pathAsList[currentMemberIndex + 1]
                val relation: Relations? = getRelationBetweenMemberAndOneOfHisConnections(nextMember, member)
                val relationString = relation?.displayAsRelation(member.getGender())
                val pronouns = if (member.getGender()) HebrewText.HE else HebrewText.SHE

                if (currentMemberIndex == 0) append(" ") else append(" ${HebrewText.THAT}")
                append("$pronouns $relationString ")
            }

            currentMemberIndex++
        }
    }
}


