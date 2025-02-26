package com.example.familytree.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import com.example.familytree.ui.CustomizedText
import com.example.familytree.ui.DialogTitle
import com.example.familytree.ui.TwoConfirmButtons

/**
 * Displays a customizable dialog with two buttons.
 *
 * @param title The title of the dialog, displayed at the top center.
 * @param text The default text to display if no custom content is provided.
 * @param onClickForLeft A callback invoked when the left button is clicked.
 * @param textForLeft The text displayed on the left button.
 * @param enabledForLeftButton Whether the left button is enabled.
 * @param onClickForRight A callback invoked when the right button is clicked.
 * @param textForRight The text displayed on the right button.
 * @param enabledForRightButton Whether the right button is enabled.
 * @param isOnDismissTheRightButton Whether dismissing the dialog triggers the right button's action.
 * @param onDismiss An optional callback invoked when the dialog is dismissed. Defaults to one of the onClick actions.
 * @param contentOfDialog An optional composable function providing custom content for the dialog.
 */
@Composable
fun DialogWithTwoButtons(
    title: String,
    text: String = "",
    onClickForLeft: () -> Unit,
    textForLeft: String,
    enabledForLeftButton: Boolean = true,
    onClickForRight: () -> Unit,
    textForRight: String,
    enabledForRightButton: Boolean = true,
    isOnDismissTheRightButton: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    contentOfDialog: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss ?: if (isOnDismissTheRightButton) { onClickForRight } else { onClickForLeft },
        title = { DialogTitle(title) },
        text = {
            if (contentOfDialog != null) {
                contentOfDialog()
            } else {
                CustomizedText(text)
            }
        },
        confirmButton = {
            TwoConfirmButtons(
                textForLeftButton = textForLeft,
                onClickForLeftButton = onClickForLeft,
                enabledForLeftButton = enabledForLeftButton,
                textForRightButton = textForRight,
                onClickForRightButton = onClickForRight,
                enabledForRightButton = enabledForRightButton
            )
        }
    )
}
