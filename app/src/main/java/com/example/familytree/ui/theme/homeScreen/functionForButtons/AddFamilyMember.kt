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
import com.example.familytree.data.dataManagement.DatabaseManager.addNewMemberToLocalMemberMap
import com.example.familytree.data.dataManagement.DatabaseManager.validateConnection
import com.example.familytree.data.exceptions.InvalidGenderRoleException
import com.example.familytree.data.exceptions.InvalidMoreThanOneConnection
import com.example.familytree.data.exceptions.SameSexMarriageException
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.AskUserForMemberDetailsDialog
import com.example.familytree.ui.theme.dialogs.ChooseMemberToRelateToDialog
import com.example.familytree.ui.theme.dialogs.GenderErrorDialog
import com.example.familytree.ui.theme.dialogs.HowAreTheyRelatedDialog
import com.example.familytree.ui.theme.dialogs.ChooseMemberTypeDialog
import com.example.familytree.ui.theme.dialogs.GenericMessageDialogWithOneButton
import com.example.familytree.ui.theme.dialogs.GenericMessageDialogWithTwoButtons
import com.example.familytree.ui.theme.dialogs.MoreThanOneConnectionErrorDialog
import com.example.familytree.ui.theme.dialogs.NewMemberMustBeRelatedDialog
import com.example.familytree.ui.theme.dialogs.SameMemberMarriageErrorDialog

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
        if (addNewMemberToLocalMemberMap(newMember!!)) {
            GenericMessageDialogWithOneButton(
                title = HebrewText.SUCCESS_ADDING_MEMBER,
                text = newMember!!.getFullName() + " " + HebrewText.WAS_ADDED_SUCCESSFULLY,
                onDismiss = onDismiss
            )
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
    var wasMemberAdded by remember { mutableStateOf(false) }
    var AreSuggesstionsFinished by remember { mutableStateOf(false) }

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
        wasMemberAdded = false
        AreSuggesstionsFinished = false
    }

    val onDismissAndResetState: () -> Unit = {
        resetState()
        onDismiss()
    }

    // Step 1: inform user he must relate to an existing member
    if (!wasUserInformed) {
        NewMemberMustBeRelatedDialog(
            onConfirm = {wasUserInformed = true},
            onDismiss = onDismissAndResetState
        )
    }

    // Step 2: select an existing member in the tree to connect to
    else if (existingMember == null) {

        ChooseMemberToRelateToDialog(
            existingMembers = existingMembers,
            onMemberSelected = { existingMember = it },
            onPrevious = {wasUserInformed = false},
            onDismiss = onDismissAndResetState
        )
    }

    // Step 3: select the relation between the new member, and the existing member
    else if (relationFromExistingMemberPerspective == null) {

        HowAreTheyRelatedDialog(
            existingMember = existingMember!!,
            onRelationSelected = { relationFromExistingMemberPerspective = it },
            onPrevious = { existingMember = null },
            onDismiss = onDismissAndResetState
        )
    }

    // Step 4: create a new FamilyMember object representing the new member
    else if (newMember == null) {

        AskUserToCreateNewFamilyMember(
            onMemberCreation = { newMember = it },
            showPreviousButton = true,
            onPreviousForStepOne = { relationFromExistingMemberPerspective = null },
            onPreviousForStepTwo = {relationFromExistingMemberPerspective = null},
            existingMembers = existingMembers,
            memberToRelateTo = existingMember!!,
            relation = relationFromExistingMemberPerspective,
            onDismiss = onDismissAndResetState
        )
    }

    // Step 5: Make sure the connection the user wants to add is valid
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
        catch (e: SameSexMarriageException) {
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

    // Step 6: add the new member and update the connection in both members
    else if (wasMemberAdded.not()){

        // Add new member to local map
        val newMemberAdded = addNewMemberToLocalMemberMap(newMember!!)

        // Add connections between two members
        val connectionAdded = addConnectionToBothMembersInLocalMap(existingMember!!, newMember!!, relationFromExistingMemberPerspective!!)

        if (newMemberAdded && connectionAdded) {
            Toast.makeText(context, HebrewText.SUCCESS_ADDING_MEMBER, Toast.LENGTH_LONG).show()
            wasMemberAdded = true
        }
        else {
            Toast.makeText(context, HebrewText.ERROR_ADDING_MEMBER, Toast.LENGTH_LONG).show()
        }
    }

    // Step 7: offer user to add suggested connections
    else if (AreSuggesstionsFinished.not()) {
        SuggestConnections({ AreSuggesstionsFinished = true })
    }

    // Step 8: Inform the user, the member was added successfully, and then dismiss
    else {

        GenericMessageDialogWithOneButton(
            title = HebrewText.SUCCESS_ADDING_MEMBER,
            text = newMember!!.getFullName() + " " + HebrewText.WAS_ADDED_SUCCESSFULLY,
            onDismiss = onDismissAndResetState
        )
    }
}

