package com.example.familytree.ui.theme.homeScreen.functionForButtons

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.DatabaseManager.addConnectionToBothMembersInLocalMap
import com.example.familytree.data.dataManagement.DatabaseManager.addMemberIdToListOfNotYetUpdated
import com.example.familytree.data.dataManagement.DatabaseManager.addNewMemberToLocalMemberMap
import com.example.familytree.data.dataManagement.DatabaseManager.validateConnection
import com.example.familytree.data.exceptions.InvalidGenderRoleException
import com.example.familytree.data.exceptions.InvalidMoreThanOneConnection
import com.example.familytree.data.exceptions.SameMarriageException
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.AskUserForMemberDetailsDialog
import com.example.familytree.ui.theme.dialogs.ChooseMemberToRelateToDialog
import com.example.familytree.ui.theme.dialogs.GenderErrorDialog
import com.example.familytree.ui.theme.dialogs.HowAreTheyRelatedDialog
import com.example.familytree.ui.theme.dialogs.MemberTypeSelectionDialog
import com.example.familytree.ui.theme.dialogs.MoreThanOneConnectionErrorDialog
import com.example.familytree.ui.theme.dialogs.NewMemberMustBeRelatedDialog
import com.example.familytree.ui.theme.dialogs.SameMemberMarriageErrorDialog
import kotlinx.coroutines.launch

/**
 * A composable function that displays a dialog for adding a family member to the family tree.
 *
 * @param onDismiss Callback to handle dialog dismissal.
 * @param existingMembers A list of existing family members.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddFamilyMember(
    existingMembers: List<FamilyMember>,
    onDismiss: () -> Unit
) {

    if (existingMembers.isEmpty()) {
        // Add the first family member of the tree
        AddNewFamilyMemberToEmptyTree(
            existingMembers = existingMembers,
            onDismiss = onDismiss
        )

    } else {

        AddNewMemberAndRelateToExistingMember(
            existingMembers = existingMembers,
            onDismiss = onDismiss
        )
    }
}

// Private functions

/**
 * Composable function for adding a new family member when starting with an empty family tree.
 * This function provides a UI that prompts the user to create a new family member and adds
 * the member to the family tree once created.
 *
 * @param existingMembers A list of already existing family members to prevent duplicate entries.
 *                        It is passed to ensure uniqueness of the new member's information.
 * @param onDismiss A lambda function to handle the dismiss action, allowing the user to close
 *                  the UI without adding a new family member.
 */
@Composable
private fun AddNewFamilyMemberToEmptyTree(
    existingMembers: List<FamilyMember>,
    onDismiss: () -> Unit
) {
    var newMember: FamilyMember? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    AskUserToCreateNewFamilyMember(
        onMemberCreation = { newMember = it },
        existingMembers = existingMembers,
        onDismiss = onDismiss,
    )

    if (newMember != null) {
        newMember?.let { member ->
            addNewMemberToLocalMemberMap(member)
            Toast.makeText(context, HebrewText.SUCCESS_ADDING_MEMBER, Toast.LENGTH_LONG).show()
            onDismiss()
        }
    }
}

/**
 * Composable function for adding a new family member and relating them to an existing member.
 * This function guides the user through the process of selecting an existing member, choosing
 * the relationship, creating the new member, and validating the connection before adding the new member.
 *
 * @param existingMembers A list of existing family members to choose from when selecting an existing member.
 * @param onDismiss A callback function to handle dismissing the dialog or UI component when the process is canceled or completed.
 */
