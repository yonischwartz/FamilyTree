package com.example.familytree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.dialogs.DialogWithOneButton

/**
 * Displays a dialog indicating that no family members were found for the given search query.
 *
 * @param searchQuery The search query entered by the user.
 * @param onDismiss A callback function to handle the dismissal of the dialog.
 */
@Composable
fun NoMembersFoundDialog(
    searchQuery: String,
    onDismiss: () -> Unit,
) {
    DialogWithOneButton(
        title = HebrewText.NO_MEMBERS_FOUND,
        text = "${HebrewText.NO_MEMBERS_FOUND} ${HebrewText.WITH_STRING} $searchQuery",
        onClick = onDismiss,
    )
}
