package com.example.familytree.ui.theme

import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.platform.LocalContext
import com.example.familytree.data.Connection
import com.example.familytree.data.dataManagement.DatabaseManager.addConnectionToBothMembersInLocalMap
import com.example.familytree.data.dataManagement.DatabaseManager.addMemberIdToListOfNotYetUpdated
import com.example.familytree.data.dataManagement.DatabaseManager.addNewMemberToLocalMemberMap
import com.example.familytree.data.dataManagement.DatabaseManager.validateConnection
import com.example.familytree.data.exceptions.*
import com.example.familytree.ui.theme.dialogs.*

/**
 * Form fields for adding a Yeshiva family member.
 *
 */
@Composable
internal fun AskUserForYeshivaMemberDetails(
    memberType: MemberType,
    onFamilyMemberCreation: (FamilyMember) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var machzor by remember { mutableStateOf<Int?>(null) }
    var isRabbi by remember { mutableStateOf(false) }
    var isYeshivaRabbi by remember { mutableStateOf(false) }


    Column(modifier = Modifier.padding(16.dp)) {
        FreeTextField(
            text = HebrewText.FIRST_NAME,
            value = firstName,
            onValueChange = { firstName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        FreeTextField(
            text = HebrewText.LAST_NAME,
            value = lastName,
            onValueChange = { lastName = it }
        )

        // Only a yeshivaMember needs to enter a machzor
        if (memberType == MemberType.Yeshiva) {
            Spacer(modifier = Modifier.height(8.dp))
            MachzorInput(
                machzor = machzor,
                onMachzorChange = { machzor = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        BooleanSelection(
            label = HebrewText.IS_THIS_FAMILY_MEMBER_A_RABBI,
            selected = isRabbi,
            onChange = { isRabbi = it }
        )

        // Show this boolean selection only if the this member is a rabbi and he is a yeshivaMember
        if (isRabbi and (memberType == MemberType.Yeshiva)) {
            Spacer(modifier = Modifier.height(8.dp))
            BooleanSelection(
                label = HebrewText.IS_THIS_RABBI_A_YESHIVA_RABBI,
                selected = isYeshivaRabbi,
                onChange = { isYeshivaRabbi = it }
            )
        }
    }

    if (
        firstName != "" &&
        lastName != "" &&
        machzor != null
    ) {
        // Trigger callback with the newly created FamilyMember object
        onFamilyMemberCreation(
            FamilyMember(
                memberType = memberType,
                firstName = firstName,
                lastName = lastName,
                gender = true,
                machzor = machzor,
                isRabbi = isRabbi,
                isYeshivaRabbi = isYeshivaRabbi
            )
        )
    }
}

@Composable
internal fun AskUserForNonYeshivaMemberDetails(
    onFamilyMemberCreation: (FamilyMember) -> Unit
) {

    var firstName: String by remember { mutableStateOf("") }
    var lastName: String by remember { mutableStateOf("") }
    var gender: Boolean by remember { mutableStateOf(true) }
    var isRabbi by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        FreeTextField(
            text = HebrewText.FIRST_NAME,
            value = firstName,
            onValueChange = { firstName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        FreeTextField(
            text = HebrewText.LAST_NAME,
            value = lastName,
            onValueChange = { lastName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BooleanSelection (
            label = HebrewText.SEX,
            optionOne = HebrewText.MALE,
            optionTwo = HebrewText.FEMALE,
            selected = gender,
            onChange = { gender = it }
        )

        // Only a male can be a rabbi
        if (gender) {
            Spacer(modifier = Modifier.height(8.dp))
            BooleanSelection(
                label = HebrewText.IS_THIS_FAMILY_MEMBER_A_RABBI,
                selected = isRabbi,
                onChange = { isRabbi = it }
            )
        }
    }

    if (
        firstName != "" &&
        lastName != ""
    ) {
        // Callback with a new FamilyMember object
        onFamilyMemberCreation(
            FamilyMember(
                memberType = MemberType.NonYeshiva,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                isRabbi = isRabbi,
                machzor = null,
            )
        )
    }
}

/**
 * A reusable text input field for free-text entry.
 *
 * @param text The label describing the text field.
 * @param value The current text value entered by the user.
 * @param onValueChange Callback triggered when the text value changes.
 */
@Composable
private fun FreeTextField(
    text: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text) }
    )
}

/**
 * Provides radio buttons for selecting gender.
 *
 * @param gender The currently selected gender.
 * @param onGenderChange Callback for updating the gender.
 */
@Composable
private fun GenderSelection(
    gender: Boolean,
    onGenderChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Text(HebrewText.SEX)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = gender,
                    onClick = { onGenderChange(true) }
                )
                Text(HebrewText.MALE)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !gender,
                    onClick = { onGenderChange(false) }
                )
                Text(HebrewText.FEMALE)
            }
        }
    }
}

