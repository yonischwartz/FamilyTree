package com.yoniSchwartz.YBMTree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.dialogs.DialogWithButtons

/**
 * Displays a dialog indicating that a new family member was successfully added.
 *
 * @param newMember The [FamilyMember] that was added.
 * @param onDismiss A callback function invoked when the dialog is dismissed.
 */
@Composable
fun MemberAddedSuccessfullyDialog(
    newMember: FamilyMember,
    onDismiss: () -> Unit
) {
    val text = newMember.getFullName() + " " + HebrewText.WAS_ADDED_SUCCESSFULLY
    val title = HebrewText.SUCCESS_ADDING_MEMBER

    DialogWithButtons(
        title = title,
        text = text,
        onLeftButtonClick = onDismiss
    )
}