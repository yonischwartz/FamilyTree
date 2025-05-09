package com.example.familytree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.dialogs.DialogWithButtons

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