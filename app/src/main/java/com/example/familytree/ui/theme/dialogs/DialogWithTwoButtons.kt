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

/**
 * A customizable dialog with two buttons.
 *
 * @param title The title of the dialog displayed at the top.
 * @param text The main content text of the dialog.
 * @param onClickForLeft The action to perform when the confirm button is clicked.
 * @param textForLeft The text displayed on the confirm button.
 * @param onClickForRight The action to perform when the dismiss button is clicked or when the dialog is dismissed.
 * @param textForRight The text displayed on the dismiss button.
 */
@Composable
fun DialogWithTwoButtons(
    title: String,
    text: String,
    onClickForLeft: () -> Unit,
    textForLeft: String,
    onClickForRight: () -> Unit,
    textForRight: String
) {
    AlertDialog(
        onDismissRequest = onClickForRight,
        title = { DialogTitle(title) },
        text = { CustomizedText(text) },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // First button
                DialogButton(textForRight, onClickForRight)

                // Second button
                DialogButton(textForLeft, onClickForLeft)
            }
        }
    )
}