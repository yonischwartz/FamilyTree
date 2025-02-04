package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember

/**
 * Displays a dialog to confirm if the user meant to select an existing family member.
 * Prompts the user to confirm or reject using the existing member.
 *
 * @param matchedMember The matched family member to display.
 * @param onConfirm Callback function to confirm the use of the existing member.
 * @param onDismiss Callback function to dismiss the dialog without taking action.
 */
@Composable
private fun SameMemberDialog(
    matchedMember: FamilyMember,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onConfirm) {
                    Text("כן, זה הוא")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDismiss) {
                    Text("לא, הוסף משתמש חדש")
                }
            }
        },
        dismissButton = {},
        text = { Text("כבר קיים בן משפחה בשם ${matchedMember.getFullName()}. האם התכוונת אליו?") }
    )
}