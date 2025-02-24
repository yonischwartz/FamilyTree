package com.example.familytree.ui.theme.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import com.example.familytree.ui.theme.CustomizedText
import com.example.familytree.ui.theme.DialogTitle
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.DialogButton

/**
 * Displays a dialog with a single button.
 *
 * @param title The title of the dialog, displayed at the top center.
 * @param text The message or content of the dialog.
 * @param textForButton The text displayed on the button (default is HebrewText.OK).
 * @param onDismiss A callback invoked when the dialog is dismissed.
 */
@Composable
fun DialogWithOneButton(
    title: String,
    text: String,
    textForButton: String = HebrewText.OK,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(title) },
        text = { CustomizedText(text) },
        confirmButton = { DialogButton(textForButton, onDismiss) }
    )
}