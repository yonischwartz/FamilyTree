package com.example.familytree.ui.theme.homeScreen.functionForButtons

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FullConnection
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.data.dataManagement.DatabaseManager.addConnectionToBothMembersInLocalMap
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.ChooseMemberToRelateToDialog
import com.example.familytree.ui.theme.dialogs.HowAreTheyRelatedDialog
import com.example.familytree.ui.theme.dialogs.SuggestConnectionDialog

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
    var wasConnectionAdded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Reset the state variables to their initial values
    val resetState: () -> Unit = {
        memberOne = null
        memberTwo = null
        relationFromMemberOnePerspective = null
        wasConnectionAdded = false
    }

    val onDismissAndResetState: () -> Unit = {
        resetState()
        onDismiss()
    }

    // First step: choose first member
    if (memberOne == null) {
        ChooseMemberToRelateToDialog(
            existingMembers = existingMembers,
            onMemberSelected = { memberOne = it },
            showPreviousButton = false,
            onDismiss = onDismissAndResetState
        )
    }

    // Second step: choose the relation between the members
    else if (relationFromMemberOnePerspective == null) {

        HowAreTheyRelatedDialog(
            existingMember = memberOne ?: FamilyMember(),
            onRelationSelected = { relationFromMemberOnePerspective = it },
            onPrevious = { memberOne = null },
            onDismiss = onDismissAndResetState
        )
    }

    // Third step: choose second member
    else if (memberTwo == null) {

        // Get all members that might be related to memberOne
        val optionalMembersToConnectTo =
            getListOfOptionalMembersToConnectTo(
                existingMembers = existingMembers,
                member = memberOne!!,
                relationFromMembersPerspective = relationFromMemberOnePerspective!!
            )

        ChooseMemberToRelateToDialog(
            existingMembers = optionalMembersToConnectTo,
            onMemberSelected = { memberTwo = it },
            onPrevious = { relationFromMemberOnePerspective = null },
            onDismiss = onDismissAndResetState
        )
    }

    // Fourth step: add the connection
    else if (wasConnectionAdded.not()) {

        if (addConnectionToBothMembersInLocalMap(
                memberOne = memberOne ?: FamilyMember(),
                memberTwo = memberTwo ?: FamilyMember(),
                relationFromMemberOnePerspective = relationFromMemberOnePerspective
                    ?: Relations.SON // arbitrary relation
            )
        ) {
            // Success message
            Toast.makeText(context, HebrewText.SUCCESS_ADDING_CONNECTION, Toast.LENGTH_SHORT).show()

            wasConnectionAdded = true
        }
    }

    // Fifth step: offer user to add suggested connections
    else {
        SuggestConnections(onDismissAndResetState)
    }
}

/**
 * Retrieves a filtered list of family members who can be connected to the given member
 * based on relationship constraints.
 *
 * The function ensures:
 * 1. The given member (`member`) is not included in the list.
 * 2. Members already connected to `member` are excluded.
 * 3. If the relationship requires a specific gender, only members matching that gender are included.
 *
 * @param existingMembers The list of all available family members.
 * @param relationFromMembersPerspective The relationship type being established from the given member's perspective.
 * @param member The family member for whom we are finding possible connections.
 * @return A filtered list of `FamilyMember` objects who can be connected to the given `member`.
 */
private fun getListOfOptionalMembersToConnectTo(
    existingMembers: List<FamilyMember>,
    member: FamilyMember,
    relationFromMembersPerspective: Relations
): List<FamilyMember> {

    // Ids of members already connected to memberOne
    val connectionsOfMemberOne: List<Connection> = member.getConnections()
    val idsOfMembersConnectedToMemberOne = connectionsOfMemberOne.map { it.memberId }

    // Id of memberOne and ids of members connected to him shouldn't be displayed
    var optionalMembersToConnectTo = existingMembers.filter {
        it.getId() != member.getId() && it.getId() !in idsOfMembersConnectedToMemberOne
    }

    // Find expected gender
    val expectedGender: Boolean? = if (relationFromMembersPerspective == Relations.MARRIAGE) {
        member.getGender().not()
    }
    else {
        relationFromMembersPerspective.expectedGender()
    }

    // Remove from optionalMembersToConnectTo members with gender mismatch
    if (expectedGender != null) {
        optionalMembersToConnectTo = optionalMembersToConnectTo.filter {
            it.getGender() == expectedGender
        }
    }

    return optionalMembersToConnectTo
}