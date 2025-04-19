package com.example.familytree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.dialogs.DialogWithButtons

/**
 * Displays an error dialog when attempting to delete a family member that would break the family tree structure.
 *
 * @param onDismiss A callback function that is invoked when the dialog is dismissed.
 */
@Composable
fun DeleteErrorDialog(
    onDismiss: () -> Unit
) {
    val text: String = HebrewText.REMOVING_THIS_MEMBER_BRAKES_THE_TREE
    val title: String = HebrewText.ERROR_REMOVING_MEMBER

    DialogWithButtons(
        title = title,
        text = text,
        onLeftButtonClick = onDismiss
    )
}