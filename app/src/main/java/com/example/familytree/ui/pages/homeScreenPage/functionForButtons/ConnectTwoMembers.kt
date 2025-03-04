package com.example.familytree.ui.pages.homeScreenPage.functionForButtons

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.familytree.data.Connection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.data.dataManagement.DatabaseManager.addConnectionToBothMembersInLocalMap
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.dialogs.ChooseMemberToRelateToDialog
import com.example.familytree.ui.dialogs.HowAreTheyRelatedDialog

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
 * @param onDismiss A callback function to execute when the process is canceled or completed.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ConnectTwoMembers(
    onDismiss:() -> Unit,
    givenFirstMember: FamilyMember? = null
) {

    var memberOne: FamilyMember? by remember { mutableStateOf(givenFirstMember) }
    var memberTwo by remember { mutableStateOf<FamilyMember?>(null) }
    var relationFromMemberOnePerspective by remember { mutableStateOf<Relations?>(null) }
    var expectedGenderOfMemberTwo: Boolean by remember { mutableStateOf(true) }
    var wasConnectionAdded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Reset the state variables to their initial values
    val resetState: () -> Unit = {
        memberOne = null
        memberTwo = null
        relationFromMemberOnePerspective = null
        expectedGenderOfMemberTwo = true
        wasConnectionAdded = false
    }

    val onDismissAndResetState: () -> Unit = {
        resetState()
        onDismiss()
    }

    // If existing member is given, onPrevious should be defined differently
    val onPreviousForStepTwo = if (givenFirstMember != null) {
        onDismiss
    } else {
        { memberOne = null }
    }

    // First step: choose first member
    if (memberOne == null) {
        ChooseMemberToRelateToDialog(
            onMemberSelected = { memberOne = it },
            showPreviousButton = false,
            onDismiss = onDismissAndResetState
        )
    }

    // Second step: choose the relation between the members
    else if (relationFromMemberOnePerspective == null) {

        HowAreTheyRelatedDialog(
            existingMember = memberOne ?: FamilyMember(),
            onRelationSelected = { relation, gender ->
                relationFromMemberOnePerspective = relation
                expectedGenderOfMemberTwo = gender
            },
            onPrevious = onPreviousForStepTwo,
            onDismiss = onDismissAndResetState
        )
    }

    // Third step: choose second member
    else if (memberTwo == null) {

        // Get all members that might be related to memberOne
        val optionalMembersToConnectTo =
            getListOfOptionalMembersToConnectTo(
                member = memberOne!!,
                expectedGender = expectedGenderOfMemberTwo
            )

        ChooseMemberToRelateToDialog(
            listOfMembersToConnectTo = optionalMembersToConnectTo,
            onMemberSelected = { memberTwo = it },
            showPreviousButton = true,
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
 * @param relationFromMembersPerspective The relationship type being established from the given member's perspective.
 * @param member The family member for whom we are finding possible connections.
 * @return A filtered list of `FamilyMember` objects who can be connected to the given `member`.
 */
private fun getListOfOptionalMembersToConnectTo(
    member: FamilyMember,
    expectedGender: Boolean
): List<FamilyMember> {

    // Ids of members already connected to memberOne
    val connectionsOfMemberOne: List<Connection> = member.getConnections()
    val idsOfMembersConnectedToMemberOne = connectionsOfMemberOne.map { it.memberId }

    // Id of memberOne and ids of members connected to him shouldn't be displayed
    var optionalMembersToConnectTo = DatabaseManager.getAllMembers().filter {
        it.getId() != member.getId() && it.getId() !in idsOfMembersConnectedToMemberOne
    }

    optionalMembersToConnectTo = optionalMembersToConnectTo.filter {
            it.getGender() == expectedGender
        }

    return optionalMembersToConnectTo
}