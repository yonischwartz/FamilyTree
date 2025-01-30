package com.example.familytree.ui.theme.dialogs
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.familytree.ui.theme.HebrewText

@Composable
fun SameMemberMarriageErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = HebrewText.ERROR_ADDING_MEMBER)
        },
        text = {
            Text(text = HebrewText.MARRIED_COUPLE_CAN_NOT_BE_OF_SAME_SEX)
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