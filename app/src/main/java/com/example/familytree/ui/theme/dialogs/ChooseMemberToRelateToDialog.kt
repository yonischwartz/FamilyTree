package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.ShowDetailsForMember

/**
 * Composable function to display a dialog for selecting a family member to relate to.
 *
 * @param existingMembers List of available FamilyMember objects.
 * @param onMemberSelected Callback when a member is selected.
 * @param onPrevious Callback when the previous button is clicked.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun ChooseMemberToRelateToDialog(
    existingMembers: List<FamilyMember>,
    onMemberSelected: (FamilyMember) -> Unit,
    showPreviousButton: Boolean = true,
    onPrevious: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var checkedMemberId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = HebrewText.CHOOSE_FAMILY_MEMBER) },
        text = {
            Column {
                LazyColumn {
                    items(existingMembers.size) { index ->
                        val member = existingMembers[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMember = member }
                                .padding(8.dp)
                        ) {
                            Checkbox(
                                checked = checkedMemberId == member.getId(),
                                onCheckedChange = { isChecked ->
                                    checkedMemberId = if (isChecked) member.getId() else null
                                }
                            )
                            Text(text = member.getFullName(), modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    existingMembers.find { it.getId() == checkedMemberId }?.let { onMemberSelected(it) }
                },
                enabled = checkedMemberId != null
            ) {
                Text(text = HebrewText.NEXT)
            }
        },
        dismissButton = {
            if (showPreviousButton) {
                TextButton(onClick = { onPrevious() }) {
                    Text(text = HebrewText.PREVIOUS)
                }
            }
        }
    )

    // Display detail dialog when a member is selected
    selectedMember?.let { member ->
        ShowDetailsForMember(member = member, onDismiss = { selectedMember = null })
    }
}







































//package com.example.familytree.ui.theme.dialogs
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.familytree.data.FamilyMember
//import com.example.familytree.ui.theme.HebrewText
//
///**
// * Composable function that allows the user to select a family member from a list to relate to
// * when adding a new family member. It displays a dropdown menu with the list of family members,
// * and the user can choose an existing family member to connect the new member to.
// *
// * @param existingMembers List of family members to display in the dropdown menu.
// * @param onMemberSelected Callback function that is triggered when a family member is selected.
// *        It returns the selected `FamilyMember` object.
// * @param onDismiss Callback function triggered when the dialog is dismissed without selection.
// */
//@Composable
//fun ChooseMemberToRelateToDialog(
//    existingMembers: List<FamilyMember>,
//    onMemberSelected: (FamilyMember) -> Unit,
//    onDismiss: () -> Unit
//) {
//    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
//    var expanded by remember { mutableStateOf(false) }
//    var showDialog by remember { mutableStateOf(true) }
//
//    if (showDialog) {
//        AlertDialog(
//            // Dismiss the dialog when requested
//            onDismissRequest = {
//                showDialog = false
//                onDismiss()
//            },
//            title = {
//                Text(text = HebrewText.NEW_FAMILY_MEMBERS_MUST_BE_RELATED_TO_AN_EXISTING_MEMBER)
//            },
//            text = {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),  // Padding around content
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // Display a prompt for selecting a family member
//                    Text(
//                        text = HebrewText.TO_WHICH_EXISTING_MEMBER_IS_YOUR_NEW_MEMBER_CONNECTED_TO,
//                        fontSize = 16.sp,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//                    Box(modifier = Modifier.fillMaxWidth()) {
//                        // Button to open the dropdown menu
//                        TextButton(
//                            onClick = { expanded = true },
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            // Display the selected member's name or a default prompt
//                            Text(
//                                text = selectedMember?.getFullName() ?: HebrewText.CHOOSE_FAMILY_MEMBER,
//                                fontSize = 16.sp
//                            )
//                        }
//                        DropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false }
//                        ) {
//                            // Create a dropdown item for each family member
//                            existingMembers.forEach { member ->
//                                DropdownMenuItem(
//                                    onClick = {
//                                        selectedMember = member
//                                        expanded = false
//                                    },
//                                    text = {
//                                        Text(text = member.getFullName())
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            },
//            confirmButton = {
//                // Proceed with the selected member
//                Button(
//                    onClick = {
//                        selectedMember?.let {
//                            onMemberSelected(it)
//                            showDialog = false
//                        }
//                    },
//                    enabled = selectedMember != null
//                ) {
//                    Text(text = HebrewText.NEXT)
//                }
//            },
//            dismissButton = {
//                // Handle dialog dismissal
//                TextButton(
//                    onClick = {
//                        showDialog = false
//                        onDismiss()
//                    }
//                ) {
//                    Text(text = HebrewText.CANCEL)
//                }
//            }
//        )
//    }
//}
