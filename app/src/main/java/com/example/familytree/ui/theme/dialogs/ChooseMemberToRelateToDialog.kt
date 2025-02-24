package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.ui.theme.DialogTitle
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.DialogButton

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
        onDismissRequest = onDismiss,
        title = { DialogTitle(HebrewText.CHOOSE_FAMILY_MEMBER) },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showPreviousButton) {

                    DialogButton(
                        text = HebrewText.PREVIOUS,
                        onClick = onPrevious
                    )
                }

                // In case there is no PREVIOUS button, i want the NEXT stay in the left.
                else {
                    Spacer(modifier = Modifier.width(100.dp))
                }

                DialogButton(
                    text = HebrewText.NEXT,
                    onClick = {
                        existingMembers.find { it.getId() == checkedMemberId }?.let { onMemberSelected(it) }
                    },
                    enabled = checkedMemberId != null
                )
            }
        }
    )

    // Display detail dialog when a member is selected
    selectedMember?.let { member ->
        InfoOnMemberDialog(member = member, onDismiss = { selectedMember = null })
    }
}
