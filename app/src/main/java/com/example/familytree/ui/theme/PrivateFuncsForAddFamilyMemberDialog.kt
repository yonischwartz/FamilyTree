package com.example.familytree.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.familytree.data.Connection
import com.example.familytree.data.dataManagement.DatabaseManager.addConnectionToBothMembersInLocalMap
import com.example.familytree.data.dataManagement.DatabaseManager.addNewMemberToLocalMemberMap
import com.example.familytree.data.dataManagement.DatabaseManager.validateConnection
import com.example.familytree.data.exceptions.*
import com.example.familytree.ui.theme.dialogs.GenderErrorDialog
import com.example.familytree.ui.theme.dialogs.MoreThanOneConnectionErrorDialog
import com.example.familytree.ui.theme.dialogs.SameMemberMarriageErrorDialog

/**
 * Provides buttons for selecting the member type.
 *
 * @param onMemberTypeSelected Callback invoked when a member type is selected.
 */
@Composable
internal fun MemberTypeSelection(onMemberTypeSelected: (MemberType) -> Unit) {
    MemberTypeButton(
        label = HebrewText.YESHIVA_FAMILY_MEMBER,
        onClick = { onMemberTypeSelected(MemberType.Yeshiva) }
    )
    Spacer(modifier = Modifier.height(8.dp))
    MemberTypeButton(
        label = HebrewText.NON_YESHIVA_FAMILY_MEMBER,
        onClick = { onMemberTypeSelected(MemberType.NonYeshiva) }
    )
}

/**
 * Represents a button for selecting a member type.
 *
 * @param label The text to display on the button.
 * @param onClick Callback invoked when the button is clicked.
 */
@Composable
private fun MemberTypeButton(label: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(label)
    }
}

@Composable
private fun AskUserForMemberDetailsDialog(
    selectedMemberType: MemberType?,
    onFamilyMemberCreation: (FamilyMember) -> Unit,
    onDismiss: () -> Unit
) {
    var newMember by remember { mutableStateOf<FamilyMember?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(HebrewText.ADD_FAMILY_MEMBER) },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedMemberType) {
                    MemberType.Yeshiva -> {
                        AskUserForYeshivaMemberDetails { newMember = it }
                    }
                    MemberType.NonYeshiva -> {
                        AskUserForNonYeshivaMemberDetails { newMember = it }
                    }
                    else -> Unit
                }
            }
        },
        // can you make that the user will be able to push the confirmButton only if the new member isn't null?
        confirmButton = {
            TextButton(
                onClick = { newMember?.let(onFamilyMemberCreation) },
                enabled = newMember != null
            ) {
                Text(HebrewText.CONTINUE)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(HebrewText.CANCEL)
            }
        }
    )
}

/**
 * Form fields for adding a Yeshiva family member.
 *
 */
