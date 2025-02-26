package com.example.familytree.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import com.example.familytree.ui.CustomizedText
import com.example.familytree.ui.DialogTitle
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.DialogButton

/**
 * Displays a customizable dialog with a single button.
 *
 * @param title The title of the dialog, displayed at the top center.
 * @param text The default text to display if no custom content is provided.
 * @param textForButton The text displayed on the button (default is HebrewText.OK).
 * @param onDismiss An optional callback invoked when the dialog is dismissed. Defaults to the button's action.
 * @param onClick An optional callback invoked when the button is clicked.
 * @param contentOfDialog An optional composable function providing custom content for the dialog.
 */
@Composable
fun DialogWithOneButton(
    title: String,
    text: String = "",
    textForButton: String = HebrewText.OK,
    onClick: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    contentOfDialog: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss ?: onClick,
        title = { DialogTitle(title) },
        text = {
            if (contentOfDialog != null) {
                contentOfDialog()
            } else {
                CustomizedText(text)
            }
        },
        confirmButton = {
            DialogButton(
                text = textForButton,
                onClick = onClick
            )
        }
    )
}
