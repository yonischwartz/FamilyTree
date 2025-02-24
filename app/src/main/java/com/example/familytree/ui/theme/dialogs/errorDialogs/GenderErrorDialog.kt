package com.example.familytree.ui.theme.dialogs.errorDialogs

import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.DialogWithOneButton

/**
 * Displays an error dialog when a gender conflict arises while adding a new member to the family tree.
 *
 * @param onDismiss Lambda function to handle the dismissal of the dialog.
 * @param relation The relationship type between the new member and the existing member.
 * @param newMember The FamilyMember object representing the new member being added.
 * @param existingMember The FamilyMember object representing the existing member in the family tree.
 */
@Composable
fun GenderErrorDialog(
    onDismiss: () -> Unit,
    relation: Relations,
    newMember: FamilyMember,
    existingMember: FamilyMember
) {

    val pronouns = if (newMember.getGender()) HebrewText.HE else HebrewText.SHE
    val genderDescription = if (newMember.getGender()) HebrewText.MALE else HebrewText.FEMALE
    val text: String =  HebrewText.CAN_NOT_ADD + newMember.getFullName() +
                        "${HebrewText.AS}${relation.displayAsConnections()}" +
                        "${existingMember.getFullName()}, " +
                        "${HebrewText.BECAUSE}${pronouns} ${genderDescription}."

    DialogWithOneButton(
        title = HebrewText.ERROR_ADDING_MEMBER,
        text = text,
        onDismiss = onDismiss
    )
}