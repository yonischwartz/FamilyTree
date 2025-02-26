package com.example.familytree.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.HebrewText

/**
 * Composable function to display a dialog for selecting a family member to relate to.
 *
 * @param onMemberSelected Callback when a member is selected.
 * @param onPrevious Callback when the previous button is clicked.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun ChooseMemberToRelateToDialog(
    listOfMembersToConnectTo: List<FamilyMember> = DatabaseManager.getAllMembers(),
    onMemberSelected: (FamilyMember) -> Unit,
    showPreviousButton: Boolean = true,
    onPrevious: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var checkedMemberId by remember { mutableStateOf<String?>(null) }

    // The lambda for when the user clicks the NEXT button
    var getOptionalMembersToConnectTo: () -> Unit = {}
    getOptionalMembersToConnectTo = {
        listOfMembersToConnectTo
            .find { it.getId() == checkedMemberId }?.let { onMemberSelected(it) }
    }

    // The condition for enabling the NEXT button
    val didUserChooseMember = checkedMemberId != null


    DialogWithTwoButtons(
        title = HebrewText.CHOOSE_FAMILY_MEMBER,
        onClickForLeft = getOptionalMembersToConnectTo,
        textForLeft = HebrewText.NEXT,
        enabledForLeftButton = didUserChooseMember,
        onClickForRight = onPrevious,
        textForRight = HebrewText.PREVIOUS,
        onDismiss = onDismiss,
        contentOfDialog = {
            Column {
                LazyColumn {
                    items(listOfMembersToConnectTo.size) { index ->
                        val member = listOfMembersToConnectTo[index]
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
    )

    // Display detail dialog when a member is selected
    selectedMember?.let { member ->
        InfoOnMemberDialog(member = member, onDismiss = { selectedMember = null })
    }
}
