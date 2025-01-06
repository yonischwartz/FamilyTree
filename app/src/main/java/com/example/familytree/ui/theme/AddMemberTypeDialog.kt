package com.example.familytree.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.data.YeshivaFamilyMember
import androidx.compose.ui.Alignment


/**
 * Composable function that displays a dialog asking the user whether they want to add a Yeshiva
 * or a Non-Yeshiva family member, and then presents input fields to collect the member's details.
 *
 * @param onDismiss The action to perform when the dialog is dismissed.
 * @param onAddMember The action to perform after adding a new member (with details).
 */
@Composable
fun AddMemberTypeDialog(onDismiss: () -> Unit, onAddMember: (FamilyMember) -> Unit) {
    var selectedMemberType by remember { mutableStateOf<MemberType?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(true) } // Assuming true for male, false for female
    var machzor by remember { mutableStateOf("") }
    var isRabbi by remember { mutableStateOf(false) }

    // Dialog to choose the family member type
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (selectedMemberType == null) {
                Text("בחר סוג בן משפחה", style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column {
                // Choose member type buttons
                if (selectedMemberType == null) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectedMemberType = MemberType.Yeshiva }
                    ) {
                        Text("בן משפחה מהישיבה")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectedMemberType = MemberType.NonYeshiva }
                    ) {
                        Text("בן משפחה שאינו מהישיבה")
                    }
                }

                // Form for Yeshiva family member
                selectedMemberType?.let { it ->
                    if (it == MemberType.Yeshiva) {
                        // Input fields for Yeshiva member
                        TextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("שם פרטי") }
                        )
                        TextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("שם משפחה") }
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Text("מין:")
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = gender,
                                        onClick = { gender = true }
                                    )
                                    Text("זכר")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = !gender,
                                        onClick = { gender = false }
                                    )
                                    Text("נקבה")
                                }
                            }
                        }

                        TextField(
                            value = machzor,
                            onValueChange = { machzor = it },
                            label = { Text("מחזור") }
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Text("רב?")
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isRabbi,
                                        onClick = { isRabbi = true }
                                    )
                                    Text("כן")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = !isRabbi,
                                        onClick = { isRabbi = false }
                                    )
                                    Text("לא")
                                }
                            }
                        }
                    }

                    // Form for Non-Yeshiva family member
                    if (it == MemberType.NonYeshiva) {
                        // Input fields for Non-Yeshiva member
                        TextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("שם פרטי") }
                        )
                        TextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("שם משפחה") }
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Text("מין:")
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = gender,
                                        onClick = { gender = true }
                                    )
                                    Text("זכר")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = !gender,
                                        onClick = { gender = false }
                                    )
                                    Text("נקבה")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedMemberType != null) {  // Show the confirm button only after selection
                Button(
                    onClick = {
                        // After filling the form, create the family member
                        if (firstName.isNotBlank() && lastName.isNotBlank()) {
                            val familyMember: FamilyMember = when (selectedMemberType) {
                                MemberType.Yeshiva -> YeshivaFamilyMember(
                                    firstName, lastName, gender, machzor.toIntOrNull() ?: 0, isRabbi
                                )
                                MemberType.NonYeshiva -> FamilyMember(
                                    firstName, lastName, gender
                                )
                                else -> throw IllegalArgumentException("Invalid member type")
                            }
                            onAddMember(familyMember) // Add the new member
                            onDismiss() // Dismiss the dialog
                        }
                    }
                ) {
                    Text("הוסף בן משפחה")
                }
            }
        }
    )
}
