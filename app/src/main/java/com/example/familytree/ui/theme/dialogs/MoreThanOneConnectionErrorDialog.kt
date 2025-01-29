package com.example.familytree.ui.theme.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations

/**
 * Displays a dialog indicating an error when more than one connection exists between members.
 *
 * @param onDismiss A callback function that is triggered when the user dismisses the dialog.
 */
@Composable
fun MoreThanOneConnectionErrorDialog(
    onDismiss: () -> Unit,
    relation: Relations,
    existingMember: FamilyMember
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Connection Error")
        },
        text = {
            Text(
                text = "${existingMember.getFullName()} already has a  כ-${relation.name.lowercase()}."
            )
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("OK")
            }
        }
    )
}