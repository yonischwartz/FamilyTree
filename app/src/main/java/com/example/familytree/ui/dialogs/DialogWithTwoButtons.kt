package com.example.familytree.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.ui.CustomizedText
import com.example.familytree.ui.DialogTitle
import com.example.familytree.ui.TwoConfirmButtons

/**
 * Displays a customizable dialog with two buttons.
 *
 * @param title The title of the dialog, displayed at the top center.
 * @param text The default text to display if no custom content is provided.
 * @param onLeftButtonClick A callback invoked when the left button is clicked.
 * @param textForLeftButton The text displayed on the left button.
 * @param enabledForLeftButton Whether the left button is enabled.
 * @param onRightButtonClick A callback invoked when the right button is clicked.
 * @param textForRightButton The text displayed on the right button.
 * @param enabledForRightButton Whether the right button is enabled.
 * @param isOnDismissTheRightButton Whether dismissing the dialog triggers the right button's action.
 * @param onDismiss An optional callback invoked when the dialog is dismissed. Defaults to one of the onClick actions.
 * @param contentOfDialog An optional composable function providing custom content for the dialog.
 */
@Composable
fun DialogWithTwoButtons(
    title: String,
    text: String = "",
    onLeftButtonClick: () -> Unit,
    textForLeftButton: String,
    enabledForLeftButton: Boolean = true,
    onRightButtonClick: () -> Unit,
    textForRightButton: String,
    enabledForRightButton: Boolean = true,
    isOnDismissTheRightButton: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    contentOfDialog: @Composable (() -> Unit)? = null
) {

    val dismissAction = onDismiss ?: if (isOnDismissTheRightButton) { onRightButtonClick } else { onLeftButtonClick }

    AlertDialog(
        onDismissRequest = dismissAction,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Centered title
                DialogTitle(
                    text = title,
                    modifier = Modifier.align(Alignment.Center)
                )

                // X icon in the top right
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .clickable { dismissAction() }
                        .padding(4.dp)
                )
            }
        },
        text = {
            if (contentOfDialog != null) {
                contentOfDialog()
            } else {
                CustomizedText(text)
            }
        },
        confirmButton = {
            TwoConfirmButtons(
                textForLeftButton = textForLeftButton,
                onClickForLeftButton = onLeftButtonClick,
                enabledForLeftButton = enabledForLeftButton,
                textForRightButton = textForRightButton,
                onClickForRightButton = onRightButtonClick,
                enabledForRightButton = enabledForRightButton
            )
        }
    )
}
