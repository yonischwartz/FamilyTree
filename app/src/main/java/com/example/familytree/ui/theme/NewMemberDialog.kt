package com.example.familytree.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.FamilyTreeData
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun NewMemberDialog(
    familyTreeData: FamilyTreeData,
    onDismiss: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") } // Default value

    Dialog(onDismissRequest = onDismiss) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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

                    Text("מין:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RadioButton(
                            selected = gender == "Male",
                            onClick = { gender = "Male" }
                        )
                        Text("זכר")

                        RadioButton(
                            selected = gender == "Female",
                            onClick = { gender = "Female" }
                        )
                        Text("נקבה")
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = onDismiss) {
                            Text("ביטול")
                        }
                        Button(onClick = {
                            val newMember = FamilyMember(
                                firstName = firstName,
                                lastName = lastName,
                                gender = (gender == "Male")
                            )
                            familyTreeData.addNewMemberToTree(newMember)
                            onDismiss()
                        }) {
                            Text("הוסף")
                        }
                    }
                }
            }
        }
    }
}
