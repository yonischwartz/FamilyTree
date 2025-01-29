package com.example.familytree.ui.theme.dialogs
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SameMemberMarriageErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "שגיאה בהכנסת בן משפחה חדש!")
        },
        text = {
            Text(text = "זוג נשוי אינם יכולים להיות מאותו המגדר")
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