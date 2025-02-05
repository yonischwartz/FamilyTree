package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.allMachzorim
import com.example.familytree.ui.theme.intToMachzor
import com.example.familytree.ui.theme.machzorToInt

/**
 * A composable function that displays a dialog to collect details for creating a new family member.
 *
 * This dialog allows the user to input a family member's first name, last name, and other optional
 * details depending on the selected member type. It includes text fields, boolean selections, and
 * a conditional machzor input for Yeshiva members.
 *
 * @param headLine The title of the dialog, typically a prompt for entering family member details.
 * @param selectedMemberType The type of family member being created (Yeshiva or NonYeshiva).
 * @param onFamilyMemberCreation Callback function invoked when the user confirms the creation
 *                                of a new `FamilyMember`. A `FamilyMember` object is passed
 *                                containing the collected details.
 * @param onDismiss Callback function invoked when the user cancels or dismisses the dialog.
 *
 * @see FamilyMember
 * @see MemberType
 */
@Composable
fun AskUserForMemberDetailsDialog(
    headLine: String,
    selectedMemberType: MemberType?,
    onFamilyMemberCreation: (FamilyMember) -> Unit,
    onDismiss: () -> Unit
) {
    // State variables to store user input
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var machzor by remember { mutableStateOf<Int?>(null) }
    var isRabbi by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf(true) }
    var isYeshivaRabbi by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(headLine) },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {

                // Input field for first name
                FreeTextField(
                    text = HebrewText.FIRST_NAME,
                    value = firstName,
                    onValueChange = { firstName = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input field for last name
                FreeTextField(
                    text = HebrewText.LAST_NAME,
                    value = lastName,
                    onValueChange = { lastName = it }
                )

                // Only Yeshiva members need to enter a machzor
                if (selectedMemberType == MemberType.Yeshiva) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MachzorInput(
                        machzor = machzor,
                        onMachzorChange = { machzor = it }
                    )
                }

                // Only nonYeshiva members need to choose a gender
                else {
                    BooleanSelection(
                        label = HebrewText.SEX,
                        optionOne = HebrewText.MALE,
                        optionTwo = HebrewText.FEMALE,
                        selected = gender,
                        onChange = { gender = it }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Only a male member can be a rabbi
                if (gender) {
                    BooleanSelection(
                        label = HebrewText.IS_THIS_FAMILY_MEMBER_A_RABBI,
                        selected = isRabbi,
                        onChange = { isRabbi = it }
                    )
                }

                // If the member is a rabbi and a Yeshiva member, ask if they are a Yeshiva rabbi
                if (isRabbi && selectedMemberType == MemberType.Yeshiva) {
                    Spacer(modifier = Modifier.height(8.dp))
                    BooleanSelection(
                        label = HebrewText.IS_THIS_RABBI_A_YESHIVA_RABBI,
                        selected = isYeshivaRabbi,
                        onChange = { isYeshivaRabbi = it }
                    )
                }
            }
        },

        // Confirm button is enabled only when mandatory fields are filled
        confirmButton = {
            TextButton(
                onClick = {
                    onFamilyMemberCreation(
                        FamilyMember(
                            memberType = selectedMemberType!!,
                            firstName = firstName,
                            lastName = lastName,
                            gender = gender,
                            machzor = machzor,
                            isRabbi = isRabbi,
                            isYeshivaRabbi = isYeshivaRabbi
                        )
                    )
                },
                enabled = firstName.isNotEmpty() && lastName.isNotEmpty() &&
                        (selectedMemberType == MemberType.NonYeshiva || machzor != null)
            ) {
                Text(HebrewText.NEXT)
            }
        },

        // Dismiss button to close the dialog without saving
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(HebrewText.CANCEL)
            }
        }
    )
}

// Private functions

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
 * A composable function that renders a boolean selection using radio buttons.
 *
 * This component allows users to choose between two options, typically "Yes" and "No".
 *
 * @param label A descriptive text displayed above the radio buttons.
 * @param optionOne The text label for the `true` selection (default: HebrewText.YES).
 * @param optionTwo The text label for the `false` selection (default: HebrewText.NO).
 * @param selected The currently selected boolean value (default: `true`).
 * @param onChange A callback function triggered when the selection changes.
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
