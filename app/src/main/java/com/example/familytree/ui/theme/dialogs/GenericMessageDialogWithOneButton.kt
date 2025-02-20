package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.familytree.ui.theme.HebrewText

/**
 * A generic message dialog using Jetpack Compose's AlertDialog.
 *
 * This composable displays a simple dialog with a title, message, and a single confirmation button.
 *
 * @param title The title of the dialog.
 * @param text The message content displayed in the dialog.
 * @param onDismiss A callback invoked when the dialog is dismissed.
 */
@Composable
fun GenericMessageDialogWithOneButton(
    title: String,
    text: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
        },
        text = { Text(text, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(HebrewText.OK)
            }
        }
    )
}
