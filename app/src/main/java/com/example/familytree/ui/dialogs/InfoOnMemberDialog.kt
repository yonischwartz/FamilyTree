package com.example.familytree.ui.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.familytree.data.FamilyMember
import androidx.compose.ui.unit.dp
import com.example.familytree.data.MemberType
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.data.exceptions.UnsafeDeleteException
import com.example.familytree.ui.CustomizedTitleText
import com.example.familytree.ui.DisplayConnectionsForMembersInfoDialog
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.dialogs.errorAndSuccessDialogs.DeleteErrorDialog
import com.example.familytree.ui.intToMachzor
import androidx.compose.runtime.key

/**
 * Composable function that displays detailed information about a yeshiva family member.
 *
 * @param member The yeshiva family member whose details are shown.
 * @param onDismiss The action to perform when the detail dialog is dismissed.
 * @param showRemoveButton Whether to show the "Remove" button. Defaults to false.
 * @param showEditButton Whether to show the "Edit" button. Defaults to false.
 * @param onMemberRemoval Callback invoked when the user chooses to remove the member, receiving the member's ID. Defaults to no action.
 * @param onMemberEdit Callback invoked when the user chooses to edit the member. Defaults to no action.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun InfoOnMemberDialog(
    member: FamilyMember,
    onDismiss: () -> Unit,
    showRemoveButton: Boolean = false,
    showEditButton: Boolean = false,
    onMemberRemoval: (memberId: String) -> Unit = {},
    onMemberEdit: (member: FamilyMember) -> Unit = {}
) {

    var memberToDisplay by remember { mutableStateOf(member) }
    var showDeleteErrorDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    DialogWithButtons(
        title = HebrewText.FAMILY_MEMBER_DETAILS,
        textForLeftButton = if (showRemoveButton) HebrewText.REMOVE else null,
        onLeftButtonClick = if (showRemoveButton) {
            {
                try {
                    onMemberRemoval(memberToDisplay.getId())
                    onDismiss()
                } catch (e: UnsafeDeleteException) {
                    showDeleteErrorDialog = true
                }
            }
        } else null,
        textForRightButton = if (showEditButton) HebrewText.EDIT else null,
        onRightButtonClick = { if (showEditButton) { showEditDialog = true } },
        contentOfDialog = @Composable {
            key(memberToDisplay.getId()) {
                Column(modifier = Modifier.padding(4.dp)) {

                    val machzorToDisplay: String

                    // Add rabbi to name in case member is a rabbi
                    val firstNameDisplay = when {
                        memberToDisplay.getIsRabbi() && memberToDisplay.getGender() -> "${HebrewText.RABBI}${memberToDisplay.getFirstName()}"
                        memberToDisplay.getIsRabbi() && !memberToDisplay.getGender() -> "${HebrewText.RABBI_WIFE}${memberToDisplay.getFirstName()}"
                        else -> memberToDisplay.getFirstName()
                    }

                    // Full name
                    CustomizedTitleText(
                        title = HebrewText.NAME,
                        text = memberToDisplay.getFullName()
                    )

                    // Gender
                    CustomizedTitleText(
                        title = HebrewText.SEX,
                        text = if (memberToDisplay.getGender()) HebrewText.MALE else HebrewText.FEMALE
                    )

                    // Machzor
                    if (memberToDisplay.getMemberType() == MemberType.NonYeshiva) {
                        machzorToDisplay = if (memberToDisplay.getGender()) {
                            HebrewText.HE_IS_NOT_FROM_THE_YESHIVA
                        } else {
                            HebrewText.SHE_IS_NOT_FROM_THE_YESHIVA
                        }
                    } else if (memberToDisplay.getMachzor() == 0) {
                        machzorToDisplay = HebrewText.RABBIS_AND_STAFF
                    } else {
                        machzorToDisplay = intToMachzor[memberToDisplay.getMachzor()].toString()
                    }

                    CustomizedTitleText(
                        title = HebrewText.MACHZOR,
                        text = machzorToDisplay
                    )

                    // Connections
                    DisplayConnectionsForMembersInfoDialog(
                        connections = memberToDisplay.getConnections(),
                        onMemberClick = { clickedMember -> memberToDisplay = clickedMember }
                    )
                }
            }
        },
        onDismiss = onDismiss
    )

    // Inform user that removal is invalid
    if (showDeleteErrorDialog) {
        DeleteErrorDialog { showDeleteErrorDialog = false }
    }

    // Edit family member dialog
    if (showEditDialog) {
        EditFamilyMemberDialog(
            member = memberToDisplay,
            onConfirm = { updatedMember ->
                DatabaseManager.updateMember(memberToDisplay.getId(), updatedMember)
                onMemberEdit(updatedMember)
                memberToDisplay = updatedMember
                showEditDialog = false
            },
            onPrevious = { showEditDialog = false },
            onDismiss = onDismiss
        )
    }

}