@Composable
internal fun AskUserForYeshivaMemberDetails(
    onFamilyMemberCreation: (FamilyMember) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var machzor by remember { mutableStateOf<Int?>(null) }
    var isRabbi by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        MemberFirstNameField(
            firstName = firstName,
            onFirstNameChange = { firstName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MemberLastNameField(
            lastName = lastName,
            onLastNameChange = { lastName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MachzorInput(
            machzor = machzor,
            onMachzorChange = { machzor = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BooleanSelection(
            label = HebrewText.IS_THIS_FAMILY_MEMBER_A_RABBI,
            selected = isRabbi,
            onChange = { isRabbi = it }
        )
    }

    if (
        firstName != "" &&
        lastName != "" &&
        machzor != null
    ) {
        // Trigger callback with the newly created FamilyMember object
        onFamilyMemberCreation(
            FamilyMember(
                memberType = MemberType.Yeshiva,
                firstName = firstName,
                lastName = lastName,
                gender = true,
                machzor = machzor,
                isRabbi = isRabbi
            )
        )
    }
}

/**
 * Form fields for adding a Non-Yeshiva family member.
 *
 * @param firstName The first name input.
 * @param lastName The last name input.
 * @param gender The gender input.
 * @param onFirstNameChange Callback for updating the first name.
 * @param onLastNameChange Callback for updating the last name.
 * @param onGenderChange Callback for updating the gender.
 */
@Composable
internal fun AskUserForNonYeshivaMemberDetails(
    onFamilyMemberCreation: (FamilyMember) -> Unit
) {

    var firstName: String by remember { mutableStateOf("") }
    var lastName: String by remember { mutableStateOf("") }
    var gender: Boolean by remember { mutableStateOf(true) }
    var isRabbi by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        MemberFirstNameField(
            firstName,
            onFirstNameChange = { firstName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MemberLastNameField(
            lastName,
            onLastNameChange = { lastName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        GenderSelection(
            gender,
            onGenderChange = { gender = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BooleanSelection(
            label = HebrewText.IS_THIS_FAMILY_MEMBER_A_RABBI,
            selected = isRabbi,
            onChange = { isRabbi = it }
        )
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
 * Provides input field for first name.
 *
 * @param firstName The first name input.
 * @param onFirstNameChange Callback for updating the first name.
 */
@Composable
private fun MemberFirstNameField(
    firstName: String,
    onFirstNameChange: (String) -> Unit
) {
    TextField(
        value = firstName,
        onValueChange = onFirstNameChange,
        label = { Text(HebrewText.FIRST_NAME) }
    )
}

/**
 * Provides input field for last name.
 *
 * @param lastName The last name input.
 * @param onLastNameChange Callback for updating the last name.
 */
@Composable
private fun MemberLastNameField(
    lastName: String,
    onLastNameChange: (String) -> Unit
) {
    TextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = { Text(HebrewText.LAST_NAME) }
    )
}

/**
 * Provides radio buttons for selecting gender.
 *
 * @param gender The currently selected gender.
 * @param onGenderChange Callback for updating the gender.
 */
@Composable
private fun GenderSelection(gender: Boolean, onGenderChange: (Boolean) -> Unit) {
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
private fun BooleanSelection(label: String, selected: Boolean, onChange: (Boolean) -> Unit) {
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
                Text(HebrewText.YES)
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
                Text(HebrewText.NO)
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
 * Displays a dialog to confirm if the user meant to select an existing family member.
 * Prompts the user to confirm or reject using the existing member.
 *
 * @param matchedMember The matched family member to display.
 * @param onConfirm Callback function to confirm the use of the existing member.
 * @param onDismiss Callback function to dismiss the dialog without taking action.
 */
@Composable
private fun SameMemberDialog(
    matchedMember: FamilyMember,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onConfirm) {
                    Text("כן, זה הוא")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDismiss) {
                    Text("לא, הוסף משתמש חדש")
                }
            }
        },
        dismissButton = {},
        text = { Text("כבר קיים בן משפחה בשם ${matchedMember.getFullName()}. האם התכוונת אליו?") }
    )
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
 * Composable function that allows the user to select a family member from a list to relate to
 * when adding a new family member. It displays a dropdown menu with the list of family members,
 * and the user can choose an existing family member to connect the new member to.
 *
 * @param existingMembers List of family members to display in the dropdown menu.
 * @param onMemberSelected Callback function that is triggered when a family member is selected.
 *        It returns the selected `FamilyMember` object.
 * @param onDismiss Callback function triggered when the dialog is dismissed without selection.
 */
@Composable
fun ChooseMemberToRelateTo(
    existingMembers: List<FamilyMember>,
    onMemberSelected: (FamilyMember) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            // Dismiss the dialog when requested
            onDismissRequest = {
                showDialog = false
                onDismiss()
           },
            title = {
                Text(text = HebrewText.NEW_FAMILY_MEMBERS_MUST_BE_RELATED_TO_AN_EXISTING_MEMBER)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),  // Padding around content
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display a prompt for selecting a family member
                    Text(
                        text = HebrewText.TO_WHICH_EXISTING_MEMBER_IS_YOUR_NEW_MEMBER_CONNECTED_TO,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Button to open the dropdown menu
                        TextButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Display the selected member's name or a default prompt
                            Text(
                                text = selectedMember?.getFullName() ?: HebrewText.CHOOSE_FAMILY_MEMBER,
                                fontSize = 16.sp
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // Create a dropdown item for each family member
                            existingMembers.forEach { member ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedMember = member
                                        expanded = false
                                    },
                                    text = {
                                        Text(text = member.getFullName())
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                // Proceed with the selected member
                Button(
                    onClick = {
                        selectedMember?.let {
                            onMemberSelected(it)
                            showDialog = false
                        }
                    },
                    enabled = selectedMember != null
                ) {
                    Text(text = HebrewText.CONTINUE)
                }
            },
            dismissButton = {
                // Handle dialog dismissal
                TextButton(
                    onClick = {
                        showDialog = false
                        onDismiss()
                    }
                ) {
                    Text(text = HebrewText.CANCEL)
                }
            }
        )
    }
}

/**
 * A composable function that displays a user interface for selecting the relation
 * between an existing family member and a new family member to be added using an AlertDialog.
 *
 * @param existingMember The FamilyMember object representing the existing family member.
 * @param onRelationSelected A callback function that is invoked with the selected relation
 * when the user clicks the "המשך" (Next) button.
 */
@Composable
private fun HowAreTheyRelated(
    existingMember: FamilyMember,
    onRelationSelected: (Relations) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRelation by remember { mutableStateOf<Relations?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            title = {
                Text(text = "כיצד ${existingMember.getFullName()} קשור לבן המשפחה החדש?")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Displays the question prompting the user to choose how the new member is related.
                    Text(
                        text = "בן המשפחה שאני רוצה להוסיף הוא",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Row for the button and selected relation to be on the same line
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = HebrewText.CHOOSE_RELATION)
                        }

                        // Displays the selected relation
                        selectedRelation?.let {
                            Text(text = it.displayName(), fontSize = 16.sp)
                        }
                    }

                    // Dropdown menu listing all relation options.
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        Relations.entries.forEach { relation ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedRelation = relation
                                    expanded = false
                                },
                                text = {
                                    Text(text = relation.displayName())
                                }
                            )
                        }
                    }
                    Text(
                        text = existingMember.getFullName(),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedRelation?.let {
                            onRelationSelected(it)
                            showDialog = false
                        }
                    },
                    enabled = selectedRelation != null
                ) {
                    Text(text = HebrewText.CONTINUE)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDismiss()
                }) {
                    Text(text = HebrewText.CANCEL)
                }
            }
        )
    }
}

/**
 * Provides Hebrew-friendly display names for Relations enum.
 * This function returns a string that represents the relationship between two family members.
 *
 * @return A string representing the relationship in Hebrew, such as "אבא של " for FATHER or "נכד של " for GRANDSON.
 */
internal fun Relations.displayName(): String {
    return when (this) {
        Relations.MARRIAGE -> HebrewText.MARRIED_TO
        Relations.FATHER -> HebrewText.FATHER_OF
        Relations.MOTHER -> HebrewText.MOTHER_OF
        Relations.SON -> HebrewText.SON_OF
        Relations.DAUGHTER -> HebrewText.DAUGHTER_OF
        Relations.GRANDMOTHER -> HebrewText.GRANDMOTHER_OF
        Relations.GRANDFATHER -> HebrewText.GRANDFATHER_OF
        Relations.GRANDSON -> HebrewText.GRANDSON_OF
        Relations.GRANDDAUGHTER -> HebrewText.GRANDDAUGHTER_OF
        Relations.COUSINS -> HebrewText.COUSIN_OF
        Relations.SIBLINGS -> HebrewText.SIBLING_OF
    }
}

/**
 * A composable function that displays a dialog for selecting the member type.
 *
 * @param onMemberTypeSelected A lambda function triggered when a member type is selected.
 * @param onDismiss A lambda function triggered when the dialog is dismissed.
 */
@Composable
private fun MemberTypeSelectionDialog(
    onMemberTypeSelected: (MemberType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(HebrewText.CHOOSE_FAMILY_MEMBER_TYPE, textAlign = TextAlign.End) },
        text = {
            Column {
                MemberTypeSelection(onMemberTypeSelected = onMemberTypeSelected)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(HebrewText.CANCEL)
            }
        }
    )
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
    onDismiss: () -> Unit
) {
    // Holds the newly created family member.
    var newMember: FamilyMember? by remember { mutableStateOf(null) }

    // Holds the selected type of the member (Yeshiva or NonYeshiva).
    var selectedMemberType: MemberType? by remember { mutableStateOf<MemberType?>(null) }

    // Indicates whether the user has selected a member type.
    var didUserSelectMemberType: Boolean by remember { mutableStateOf(false) }

    // Indicates whether the user has entered the member's details.
    var didUserEnterMembersDetails: Boolean by remember { mutableStateOf(false) }


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
            selectedMemberType,
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
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddNewMemberAndRelateToExistingMember(
    existingMembers: List<FamilyMember>,
    onDismiss: () -> Unit
) {
    var existingMember: FamilyMember? by remember { mutableStateOf(null) }
    var newMember: FamilyMember? by remember { mutableStateOf(null) }
    var relationFromExistingMemberPerspective: Relations? by remember { mutableStateOf(null) }
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
        showGenderErrorDialog = false
        showMoreThanOneMemberErrorDialog = false
        showSameMemberMarriageErrorDialog = false
        isConnectionValid = false
    }

    val onDismissAndResetState: () -> Unit = {
        resetState()
        onDismiss()
    }

    // First step: select an existing member in the tree to connect to
    if (existingMember == null) {

        ChooseMemberToRelateTo(
            existingMembers = existingMembers,
            onMemberSelected = { existingMember = it },
            onDismiss = onDismissAndResetState
        )

    }

    // Second step: select the relation between the new member, and the existing member
    else if (relationFromExistingMemberPerspective == null) {

        HowAreTheyRelated(
            existingMember = existingMember!!,
            onRelationSelected = { relationFromExistingMemberPerspective = it },
            onDismiss = onDismissAndResetState
        )
    }

    // Third step: create a new FamilyMember object representing the new member
    else if (newMember == null) {

        AskUserToCreateNewFamilyMember(
            onMemberCreation = { newMember = it },
            existingMembers = existingMembers,
            onDismiss = onDismissAndResetState
        )
    }

    // Fourth step: Make sure the connection the user wants to add is valid
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

    // Fifth step: add the new member and update thr connection in both members
    else {

        val newMemberAdded = addNewMemberToLocalMemberMap(newMember!!)
        val connectionAdded = addConnectionToBothMembersInLocalMap(existingMember!!, newMember!!, relationFromExistingMemberPerspective!!)

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

