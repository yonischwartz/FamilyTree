package com.example.familytree.ui.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.data.dataManagement.DatabaseManager.removeMemberFromLocalMemberMap
import com.example.familytree.data.exceptions.UnsafeDeleteException
import com.example.familytree.ui.DialogButton
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.dialogs.errorAndSuccessDialogs.DeleteErrorDialog


/**
 * Composable function that displays a dialog containing a list of all family members.
 * Each member can be clicked to view detailed information.
 *
 * @param onDismiss The action to perform when the dialog is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MemberListDialog(
    onDismiss: () -> Unit,
    membersToDisplay: List<FamilyMember> = DatabaseManager.getAllMembers()
) {

    val sortedMembers = remember { membersToDisplay.sortedBy { it.getFullName() }.toMutableStateList() }
    val filteredMembers = remember { mutableStateListOf<FamilyMember>().apply { addAll(sortedMembers) } }

    // Use state-backed mutable list for dynamic updates
//    val memberList = remember { mutableStateListOf(*filteredMembers.toTypedArray()) }
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var showDeleteErrorDialog by remember { mutableStateOf(false) }

    // Set right-to-left layout direction for Hebrew content.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(HebrewText.FAMILY_MEMBER_LIST, style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Column {

                    MembersSearchBar(
                        members = filteredMembers,
                        onSearchResults = { results ->
                            filteredMembers.clear()
                            filteredMembers.addAll(results)
                        }
                    )

                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(sortedMembers) { member ->
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                                Text(
                                    text = member.getFullName(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedMember = member }
                                )
                                DialogButton(
                                    text = HebrewText.REMOVE,
                                    onClick = {
                                        try {
                                            removeMemberFromLocalMemberMap(member.getId())
                                            sortedMembers.remove(member)
                                        }
                                        catch (e: UnsafeDeleteException) {
                                            showDeleteErrorDialog = true
                                        }
                                    }
                                )

                                // Inform user that removal is invalid
                                if (showDeleteErrorDialog) {
                                    DeleteErrorDialog { showDeleteErrorDialog = false }
                                }
                            }
                        }
                    }

                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(HebrewText.CLOSE)
                }
            }
        )
    }

    // Display details dialog of member
    selectedMember?.let { member ->
        InfoOnMemberDialog(
            member = member,
            onDismiss = { selectedMember = null },
        )
    }
}