@Composable
private fun AddNewMemberAndRelateToExistingMember(
    existingMembers: List<FamilyMember>,
    onDismiss: () -> Unit
) {
    var existingMember: FamilyMember? by remember { mutableStateOf(null) }
    var newMember: FamilyMember? by remember { mutableStateOf(null) }
    var relationFromExistingMemberPerspective: Relations? by remember { mutableStateOf(null) }
    var wasUserInformed by remember { mutableStateOf(false) }
    var showGenderErrorDialog by remember { mutableStateOf(false) }
    var showMoreThanOneMemberErrorDialog by remember { mutableStateOf(false) }
    var showSameMemberMarriageErrorDialog by remember { mutableStateOf(false) }
    var isConnectionValid by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Reset the state variables to their initial values
    val resetState: () -> Unit = {
        existingMember = null
        newMember = null
        relationFromExistingMemberPerspective = null
        wasUserInformed = false
        showGenderErrorDialog = false
        showMoreThanOneMemberErrorDialog = false
        showSameMemberMarriageErrorDialog = false
        isConnectionValid = false
    }

    val onDismissAndResetState: () -> Unit = {
        resetState()
        onDismiss()
    }

    // First step: inform user he must relate to an existing member
    if (!wasUserInformed) {
        NewMemberMustBeRelatedDialog(
            onConfirm = {wasUserInformed = true},
            onDismiss = onDismissAndResetState
        )
    }

    // Second step: select an existing member in the tree to connect to
    else if (existingMember == null) {

        ChooseMemberToRelateToDialog(
            existingMembers = existingMembers,
            onMemberSelected = { existingMember = it },
            onPrevious = {wasUserInformed = false},
            onDismiss = onDismissAndResetState
        )
    }

    // Third step: select the relation between the new member, and the existing member
    else if (relationFromExistingMemberPerspective == null) {

        HowAreTheyRelatedDialog(
            existingMember = existingMember!!,
            onRelationSelected = { relationFromExistingMemberPerspective = it },
            onPrevious = { existingMember = null },
            onDismiss = onDismissAndResetState
        )
    }

    // Forth step: create a new FamilyMember object representing the new member
    else if (newMember == null) {

        AskUserToCreateNewFamilyMember(
            onMemberCreation = { newMember = it },
            existingMembers = existingMembers,
            memberToRelateTo = existingMember!!,
            relation = relationFromExistingMemberPerspective,
            onDismiss = onDismissAndResetState
        )
    }

    // Fifth step: Make sure the connection the user wants to add is valid
    else if (!isConnectionValid) {

        try {
            if (validateConnection(
                    existingMember!!,
                    newMember!!,
                    relationFromExistingMemberPerspective!!
                )
            ) {isConnectionValid = true}
        }
        catch (e: InvalidGenderRoleException) {
            showGenderErrorDialog = true
        }
        catch (e: InvalidMoreThanOneConnection) {
            showMoreThanOneMemberErrorDialog = true
        }
        catch (e: SameMarriageException) {
            showSameMemberMarriageErrorDialog = true
        }
        catch (e: Exception) {

            // Show a toast message
            Toast.makeText(context, HebrewText.ERROR_ADDING_MEMBER, Toast.LENGTH_LONG).show()

            // Handle the error by dismissing and resetting state
            onDismissAndResetState()

        }

        // Gender mismatch
        if (showGenderErrorDialog) {
            GenderErrorDialog(
                onDismiss = onDismissAndResetState,
                relationFromExistingMemberPerspective!!,
                newMember!!,
                existingMember!!
            )
        }

        // Two fathers, two mothers, or two marriage
        if (showMoreThanOneMemberErrorDialog) {
            MoreThanOneConnectionErrorDialog(
                onDismiss = onDismissAndResetState,
                relationFromExistingMemberPerspective!!,
                existingMember!!
            )
        }

        // Same gender marriage
        if (showSameMemberMarriageErrorDialog) {
            SameMemberMarriageErrorDialog(onDismiss = onDismissAndResetState)
        }
    }

    // Sixth step: add the new member and update thr connection in both members
    else {

        // Add new member to local map
        val newMemberAdded = addNewMemberToLocalMemberMap(newMember!!)

        // Add connections between two members
        val connectionAdded = addConnectionToBothMembersInLocalMap(existingMember!!, newMember!!, relationFromExistingMemberPerspective!!)

        // Add the id of the existing member to list of ids that need to be updated
        addMemberIdToListOfNotYetUpdated(existingMember!!.getId())

        if (newMemberAdded && connectionAdded) {
            Toast.makeText(context, HebrewText.SUCCESS_ADDING_MEMBER, Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(context, HebrewText.ERROR_ADDING_MEMBER, Toast.LENGTH_LONG).show()
        }

        onDismissAndResetState()
    }
}

/**
 * A composable function that displays a series of dialogs to guide the user in creating a new family member
 *
 * This function handles a step-by-step process:
 * 1. The user selects the type of member (Yeshiva or NonYeshiva).
 * 2. The user enters the details of the family member.
 *
 * @param existingMembers A list of existing family members used for validation or reference.
 * @param onMemberCreation A lambda function invoked when a new family member is successfully created, passing the new FamilyMember object.
 * @param onDismiss A lambda function that is called to dismiss the dialog when the user cancels the action.
 */
@Composable
private fun AskUserToCreateNewFamilyMember(
    onMemberCreation: (FamilyMember) -> Unit,
    existingMembers: List<FamilyMember>,
    memberToRelateTo: FamilyMember? = null,
    relation: Relations? = null,
    onDismiss: () -> Unit
) {

    var newMember: FamilyMember? by remember { mutableStateOf(null) }
    var selectedMemberType: MemberType? by remember { mutableStateOf<MemberType?>(null) }

    // If it's a marriage relation, gender will determine weather it's a wife or a husband
    val gender = memberToRelateTo?.getGender()

    // Headline for AskUserForMemberDetailsDialog
    val headLine = if (memberToRelateTo == null || relation == null) {
        HebrewText.ADD_FAMILY_MEMBER
    } else {
        HebrewText.ENTER_DETAILS_FOR + " " + HebrewText.THE +
                relation.displayAsConnections(!gender!!) +
                memberToRelateTo.getFullName()
    }

    val resetState: () -> Unit = {
        newMember = null
        selectedMemberType = null
    }

    val onDismissAndResetState: () -> Unit = {
        resetState()
        onDismiss()
    }

    // Females can't be of YeshivaMember type
    if (relation != null && relation.expectedGender() == false) {
        selectedMemberType = MemberType.NonYeshiva
    }

    // relation.expectedGender() doesn't handle marriage, so these lines check if member is a wife
    if (memberToRelateTo != null &&
        relation == Relations.MARRIAGE &&
        memberToRelateTo.getGender()
        ) {
        selectedMemberType = MemberType.NonYeshiva
    }

    // First step: user needs to select member type
    if (selectedMemberType == null) {
        // Display a dialog for selecting the type of member.
        MemberTypeSelectionDialog(
            onMemberTypeSelected = {
                selectedMemberType = it
            },
            onDismiss = onDismissAndResetState
        )
    }

    // Second step: user needs to enter members details
    else if (newMember == null) {
        // Display a dialog for entering member details based on the selected type.
        AskUserForMemberDetailsDialog(
            headLine = headLine,
            selectedMemberType = selectedMemberType,
            onFamilyMemberCreation = { member ->
                newMember = member
            },
            onDismiss = onDismissAndResetState
        )

        //TODO: Check if there's already a member with this name

    }

    // Third step: If both steps are complete, crate new member and return it.
    else {
        newMember?.let { onMemberCreation(it) }
    }
}
