package com.example.familytree.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp



/**
 * Displays the title of the dialog.
 *
 * @param selectedMemberType The currently selected type of family member, or null if none.
 */
@Composable
internal fun AddMemberDialogTitle(selectedMemberType: MemberType?) {
    if (selectedMemberType == null) {
        Text("בחר סוג בן משפחה", style = MaterialTheme.typography.titleMedium)
    }
}

/**
 * Provides buttons for selecting the member type.
 *
 * @param onMemberTypeSelected Callback invoked when a member type is selected.
 */
@Composable
internal fun MemberTypeSelection(onMemberTypeSelected: (MemberType) -> Unit) {
    MemberTypeButton(
        label = "בן משפחה מהישיבה",
        onClick = { onMemberTypeSelected(MemberType.Yeshiva) }
    )
    Spacer(modifier = Modifier.height(8.dp))
    MemberTypeButton(
        label = "בן משפחה שאינו מהישיבה",
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

/**
 * Form fields for adding a Yeshiva family member.
 *
 * @param firstName The first name input.
 * @param lastName The last name input.
 * @param machzor The machzor input.
 * @param isRabbi The rabbi status.
 * @param onFirstNameChange Callback for updating the first name.
 * @param onLastNameChange Callback for updating the last name.
 * @param onMachzorChange Callback for updating the machzor.
 * @param onIsRabbiChange Callback for updating the rabbi status.
 */
@Composable
internal fun YeshivaMemberForm(
    firstName: String, lastName: String, machzor: Int?, isRabbi: Boolean,
    onFirstNameChange: (String) -> Unit, onLastNameChange: (String) -> Unit,
    onMachzorChange: (Int?) -> Unit, onIsRabbiChange: (Boolean) -> Unit
) {
    MemberNameFields(firstName, lastName, onFirstNameChange, onLastNameChange)
    MachzorInput(
        machzor = machzor,
        onMachzorChange = onMachzorChange,
    )
    BooleanSelection("האם בן משפחה זה, רב בישיבה?", isRabbi, onIsRabbiChange)
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
internal fun NonYeshivaMemberForm(
    firstName: String, lastName: String, gender: Boolean,
    onFirstNameChange: (String) -> Unit, onLastNameChange: (String) -> Unit,
    onGenderChange: (Boolean) -> Unit
) {
    MemberNameFields(firstName, lastName, onFirstNameChange, onLastNameChange)
    GenderSelection(gender, onGenderChange)
}

/**
 * Provides input fields for first and last name.
 *
 * @param firstName The first name input.
 * @param lastName The last name input.
 * @param onFirstNameChange Callback for updating the first name.
 * @param onLastNameChange Callback for updating the last name.
 */
@Composable
internal fun MemberNameFields(
    firstName: String, lastName: String,
    onFirstNameChange: (String) -> Unit, onLastNameChange: (String) -> Unit
) {
    TextField(
        value = firstName,
        onValueChange = onFirstNameChange,
        label = { Text("שם פרטי") }
    )
    TextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = { Text("שם משפחה") }
    )
}

/**
 * Provides radio buttons for selecting gender.
 *
 * @param gender The currently selected gender.
 * @param onGenderChange Callback for updating the gender.
 */
@Composable
internal fun GenderSelection(gender: Boolean, onGenderChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Text("מין:")
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = gender,
                    onClick = { onGenderChange(true) }
                )
                Text("זכר")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !gender,
                    onClick = { onGenderChange(false) }
                )
                Text("נקבה")
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
internal fun BooleanSelection(label: String, selected: Boolean, onChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Text(label)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selected,
                    onClick = { onChange(true) }
                )
                Text("כן")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !selected,
                    onClick = { onChange(false) }
                )
                Text("לא")
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
internal fun MachzorInput(machzor: Int?,
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
            label = { Text("מחזור") },
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
 * Displays a button to confirm adding a new family member and handles potential duplicates.
 * If a member with the same first name, last name, and machzor exists, prompts the user
 * to confirm or reject using the existing member.
 *
 * @param firstName The first name input.
 * @param lastName The last name input.
 * @param memberType The selected member type (Yeshiva or NonYeshiva).
 * @param machzor The machzor input (nullable).
 * @param isRabbi Indicates if the member is a rabbi (for Yeshiva members).
 * @param gender The gender input (true for male, false for female).
 * @param existingMembers A list of existing family members to check for duplicates.
 * @param onAddMember Callback function to add a new family member.
 * @param onDismiss Callback function to dismiss the add member dialog.
 */
@Composable
internal fun ConfirmButton(
    firstName: String, lastName: String, memberType: MemberType?, machzor: Int?,
    isRabbi: Boolean, gender: Boolean,
    existingMembers: List<FamilyMember>,
    onAddMember: (FamilyMember) -> Unit, onDismiss: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var matchedMember by remember { mutableStateOf<FamilyMember?>(null) }

    Button(
        onClick = {
            val matched = findMatchingMember(firstName, lastName, machzor, existingMembers)
            if (matched != null) {
                matchedMember = matched
                showDialog = true
            } else if (firstName.isNotBlank() && lastName.isNotBlank()) {
                val familyMember = when (memberType) {
                    MemberType.Yeshiva -> FamilyMember(firstName, lastName, gender, machzor, isRabbi)
                    MemberType.NonYeshiva -> FamilyMember(firstName, lastName, gender)
                    else -> throw IllegalArgumentException("Invalid member type")
                }
                onAddMember(familyMember)
                onDismiss()
            }
        }
    ) {
        Text("הוסף בן משפחה")
    }

    // Show duplicate member dialog if needed
    if (showDialog && matchedMember != null) {
        DuplicateMemberDialog(
            matchedMember = matchedMember!!,
            onConfirm = {
                matchedMember?.let {
                    onAddMember(it)
                    onDismiss()
                }
                showDialog = false
            },
            onDismiss = {
                // Add the new member again if user chooses to add a new one
                if (firstName.isNotBlank() && lastName.isNotBlank()) {
                    val familyMember = when (memberType) {
                        MemberType.Yeshiva -> FamilyMember(firstName, lastName, gender, machzor, isRabbi)
                        MemberType.NonYeshiva -> FamilyMember(firstName, lastName, gender)
                        else -> throw IllegalArgumentException("Invalid member type")
                    }
                    onAddMember(familyMember)
                }
                showDialog = false
            }
        )
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
fun DuplicateMemberDialog(
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
        text = { Text("כבר קיים בן משפחה בשם ${matchedMember.getFullName()}. האם זו הכוונה?") }
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
fun findMatchingMember(
    firstName: String, lastName: String, machzor: Int?, members: List<FamilyMember>
): FamilyMember? {
    return members.find {
        it.getFirstName() == firstName && it.getLastName() == lastName && it.getMachzor() == machzor
    }
}
