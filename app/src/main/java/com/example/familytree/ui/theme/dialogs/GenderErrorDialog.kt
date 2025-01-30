package com.example.familytree.ui.theme.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.displayName

/**
 * Composable function to display a dialog for gender role errors.
 *
 * @param onDismiss A lambda function that gets executed when the dialog is dismissed.
 */
@Composable
fun GenderErrorDialog(
    onDismiss: () -> Unit,
    relation: Relations,
    newMember: FamilyMember,
    existingMember: FamilyMember
) {

    val genderWord = if (newMember.getGender()) HebrewText.HE else HebrewText.SHE
    val genderDescription = if (newMember.getGender()) HebrewText.MALE else HebrewText.FEMALE

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = HebrewText.ERROR_ADDING_MEMBER)
        },
        text = {
            Text(
                text = HebrewText.CAN_NOT_ADD + "${newMember.getFullName()} ${HebrewText.AS}${relation.displayName()}" +
                        "${existingMember.getFullName()}, " +
                        "${HebrewText.BECAUSE}${genderWord} ${genderDescription}."
            )
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(HebrewText.OK)
            }
        }
    )
}