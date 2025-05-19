package com.yoniSchwartz.YBMTree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.dialogs.DialogWithButtons

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
