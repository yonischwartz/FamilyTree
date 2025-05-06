package com.example.familytree.ui.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.appTextStyleBlack
import com.example.familytree.ui.appTextStyleLargeBlack
import com.example.familytree.ui.buttonColor

/**
 * Composable function to display a dialog for selecting a family member to relate to.
 *
 * @param onMemberSelected Callback when a member is selected.
 * @param onPrevious Callback when the previous button is clicked.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ChooseMemberToRelateToDialog(
    listOfMembersToConnectTo: List<FamilyMember> = DatabaseManager.getAllMembers(),
    onMemberSelected: (FamilyMember) -> Unit,
    showPreviousButton: Boolean = false,
    onPrevious: () -> Unit = {},
    onDismiss: () -> Unit
) {

    // Sort members by their full name
    val sortedMembers = listOfMembersToConnectTo.sortedBy { it.getFullName() }

    // State to hold filtered members based on search input
    var filteredMembers by remember { mutableStateOf(sortedMembers) }

    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var checkedMemberId by remember { mutableStateOf<String?>(null) }

    val onClickRightButton: () -> Unit
    val rightButtonText: String

    if (showPreviousButton) {
        onClickRightButton = onPrevious
        rightButtonText = HebrewText.PREVIOUS
    }

    else {
        onClickRightButton = onDismiss
        rightButtonText = HebrewText.CLOSE
    }

    // The lambda for when the user clicks the NEXT button
    var getOptionalMembersToConnectTo: () -> Unit = {}
    getOptionalMembersToConnectTo = {
        sortedMembers
            .find { it.getId() == checkedMemberId }?.let { onMemberSelected(it) }
    }

    // The condition for enabling the NEXT button
    val didUserChooseMember = checkedMemberId != null

    DialogWithButtons(
        title = HebrewText.CHOOSE_FAMILY_MEMBER,
        onLeftButtonClick = getOptionalMembersToConnectTo,
        textForLeftButton = HebrewText.NEXT,
        enabledForLeftButton = didUserChooseMember,
        onRightButtonClick = onClickRightButton,
        textForRightButton = rightButtonText,
        onDismiss = onDismiss,
        contentOfDialog = {
            Column {

                MembersSearchBar(
                    members = sortedMembers,
                    onSearchResults = { filteredMembers = it }
                )

                LazyColumn {
                    items(filteredMembers.size) { index ->
                        val member = filteredMembers[index]
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
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = buttonColor,
                                    uncheckedColor = Color.Black,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                text = member.getFullName(),
                                style = appTextStyleLargeBlack(),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
    )

    // Display detail dialog when a member is selected
    selectedMember?.let { member ->

        InfoOnMemberDialog(
            member = member,
            onDismiss = { selectedMember = null },
        )
    }
}
