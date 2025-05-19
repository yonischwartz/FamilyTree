package com.yoniSchwartz.YBMTree.ui.dialogs.errorAndSuccessDialogs
import androidx.compose.runtime.Composable
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.dialogs.DialogWithButtons

/**
 * Displays an error dialog when attempting to create a marriage connection
 * between two family members of the same sex.
 *
 * @param onDismiss A callback function to be invoked when the dialog is dismissed.
 */
@Composable
fun SameMemberMarriageErrorDialog(onDismiss: () -> Unit) {

    val title: String = HebrewText.ERROR_ADDING_MEMBER
    val text: String = HebrewText.MARRIED_COUPLE_CAN_NOT_BE_OF_SAME_SEX

    DialogWithButtons(
        title = title,
        text = text,
        onLeftButtonClick = onDismiss
    )
}