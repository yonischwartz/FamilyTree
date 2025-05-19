package com.yoniSchwartz.YBMTree.ui.dialogs.errorAndSuccessDialogs

import androidx.compose.runtime.Composable
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.Relations
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.dialogs.DialogWithButtons

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

    val gender = newMember.getGender()

    val pronouns = if (gender) HebrewText.HE else HebrewText.SHE
    val genderDescription = if (newMember.getGender()) HebrewText.MALE else HebrewText.FEMALE
    val text: String =  HebrewText.CAN_NOT_ADD + newMember.getFullName() +
                        "${HebrewText.AS}${relation.displayAsRelation(gender)}" +
                        "${existingMember.getFullName()}, " +
                        "${HebrewText.BECAUSE}${pronouns} ${genderDescription}."

    DialogWithButtons(
        title = HebrewText.ERROR_ADDING_MEMBER,
        text = text,
        onLeftButtonClick = onDismiss
    )
}