package com.example.familytree.ui.theme.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
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

    val genderWord = if (newMember.getGender()) "הוא" else "היא"
    val genderDescription = if (newMember.getGender()) "זכר" else "נקבה"

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "שגיאה בהכנסת בן משפחה חדש!")
        },
        text = {
            Text(
                text = "לא ניתן להוסיף את ${newMember.getFullName()} כ${relation.displayName()}" +
                        "${existingMember.getFullName()}, " +
                        "מכיוון ש${genderWord} ${genderDescription}."
            )
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("OK")
            }
        }
    )
}