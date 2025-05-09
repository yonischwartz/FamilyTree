package com.example.familytree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.dialogs.DialogWithButtons
import com.example.familytree.ui.dialogs.DialogWithTwoButtons

/**
 * Displays a dialog when a member with the same name already exists in the family tree.
 *
 * @param onApprove Called when the user approves adding the member anyway.
 * @param onDismiss Called when the user dismisses the dialog.
 */
@Composable
fun MemberWithSameNameAlreadyExistsDialog(
    onApprove: () -> Unit,
    onDismiss: () -> Unit
) {
    val title: String = HebrewText.MEMBER_ALREADY_EXISTS
    val text: String = HebrewText.DO_YOU_WANT_TO_ADD_ANYWAY

    DialogWithButtons(
        title = title,
        text = text,
        onLeftButtonClick = onApprove,
        textForLeftButton = HebrewText.YES,
        onRightButtonClick = onDismiss,
        textForRightButton = HebrewText.NO,
        onDismiss = onDismiss
    )
}
