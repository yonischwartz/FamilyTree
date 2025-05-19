package com.yoniSchwartz.YBMTree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.Relations
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.dialogs.DialogWithButtons

/**
 * Displays an error dialog when attempting to add a family connection that already exists.
 *
 * @param onDismiss A lambda function called when the dialog is dismissed.
 * @param relation The relation type that caused the error (e.g., father, son, marriage).
 * @param existingMember The existing family member that already has the specified relation.
 */
@Composable
fun MoreThanOneConnectionErrorDialog(
    onDismiss: () -> Unit,
    relation: Relations,
    existingMember: FamilyMember
) {


    val text: String = HebrewText.TO +
                        "${existingMember.getFullName()} " +
                        HebrewText.EXISTS_ALREADY_FAMILY_RELATION_OF_TYPE +
                        "${relation.name.lowercase()}."

    DialogWithButtons(
        title = HebrewText.ERROR_ADDING_MEMBER,
        text = text,
        onLeftButtonClick = onDismiss
    )
}