package com.example.familytree.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.FirebaseManager

@Composable
fun NewMemberDialog(
    firebaseManager: FirebaseManager,
    onDismiss: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") } // Default value

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("הוסף בן משפחה", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("שם פרטי") }
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("שם משפחה") }
                )

                // Gender selection
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("מין:")
                    Button(onClick = { gender = "Male" }) {
                        Text("זכר")
                    }
                    Button(onClick = { gender = "Female" }) {
                        Text("נקבה")
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onDismiss) {
                        Text("ביטול")
                    }
                    Button(onClick = {
                        askUserForDetailsOfNewMember(
                            firebaseManager = firebaseManager,
                            firstName = firstName,
                            lastName = lastName,
                            gender = gender
                        )
                        onDismiss()
                    }) {
                        Text("הוסף")
                    }
                }
            }
        }
    }
}

fun askUserForDetailsOfNewMember(
    firebaseManager: FirebaseManager,
    firstName: String,
    lastName: String,
    gender: String
) {
    val newMember = FamilyMember(
        firstName = firstName,
        lastName = lastName,
        gender = (gender == "Male")
    )

    firebaseManager.addNewMemberToTree(newMember)
}
