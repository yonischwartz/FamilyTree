package com.example.familytree.ui.theme.dialogs

import androidx.compose.runtime.Composable
import com.example.familytree.ui.theme.HebrewText

/**
 * A dialog that informs the user that new family members must be related to an existing member.
 *
 * @param onConfirm Callback function triggered when the user presses the "Next" button.
 * @param onDismiss Callback function triggered when the user presses the "Cancel" button or dismisses the dialog.
 */
@Composable
fun NewMemberMustBeRelatedDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    val title: String = HebrewText.ADDING_NEW_MEMBER
    val text: String = HebrewText.NEW_FAMILY_MEMBERS_MUST_BE_RELATED_TO_AN_EXISTING_MEMBER

    DialogWithTwoButtons(
        title = title,
        text = text,
        onClickForLeft = onConfirm,
        textForLeft = HebrewText.NEXT,
        onClickForRight = onDismiss,
        textForRight = HebrewText.CANCEL
    )
}


