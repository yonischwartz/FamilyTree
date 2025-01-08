package com.example.familytree.ui.theme
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.familytree.data.FamilyMember
//import com.example.familytree.data.MemberType
//
//
//
///**
// * Composable function that displays the form to add a new family member, based on the selected type.
// *
// * @param memberType The type of the family member (Yeshiva or Non-Yeshiva).
// * @param onDismiss The action to perform when the dialog is dismissed.
// * @param onAddMember The action to perform after adding the member.
// */
//@Composable
//fun AddFamilyMemberForm(
//    memberType: MemberType,
//    onDismiss: () -> Unit,
//    onAddMember: (FamilyMember) -> Unit
//) {
//    var firstName by remember { mutableStateOf("") }
//    var lastName by remember { mutableStateOf("") }
//    var gender by remember { mutableStateOf(true) } // true for male, false for female
//    var machzor by remember { mutableStateOf(0) }
//    var isRabbi by remember { mutableStateOf(false) }
//
//    // Dialog to add member details
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = {
//            Text("הוסף בן משפחה", style = MaterialTheme.typography.titleMedium)
//        },
//        text = {
//            Column(modifier = Modifier.padding(8.dp)) {
//                TextField(
//                    value = firstName,
//                    onValueChange = { firstName = it },
//                    label = { Text("שם פרטי") }
//                )
//                TextField(
//                    value = lastName,
//                    onValueChange = { lastName = it },
//                    label = { Text("שם משפחה") }
//                )
//                Row {
//                    Text("מין: ")
//                    RadioButton(
//                        selected = gender,
//                        onClick = { gender = true }
//                    )
//                    Text("זכר")
//                    Spacer(modifier = Modifier.width(8.dp))
//                    RadioButton(
//                        selected = !gender,
//                        onClick = { gender = false }
//                    )
//                    Text("נקבה")
//                }
//                if (memberType == MemberType.Yeshiva) {
//                    TextField(
//                        value = machzor.toString(),
//                        onValueChange = { machzor = it.toIntOrNull() ?: 0 },
//                        label = { Text("מחזור") }
//                    )
//                    Row {
//                        Text("רב: ")
//                        Checkbox(checked = isRabbi, onCheckedChange = { isRabbi = it })
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    val familyMember = if (memberType == MemberType.Yeshiva) {
//                        FamilyMember(firstName, lastName, gender, machzor, isRabbi)
//                    } else {
//                        FamilyMember(firstName, lastName, gender)
//                    }
//                    onAddMember(familyMember)
//                    onDismiss()
//                }
//            ) {
//                Text("הוסף בן משפחה")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                Text("ביטול")
//            }
//        }
//    )
//}