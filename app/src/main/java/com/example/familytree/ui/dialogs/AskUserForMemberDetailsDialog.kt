package com.example.familytree.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.ui.BooleanSelection
import com.example.familytree.ui.TextFieldWithDropdownMenu
import com.example.familytree.ui.TextField
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.allMachzorim
import com.example.familytree.ui.intToMachzor
import com.example.familytree.ui.machzorToInt

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
    expectedGender: Boolean? = null,
    onFamilyMemberCreation: (FamilyMember) -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit
) {
    // State variables to store user input
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var machzor by remember { mutableStateOf<Int?>(null) }
    var isRabbi by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf(true) }
    var isYeshivaRabbi by remember { mutableStateOf(false) }

    // The lambda for when the user clicks the OK button
    var createMember: () -> Unit = {}
    createMember = {
        val familyMember = FamilyMember(
            memberType = selectedMemberType!!,
            firstName = firstName,
            lastName = lastName,
            gender = gender,
            machzor = machzor,
            isRabbi = isRabbi,
            isYeshivaRabbi = isYeshivaRabbi
        )
        onFamilyMemberCreation(familyMember)
    }

    // The condition for enabling the OK button
    val canMemberBeCreated = firstName.isNotEmpty() && lastName.isNotEmpty() &&
            (selectedMemberType == MemberType.NonYeshiva || machzor != null)

    DialogWithTwoButtons(
        title = headLine,
        onClickForLeft = createMember,
        textForLeft = HebrewText.OK,
        enabledForLeftButton = canMemberBeCreated,
        onClickForRight = onPrevious,
        textForRight = HebrewText.PREVIOUS,
        onDismiss = onDismiss,
        contentOfDialog = {
            Column(modifier = Modifier.padding(16.dp)) {

                // Input field for first name
                TextField(
                    text = HebrewText.FIRST_NAME,
                    value = firstName,
                    onValueChange = { firstName = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input field for last name
                TextField(
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

                // nonYeshiva members who doesn't have an expected gender need to choose one
                else if (expectedGender == null) {
                    BooleanSelection(
                        label = HebrewText.SEX,
                        optionOne = HebrewText.MALE,
                        optionTwo = HebrewText.FEMALE,
                        selected = gender,
                        onChange = { gender = it }
                    )
                }

                else {
                    gender = expectedGender
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
@Composable
private fun MachzorInput(
    machzor: Int?,
    onMachzorChange: (Int?) -> Unit
) {
    TextFieldWithDropdownMenu(
        label = HebrewText.MACHZOR,
        options = allMachzorim,
        selectedOption = intToMachzor[machzor],
        onOptionSelected = { selectedOption ->
            val selectedMachzor = machzorToInt[selectedOption] ?: 0
            onMachzorChange(selectedMachzor)
        }
    )
}