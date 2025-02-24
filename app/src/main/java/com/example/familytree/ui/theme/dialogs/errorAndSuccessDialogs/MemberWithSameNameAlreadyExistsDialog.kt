package com.example.familytree.ui.theme.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.DialogWithTwoButtons

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

    DialogWithTwoButtons(
        title = title,
        text = text,
        onClickForLeft = onApprove,
        textForLeft = HebrewText.YES,
        onClickForRight = onDismiss,
        textForRight = HebrewText.NO
    )
}