/**
 * Provides radio buttons for boolean selections.
 *
 * @param label The label describing the selection.
 * @param selected The current boolean value.
 * @param onChange Callback for updating the boolean value.
 */
@Composable
private fun BooleanSelection(
    label: String,
    optionOne: String = HebrewText.YES,
    optionTwo: String = HebrewText.NO,
    selected: Boolean = true,
    onChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(label, modifier = Modifier.padding(bottom = 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onChange(true) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(optionOne)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButton(
                    selected = !selected,
                    onClick = { onChange(false) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(optionTwo)
            }
        }
    }
}

/**
 * Provides a dropdown menu for selecting the machzor value.
 * The user can pick from a predefined set of options, and the selected option will be passed to the callback.
 *
 * @param machzor The currently selected machzor as a string.
 * @param onMachzorChange Callback to update the machzor selection. The selected machzor will be passed as a string.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MachzorInput(machzor: Int?,
                          onMachzorChange: (Int?) -> Unit) {

    // State to handle dropdown visibility and selected option
    var expanded by remember { mutableStateOf(false) }
    var selectedMachzor by remember { mutableStateOf(machzor) }

    // UI layout
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = intToMachzor[selectedMachzor] ?: "",
            onValueChange = {},
            label = { Text(HebrewText.MACHZOR) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .clickable { expanded = true }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allMachzorim.forEach { machzorOption ->
                DropdownMenuItem(
                    text = { Text(machzorOption) },
                    onClick = {
                        selectedMachzor = machzorToInt[machzorOption] ?: 0
                        onMachzorChange(selectedMachzor)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Finds a matching family member in the provided list based on the first name, last name, and machzor.
 *
 * @param firstName The first name of the family member to search for.
 * @param lastName The last name of the family member to search for.
 * @param machzor The machzor of the family member to search for, or null if machzor is not applicable.
 * @param members A list of existing family members to search within.
 * @return The first matching family member if found, or null if no match is found.
 */
private fun findMatchingMember(
    firstName: String, lastName: String, machzor: Int?, members: List<FamilyMember>
): FamilyMember? {
    return members.find {
        it.getFirstName() == firstName && it.getLastName() == lastName && it.getMachzor() == machzor
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
internal fun AskUserToCreateNewFamilyMember(
    onMemberCreation: (FamilyMember) -> Unit,
    existingMembers: List<FamilyMember>,
    memberToRelateTo: FamilyMember? = null,
    relation: Relations? = null,
    onDismiss: () -> Unit
) {

    var newMember: FamilyMember? by remember { mutableStateOf(null) }
    var selectedMemberType: MemberType? by remember { mutableStateOf<MemberType?>(null) }
    var didUserSelectMemberType: Boolean by remember { mutableStateOf(false) }
    var didUserEnterMembersDetails: Boolean by remember { mutableStateOf(false) }

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
        didUserSelectMemberType = false
        didUserEnterMembersDetails = false
    }

    val onDismissAndResetState: () -> Unit = {
        resetState()
        onDismiss()
    }

    // First step: user needs to select member type
    if (!didUserSelectMemberType) {
        // Display a dialog for selecting the type of member.
        MemberTypeSelectionDialog(
            onMemberTypeSelected = {
                selectedMemberType = it
                didUserSelectMemberType = true
            },
            onDismiss = onDismissAndResetState
        )
    }

    // Second step: user needs to enter members details
    else if (!didUserEnterMembersDetails) {
        // Display a dialog for entering member details based on the selected type.
        AskUserForMemberDetailsDialog(
            headLine = headLine,
            selectedMemberType = selectedMemberType,
            onFamilyMemberCreation = { member ->
                newMember = member
                didUserEnterMembersDetails = true
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

/**
 * Returns the inverse relationship based on the given relation and the genders of both members.
 *
 * @param relation The relationship to invert.
 * @param memberOneIsMale True if the first member is male, false if female.
 * @param memberTwoIsMale True if the second member is male, false if female.
 * @return The inverse relationship.
 */
private fun getInverseRelation(relation: Relations, memberOneIsMale: Boolean, memberTwoIsMale: Boolean): Relations {
    return when (relation) {
        Relations.FATHER -> if (memberOneIsMale) Relations.SON else Relations.DAUGHTER
        Relations.MOTHER -> if (memberOneIsMale) Relations.SON else Relations.DAUGHTER
        Relations.SON -> if (memberTwoIsMale) Relations.FATHER else Relations.MOTHER
        Relations.DAUGHTER -> if (memberTwoIsMale) Relations.FATHER else Relations.MOTHER
        Relations.GRANDMOTHER -> if (memberOneIsMale) Relations.GRANDSON else Relations.GRANDDAUGHTER
        Relations.GRANDFATHER -> if (memberOneIsMale) Relations.GRANDSON else Relations.GRANDDAUGHTER
        Relations.GRANDSON -> if (memberTwoIsMale) Relations.GRANDFATHER else Relations.GRANDMOTHER
        Relations.GRANDDAUGHTER -> if (memberTwoIsMale) Relations.GRANDFATHER else Relations.GRANDMOTHER
        Relations.SIBLINGS -> Relations.SIBLINGS
        Relations.COUSINS -> Relations.COUSINS
        Relations.MARRIAGE -> Relations.MARRIAGE
    }
}

/**
 * Private suspend function that adds a new family member to the database and updates the relationship
 * between the new member and the existing member. It creates the necessary connections in both members' adjacency lists.
 *
 * @param existingMember The existing family member to whom the new member will be related.
 * @param newMember The new family member being added.
 * @param relationFromExistingMemberPerspective The relation of the new member from the perspective of the existing member.
 * @param onDismiss A callback to dismiss the UI after the operation is completed.
 * @param context The context used for showing the Toast message if the operation fails.
 */
private suspend fun addNewMemberAndConnect(
    existingMember: FamilyMember,
    newMember: FamilyMember,
    relationFromExistingMemberPerspective: Relations,
    onDismiss: () -> Unit,
    context: Context
) {
    try {
        // Wait for the new member to be added to the tree
        addNewMemberToLocalMemberMap(newMember)

        // Get members' IDs
        val existingMemberId: String = existingMember.getId()
        val newMemberId: String = newMember.getId()

        // Get members' genders
        val existingMemberGender: Boolean = existingMember.getGender()
        val newMemberGender: Boolean = newMember.getGender()

        // Determine the relation from the new member's perspective
        val relationFromNewMemberPerspective =
            getInverseRelation(relationFromExistingMemberPerspective, existingMemberGender, newMemberGender)

        // Create connections for both the new member and the existing member
        val connectionForNewMember = Connection(existingMemberId, relationFromNewMemberPerspective)
        val connectionForExistingMember = Connection(newMemberId, relationFromExistingMemberPerspective)

        // Add connections to the database
//        addConnectionToLocalMemberMap(existingMemberId, connectionForExistingMember)
//        addConnectionToLocalMemberMap(newMemberId, connectionForNewMember)

        // Dismiss the UI after the operation is completed
        onDismiss()
    } catch (e: Exception) {
        // Show a failure message if the operation fails
        Toast.makeText(context, HebrewText.ERROR_ADDING_MEMBER, Toast.LENGTH_LONG).show()
        onDismiss()
    }
}

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
internal fun AddNewFamilyMemberToEmptyTree(
    existingMembers: List<FamilyMember>,
    onDismiss: () -> Unit
) {
    var newMember: FamilyMember? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    AskUserToCreateNewFamilyMember(
        onMemberCreation = { newMember = it },
        existingMembers = existingMembers,
        onDismiss = onDismiss,
    )

    if (newMember != null) {
        newMember?.let { member ->
            coroutineScope.launch {
                try {
                    addNewMemberToLocalMemberMap(member)
                    Toast.makeText(context, HebrewText.SUCCESS_ADDING_MEMBER, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, HebrewText.ERROR_ADDING_MEMBER, Toast.LENGTH_LONG).show()
                }
                onDismiss()
            }
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
fun AddNewMemberAndRelateToExistingMember(
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
            )) {isConnectionValid = true}
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


//var showToast by remember { mutableStateOf(true) }
//val context = LocalContext.current
//if (showToast) {
//    LaunchedEffect(Unit) {
//        Toast.makeText(context, "עקיבא פרגר!", Toast.LENGTH_SHORT).show()
//        showToast = false
//    }
//}
//
//Log.d("FamilyTreeApp", "com.example.familytree               D  עקיבא פרגר!")

