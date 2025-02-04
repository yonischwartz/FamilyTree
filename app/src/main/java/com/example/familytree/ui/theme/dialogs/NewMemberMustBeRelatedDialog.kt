package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.familytree.ui.theme.HebrewText

/**
 * A dialog that informs the user that new family members must be related to an existing member.
 *
 * @param onConfirm Callback function triggered when the user presses the "Next" button.
 * @param onDismiss Callback function triggered when the user presses the "Cancel" button or dismisses the dialog.
 */
@Composable
fun NewMemberMustBeRelatedDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = HebrewText.NEW_FAMILY_MEMBERS_MUST_BE_RELATED_TO_AN_EXISTING_MEMBER,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDismiss) {
                    Text(HebrewText.CANCEL)
                }
                Button(onClick = onConfirm) {
                    Text(HebrewText.NEXT)
                }
            }
        }
    )
}


