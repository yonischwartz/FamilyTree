package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.MemberType
import com.example.familytree.ui.theme.AskUserForNonYeshivaMemberDetails
import com.example.familytree.ui.theme.AskUserForYeshivaMemberDetails
import com.example.familytree.ui.theme.HebrewText

/**
 * A dialog that prompts the user to enter details for a new family member.
 *
 * @param selectedMemberType The type of family member being added (Yeshiva or Non-Yeshiva).
 * @param onFamilyMemberCreation Callback triggered when a new family member is created.
 * @param onDismiss Callback triggered when the dialog is dismissed.
 */
@Composable
fun AskUserForMemberDetailsDialog(
    headLine: String,
    selectedMemberType: MemberType?,
    onFamilyMemberCreation: (FamilyMember) -> Unit,
    onDismiss: () -> Unit
) {
    var newMember by remember { mutableStateOf<FamilyMember?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(headLine) },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedMemberType) {
                    MemberType.Yeshiva -> {
                        AskUserForYeshivaMemberDetails(MemberType.Yeshiva) { newMember = it }
                    }
                    MemberType.NonYeshiva -> {
                        AskUserForNonYeshivaMemberDetails { newMember = it }
                    }
                    else -> Unit
                }
            }
        },
        // can you make that the user will be able to push the confirmButton only if the new member isn't null?
        confirmButton = {
            TextButton(
                onClick = { newMember?.let(onFamilyMemberCreation) },
                enabled = newMember != null
            ) {
                Text(HebrewText.NEXT)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(HebrewText.CANCEL)
            }
        }
    )
}
