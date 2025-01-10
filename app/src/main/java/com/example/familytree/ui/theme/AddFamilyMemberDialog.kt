package com.example.familytree.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations

/**
 * A composable function that displays a dialog for adding a family member to the family tree.
 *
 * @param onDismiss Callback to handle dialog dismissal.
 * @param onAddMember Callback to handle the addition of a new [FamilyMember] to the tree.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onAddMember: (FamilyMember) -> Unit,
    existingMembers: List<FamilyMember>
) {
    // State variables to capture user input.
    var selectedMemberType by remember { mutableStateOf<MemberType?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(true) } // true for male, false for female
    var machzor by remember { mutableStateOf<Int?>(null) }
    var isRabbi by remember { mutableStateOf(false) }

    if (existingMembers.isEmpty()) {
        AddFirstMemberToTree(
            onDismiss = onDismiss,
            onAddMember = onAddMember,
            existingMembers = existingMembers,
            selectedMemberType = selectedMemberType,
            onMemberTypeChange = { selectedMemberType = it },
            firstName = firstName,
            onFirstNameChange = { firstName = it },
            lastName = lastName,
            onLastNameChange = { lastName = it },
            gender = gender,
            onGenderChange = { gender = it },
            machzor = machzor,
            onMachzorChange = { machzor = it },
            isRabbi = isRabbi,
            onIsRabbiChange = { isRabbi = it }
        )
    } else {
        AddNewMemberAndRelateToExistingMember(
            onDismiss = onDismiss,
            onAddMember = onAddMember,
            existingMembers = existingMembers,
            selectedMemberType = selectedMemberType,
            onMemberTypeChange = { selectedMemberType = it },
            firstName = firstName,
            onFirstNameChange = { firstName = it },
            lastName = lastName,
            onLastNameChange = { lastName = it },
            gender = gender,
            onGenderChange = { gender = it },
            machzor = machzor,
            onMachzorChange = { machzor = it },
            isRabbi = isRabbi,
            onIsRabbiChange = { isRabbi = it }
        )
    }


//        AlertDialog(
//            onDismissRequest = onDismiss,
//            title = { AddMemberDialogTitle(selectedMemberType) },
//            text = {
//                Column {
//                    // Display member type selection if none is chosen.
//                    if (selectedMemberType == null) {
//                        MemberTypeSelection(onMemberTypeSelected = { selectedMemberType = it })
//                    }
//
//                    // Show the appropriate form based on the selected member type.
//                    selectedMemberType?.let { it ->
//                        when (it) {
//                            MemberType.Yeshiva -> YeshivaMemberForm(
//                                firstName = firstName,
//                                lastName = lastName,
//                                machzor = machzor,
//                                isRabbi = isRabbi,
//                                onFirstNameChange = { firstName = it },
//                                onLastNameChange = { lastName = it },
//                                onMachzorChange = { machzor = it },
//                                onIsRabbiChange = { isRabbi = it }
//                            )
//                            MemberType.NonYeshiva -> NonYeshivaMemberForm(
//                                firstName = firstName,
//                                lastName = lastName,
//                                gender = gender,
//                                onFirstNameChange = { firstName = it },
//                                onLastNameChange = { lastName = it },
//                                onGenderChange = { gender = it }
//                            )
//                        }
//                    }
//                }
//            },
//            confirmButton = {
//                if (selectedMemberType != null) {
//                    ConfirmAddingNewMemberButton(
//                        firstName = firstName,
//                        lastName = lastName,
//                        memberType = selectedMemberType,
//                        machzor = machzor,
//                        isRabbi = isRabbi,
//                        gender = gender,
//                        existingMembers = existingMembers,
//                        onAddMember = onAddMember,
//                        onDismiss = onDismiss
//                    )
//                }
//            }
//        )
//    }
}