/**
 * A Composable function that guides the user through the process of creating a new family member.
 * The function consists of multiple steps:
 * 1. Selecting the type of member (if applicable).
 * 2. Entering the member's details.
 * 3. Creating the new member and returning it via [onMemberCreation].
 *
 * @param onMemberCreation Callback triggered when a new [FamilyMember] is successfully created.
 * @param showPreviousButton Boolean flag indicating whether the previous button should be shown.
 * @param onPreviousForStepOne Callback triggered when the user goes back from the member type selection step.
 * @param onPreviousForStepTwo Callback triggered when the user goes back from the member details entry step.
 * @param existingMembers List of existing [FamilyMember]s to check for duplicates.
 * @param memberToRelateTo Optional [FamilyMember] to establish a relationship with the new member.
 * @param relation Optional [Relations] enum representing the relationship between the new member and [memberToRelateTo].
 * @param onDismiss Callback triggered when the process is dismissed.
 */
@Composable
private fun AskUserToCreateNewFamilyMember(
    onMemberCreation: (FamilyMember) -> Unit,
    showPreviousButton: Boolean = false,
    onPreviousForStepOne: () -> Unit = {},
    onPreviousForStepTwo: () -> Unit = {},
    existingMembers: List<FamilyMember>,
    memberToRelateTo: FamilyMember? = null,
    relation: Relations? = null,
    onDismiss: () -> Unit
) {

    var newMember: FamilyMember? by remember { mutableStateOf(null) }
    var selectedMemberType: MemberType? by remember { mutableStateOf(null) }

    // onPreviousForStepTwo should be defined differently if user needs to select a memberType.
    // Define a new lambda so it could be modified later
    var onPreviousForStepTwoModified by remember {
        mutableStateOf(onPreviousForStepTwo)
    }

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
    if (relation != null && relation.expectedGender(memberToRelateTo!!.getGender()) == false) {
        selectedMemberType = MemberType.NonYeshiva
    }

    // Step 1: User selects member type
    if (selectedMemberType == null) {
        // Display a dialog for selecting the type of member.
        ChooseMemberTypeDialog(
            onMemberTypeSelected = { selectedMemberType = it },
            showPreviousButton = showPreviousButton,
            onPrevious = onPreviousForStepOne,
            onDismiss = onDismissAndResetState
        )

        // If user needs to select a member type, the onPreviousForStepTwo should change
        onPreviousForStepTwoModified = {selectedMemberType = null}
    }

    // Step 2: User enters member details
    else if (newMember == null) {

        // Display a dialog for entering member details based on the selected type.
        AskUserForMemberDetailsDialog(
            headLine = headLine,
            expectedGender = relation?.expectedGender(memberToRelateTo!!.getGender()),
            selectedMemberType = selectedMemberType,
            onFamilyMemberCreation = { member -> newMember = member },
            onPrevious = onPreviousForStepTwoModified,
            onDismiss = onDismissAndResetState
        )
    }

    // Step 3: Member is created and returned.
    else {
        val memberExists = existingMembers.any {
            it.getFullName() == newMember?.getFullName() && it.getMachzor() == newMember?.getMachzor()
        }
        if (memberExists) {
            // Show a dialog to ask the user if they still want to add the new member
            GenericMessageDialogWithTwoButtons(
                title = HebrewText.MEMBER_ALREADY_EXISTS,
                text = HebrewText.DO_YOU_WANT_TO_ADD_ANYWAY,
                onClick = { newMember?.let { onMemberCreation(it) } },
                textForOnClick = HebrewText.YES,
                onDismiss = onDismissAndResetState,
                textForOnDismiss = HebrewText.NO
            )
        }
    }
}
