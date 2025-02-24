package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.familytree.ui.theme.CustomizedText
import com.example.familytree.ui.theme.DialogTitle
import com.example.familytree.ui.theme.DialogButton
import com.example.familytree.ui.theme.TwoConfirmButtons

/**
 * A customizable dialog with two buttons, supporting enabled/disabled states.
 *
 * @param title The title of the dialog displayed at the top.
 * @param text The main content text of the dialog.
 * @param onClickForLeft The action to perform when the confirm (left) button is clicked.
 * @param textForLeft The text displayed on the confirm (left) button.
 * @param enabledForLeftButton Whether the left button is enabled (default is true).
 * @param onClickForRight The action to perform when the dismiss (right) button is clicked.
 * @param textForRight The text displayed on the dismiss (right) button.
 * @param enabledForRightButton Whether the right button is enabled (default is true).
 */
@Composable
fun DialogWithTwoButtons(
    title: String,
    text: String,
    onClickForLeft: () -> Unit,
    textForLeft: String,
    enabledForLeftButton: Boolean = true,
    onClickForRight: () -> Unit,
    textForRight: String,
    enabledForRightButton: Boolean = true,
    isOnDismissTheRightButton: Boolean = true
) {
    AlertDialog(
        onDismissRequest = if (isOnDismissTheRightButton) { onClickForRight } else { onClickForLeft },
        title = { DialogTitle(title) },
        text = { CustomizedText(text) },
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