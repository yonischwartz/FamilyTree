package com.example.familytree.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.familytree.data.dataManagement.FamilyTreeData
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
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
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf


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
private fun MemberNameFields(
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
private fun GenderSelection(gender: Boolean, onGenderChange: (Boolean) -> Unit) {
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
private fun BooleanSelection(label: String, selected: Boolean, onChange: (Boolean) -> Unit) {
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
internal fun ConfirmAddingNewMemberButton(
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
private fun DuplicateMemberDialog(
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
 * @param members List of family members to display in the dropdown menu.
 * @param onMemberSelected Callback function that is triggered when a family member is selected.
 *        It returns the selected `FamilyMember` object.
 * @param onNext Callback function that is triggered when the "המשך" (Next) button is clicked,
 *        typically to proceed to the next step in the process.
 */
@Composable
private fun ChooseMemberToRelateTo(
    members: List<FamilyMember>,
    onMemberSelected: (FamilyMember) -> Unit,
    onNext: () -> Unit
) {
    // State to store the selected family member
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }

    // Main column layout for the composable
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Display the instructional text
        Text(
            text = "בני משפחה חדשים נדרשים להיות קשורים לבן משפחה קיים בעץ.",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Prompt text for selecting a family member to relate to
        Text(
            text = "לאיזה בן משפחה בעץ, מקושר בן המשפחה שאתה רוצה להוסיף?",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dropdown menu for selecting a family member
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display the name of the selected member or placeholder if none selected
                Text(text = selectedMember?.getFullName() ?: "בחר בן משפחה", fontSize = 16.sp)
            }
            // Dropdown menu with member options
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                members.forEach { member ->
                    // Each dropdown item displays a member's name
                    DropdownMenuItem(onClick = {
                        selectedMember = member
                        expanded = false
                    }, text = {
                        Text(text = member.getFullName())
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Button to continue after selecting a family member
        Button(
            onClick = {
                selectedMember?.let {
                    // Trigger the callbacks with the selected member and proceed
                    onMemberSelected(it)
                    onNext()
                }
            },
            enabled = selectedMember != null, // Enable button only if a member is selected
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "המשך")
        }
    }
}

/**
 * A composable function that displays a user interface for selecting the relation
 * between an existing family member and a new family member to be added.
 *
 * @param existingMember The `FamilyMember` object representing the existing family member.
 * @param onRelationSelected A callback function that is invoked with the selected relation
 * when the user clicks the "המשך" (Next) button.
 */
    @Composable
private fun HowAreTheyRelated(
    existingMember: FamilyMember,
    onRelationSelected: (Relations) -> Unit
) {
    // Holds the currently selected relation, initialized to null.
    var selectedRelation by remember { mutableStateOf<Relations?>(null) }

    // Generates a list of relation options formatted as dropdown choices.
    val relationOptions = Relations.entries.map { relation ->
        "בן המשפחה שאני רוצה להוסיף, הוא ${relation.displayName()} ${existingMember.getFullName()}"
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Displays the question prompting the user to choose how the new member is related.
        Text(
            text = "כיצד ${existingMember.getFullName()} קשור לחבר המשפחה החדש?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Manages dropdown menu expansion state.
        var expanded by remember { mutableStateOf(false) }

        Box {
            // Button that opens or closes the dropdown menu.
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(text = selectedRelation?.displayName() ?: "בחר קשר משפחתי")
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button that confirms the selection and invokes the callback with the selected relation.
        Button(
            onClick = {
                selectedRelation?.let(onRelationSelected)
            },
            enabled = selectedRelation != null
        ) {
            Text(text = "המשך")
        }
    }
}



/**
 * Provides Hebrew-friendly display names for Relations enum.
 * This function returns a string that represents the relationship between two family members.
 *
 * @return A string representing the relationship in Hebrew, such as "אבא של" for FATHER or "נכד של" for GRANDSON.
 */
private fun Relations.displayName(): String {
    return when (this) {
        Relations.MARRIAGE -> "נשוי ל"
        Relations.FATHER -> "אבא של"
        Relations.MOTHER -> "אמא של"
        Relations.SON -> "בן של"
        Relations.DAUGHTER -> "בת של"
        Relations.GRANDMOTHER -> "סבתא של"
        Relations.GRANDFATHER -> "סבא של"
        Relations.GRANDSON -> "נכד של"
        Relations.GRANDDAUGHTER -> "נכדה של"
        Relations.COUSINS -> "בן דוד / בת דודה של"
        Relations.SIBLINGS -> "אח / אחות של"
    }
}

/**
 * A composable function that displays a dialog to add the first family member to the tree.
 * This dialog allows users to input details about the family member and add them to the database.
 *
 * @param onDismiss A lambda function triggered when the dialog is dismissed.
 * @param onAddMember A lambda function that takes a [FamilyMember] object and adds it to the database.
 * @param existingMembers A list of [FamilyMember] objects representing current members in the tree.
 * @param selectedMemberType The type of member being added (Yeshiva or NonYeshiva).
 * @param onMemberTypeChange A lambda function triggered when the member type changes.
 * @param firstName A string representing the first name of the family member.
 * @param onFirstNameChange A lambda function triggered when the first name changes.
 * @param lastName A string representing the last name of the family member.
 * @param onLastNameChange A lambda function triggered when the last name changes.
 * @param gender A boolean representing the gender of the family member. Always set to true (male).
 * @param onGenderChange A lambda function triggered when the gender value changes. Included for completeness but not used.
 * @param machzor An optional integer representing the machzor (graduating class) of the member, if applicable.
 * @param onMachzorChange A lambda function triggered when the machzor value changes.
 * @param isRabbi A boolean indicating if the member is a rabbi.
 * @param onIsRabbiChange A lambda function triggered when the isRabbi status changes.
 */
@Composable
internal fun AddFirstMemberToTree(
    onDismiss: () -> Unit,
    onAddMember: (FamilyMember) -> Unit,
    existingMembers: List<FamilyMember>,
    selectedMemberType: MemberType?,
    onMemberTypeChange: (MemberType) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    gender: Boolean,
    onGenderChange: (Boolean) -> Unit,
    machzor: Int?,
    onMachzorChange: (Int?) -> Unit,
    isRabbi: Boolean,
    onIsRabbiChange: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AddMemberDialogTitle(selectedMemberType) },
        text = {
            Column {
                // Display member type selection if none is chosen.
                if (selectedMemberType == null) {
                    MemberTypeSelection(onMemberTypeSelected = onMemberTypeChange)
                }

                // Show the appropriate form based on the selected member type.
                selectedMemberType?.let { it ->
                    when (it) {
                        MemberType.Yeshiva -> YeshivaMemberForm(
                            firstName = firstName,
                            lastName = lastName,
                            machzor = machzor,
                            isRabbi = isRabbi,
                            onFirstNameChange = onFirstNameChange,
                            onLastNameChange = onLastNameChange,
                            onMachzorChange = onMachzorChange,
                            onIsRabbiChange = onIsRabbiChange
                        )
                        MemberType.NonYeshiva -> NonYeshivaMemberForm(
                            firstName = firstName,
                            lastName = lastName,
                            gender = gender,
                            onFirstNameChange = onFirstNameChange,
                            onLastNameChange = onLastNameChange,
                            onGenderChange = onGenderChange
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (selectedMemberType != null) {
                ConfirmAddingNewMemberButton(
                    firstName = firstName,
                    lastName = lastName,
                    memberType = selectedMemberType,
                    machzor = machzor,
                    isRabbi = isRabbi,
                    gender = gender,
                    existingMembers = existingMembers,
                    onAddMember = onAddMember,
                    onDismiss = onDismiss
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddNewMemberAndRelateToExistingMember(
    onDismiss: () -> Unit,
    onAddMember: (FamilyMember) -> Unit,
    existingMembers: List<FamilyMember>,
    selectedMemberType: MemberType?,
    onMemberTypeChange: (MemberType) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    gender: Boolean,
    onGenderChange: (Boolean) -> Unit,
    machzor: Int?,
    onMachzorChange: (Int?) -> Unit,
    isRabbi: Boolean,
    onIsRabbiChange: (Boolean) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var selectedMember: FamilyMember? by remember { mutableStateOf(null) }
    var selectedRelation: Relations? by remember { mutableStateOf(null) }

    when (step) {
        1 -> ChooseMemberToRelateTo(
            members = existingMembers,
            onMemberSelected = { selectedMember = it },
            onNext = { step = 2 }
        )
        2 -> {
            selectedMember?.let { member ->
                HowAreTheyRelated(
                    existingMember = member,
                    onRelationSelected = {
                        selectedRelation = it
                        step = 3
                    }
                )
            }
        }
        3 -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { AddMemberDialogTitle(selectedMemberType) },
                text = {
                    selectedMemberType?.let {
                        when (it) {
                            MemberType.Yeshiva -> YeshivaMemberForm(
                                firstName = firstName,
                                lastName = lastName,
                                machzor = machzor,
                                isRabbi = isRabbi,
                                onFirstNameChange = onFirstNameChange,
                                onLastNameChange = onLastNameChange,
                                onMachzorChange = onMachzorChange,
                                onIsRabbiChange = onIsRabbiChange
                            )
                            MemberType.NonYeshiva -> NonYeshivaMemberForm(
                                firstName = firstName,
                                lastName = lastName,
                                gender = gender,
                                onFirstNameChange = onFirstNameChange,
                                onLastNameChange = onLastNameChange,
                                onGenderChange = onGenderChange
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val newMember = FamilyMember(/* Construct with input values */)
                        onAddMember(newMember)
                        try {
                            FamilyTreeData.addConnectionToAdjacencyList(
                                memberOne = selectedMember!!,
                                memberTwo = newMember,
                                relation = selectedRelation!!
                            )
                        } catch (e: Exception) {
                            FamilyTreeData.deleteFamilyMember(newMember.documentId)
                            //TODO: add here a message that explains why the new member couldn't be added
                        }
                        onDismiss()
                    }) {
                        Text("הוסף")
                    }
                }

            )
        }
    }
}

