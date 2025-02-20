package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.familytree.ui.theme.HebrewText

@Composable
fun GenericMessageDialogWithTwoButtons(
    title: String,
    text: String,
    onClick: () -> Unit,
    textForOnClick: String,
    onDismiss: () -> Unit,
    textForOnDismiss: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
        },
        text = { Text(text, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // First button
                Button(onClick = onClick) {
                    Text(textForOnClick)
                }

                // Second button
                Button(onClick = onDismiss) {
                    Text(textForOnDismiss)
                    }
            }
        }
    )
}