package com.example.familytree.ui.theme.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.DialogWithOneButton


/**
 * A dialog displayed when there are not enough members in the family tree to find a connection.
 *
 * @param onDismiss A callback invoked when the dialog is dismissed.
 */
@Composable
fun NotEnoughMembersInTreeDialog(onDismiss: () -> Unit) {
    DialogWithOneButton(
        title = HebrewText.NOT_ENOUGH_MEMBERS_IN_TREE,
        text = HebrewText.IN_ORDER_TO_FIND_CONNECTION_BETWEEN_MEMBERS_TREE_MUST_HAVE_AT_LEAST_TWO_MEMBERS,
        onDismiss = onDismiss,
    )
}